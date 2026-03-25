
package udomljavanje;

/**
 * Klasa koja je zadužena za CRUD sa tabelom udomljavanje
 * @author Amel Džanić
 */
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import ljubimac.LjubimacCRUD;

public class UdomljavanjeCRUD extends korisni.Kontroler{
    private klijent.KlijentCRUD kk;
    private ljubimac.LjubimacCRUD lk;
    
    
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    /**
     * Konstruktor kreira tabelu ukoliko ne postoji     * 
     * @throws SQLException ukoliko se desi neka greška sa DB
     */
    public UdomljavanjeCRUD() throws SQLException {
        createTable();       
    }
    /**
     * Metoda koja unosi novi slog u tabelu
     * @param udomljen prestavlja objekat 
     * @throws SQLException
     */
    public void dodajRelaciju(Udomljen udomljen) throws SQLException {
        String sql = String.format("INSERT INTO udomljavanje "
                + "(idKlijenti, idLjubimac, datumUdomljavanja, status) " +
                "VALUES (%d, %d, '%s', '%s')",
                udomljen.getIdKlijenti(),
                udomljen.getIdLjubimac(),
                dateFormat.format(udomljen.getDatumUdomljavanja()),
                udomljen.getStatus()
        );        
        InsDelUpd(sql);
    }

    // Read (sva udomljavanja)
    public List<Udomljen> dobaviSvaUdomljavanja() throws SQLException, ParseException {
        return dobaviSve(0);
    } 
    public List<Udomljen> dobaviSveRezervacije() throws SQLException, ParseException{
        return dobaviSve(1);
    }
    /**
     * Metoda koja dobavlja sva udomljavanja ne koristim je
     * @return
     * @throws SQLException
     * @throws ParseException
     */
    private List<Udomljen> dobaviSve(int opcija) throws SQLException, ParseException {
        List<Udomljen> udomljavanja = new ArrayList<>();
        String sql;
        if (opcija == 0)
            sql = "SELECT * FROM udomljavanje WHERE rezervacija=0"; 
        else 
            sql = "SELECT * FROM udomljavanje WHERE rezervacija=1";
        Statement st = getKon().createStatement();        
        ResultSet resultSet = st.executeQuery(sql);
        Udomljen udomljen = null;
            
        while (resultSet.next()) {
            udomljen = new Udomljen(
                resultSet.getInt("idKlijenti"),
                resultSet.getInt("idLjubimac"),
                dateFormat.parse(resultSet.getString("datumUdomljavanja"))
               //lk.dobaviLjubimcaPoId(resultSet.getInt("idLjubimac")),
              // kk.dobaviKlijentaPoId(resultSet.getInt("idKlijenti"))                        
            );
            udomljavanja.add(udomljen);
            st.close();
            zatvoriKonekciju();
            udomljen.setKlijent(kk.dobaviKlijentaPoId(udomljen.getIdKlijenti()));
            udomljen.setLjub(lk.dobaviLjubimcaPoId(udomljen.getIdLjubimac()));
        }       
        return udomljavanja;
    }

    
    public Udomljen dobaviUdomljavanje (int idKlijenti, int idLjubimac) throws SQLException{
        return dobaviRelaciju(idKlijenti, idLjubimac, 0);
    }
    public Udomljen dobaviRezervaciju (int idKlijenti, int idLjubimac) throws SQLException{
        return dobaviRelaciju(idKlijenti, idLjubimac, 1);
    }
   
    private Udomljen dobaviRelaciju(int idKlijenti, int idLjubimac, int opcija) throws SQLException {
        String sql = "SELECT * FROM udomljavanje WHERE idKlijenti = ? AND idLjubimac = ?";
        String dodatak;
        if (opcija==0) dodatak = " WHERE rezervacija=0";
        else dodatak = " WHERE rezervacija=1";
        sql=sql + dodatak;
        Udomljen udomljen = null;

        try (Connection konekcija = getKon();
             PreparedStatement pstmt = konekcija.prepareStatement(sql)) {

            pstmt.setInt(1, idKlijenti);
            pstmt.setInt(2, idLjubimac);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    // Kreiramo objekt samo ako smo pronašli zapis u bazi
                    udomljen = new Udomljen();
                    udomljen.setIdKlijenti(rs.getInt("idKlijenti"));
                    udomljen.setIdLjubimac(rs.getInt("idLjubimac"));

                    // Sigurno parsiranje datuma (SQLite sprema datume kao Stringove)
                    try {
                        udomljen.setDatumUdomljavanja(dateFormat.parse(rs.getString("datumUdomljavanja")));
                    } catch (ParseException e) {
                        System.err.println("Greška kod formata datuma: " + e.getMessage());
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Database greška: " + e.getMessage());
        }

        // Dodatno popunjavanje objekata radimo SAMO ako udomljavanje postoji (nije null)
        if (udomljen != null) {
            udomljen.setKlijent(kk.dobaviKlijentaPoId(udomljen.getIdKlijenti()));
            udomljen.setLjub(lk.dobaviLjubimcaPoId(udomljen.getIdLjubimac()));
        }

        return udomljen;
    }


    // Update

    /**
     *
     * @param udomljen
     * @throws SQLException
     */
    public void azurirajUdomljavanje(Udomljen udomljen) throws SQLException {
        String sql = String.format("UPDATE udomljavanje SET " +
                "datumUdomljavanja = '%s' " +
                "WHERE idKlijenti = %d AND idLjubimac = %d",
                dateFormat.format(udomljen.getDatumUdomljavanja()),
                udomljen.getIdKlijenti(),
                udomljen.getIdLjubimac());
        
        InsDelUpd(sql);
    }
    /**
     *
     * @param idKlijenti
     * @param idLjubimac
     * @throws SQLException
     */
    public void obrisiUdomljavanje(int idKlijenti, int idLjubimac) throws SQLException {
        //Treba dodati logiku da se u tabeli ljubimac prebaci na NE
        String sql = String.format("DELETE FROM udomljavanje WHERE idKlijenti = %d OR idLjubimac = %d",
                idKlijenti, idLjubimac);
        ljubimac.LjubimacCRUD lc = new LjubimacCRUD();
        ArrayList<Integer> ljubimci = vratiIdLjubimaca(idKlijenti);
        for (Integer integer : ljubimci) {
            lc.ukloniUdomljavanje(lc.dobaviLjubimcaPoId(integer));
        }
        InsDelUpd(sql);
    }
    
    /**
     *
     * @throws SQLException
     */
    public final void createTable() throws SQLException  {
        String sql = "CREATE TABLE IF NOT EXISTS udomljavanje (" +
                "idKlijenti INTEGER NOT NULL, " +
                "idLjubimac INTEGER NOT NULL, " +
                "datumUdomljavanja TEXT NOT NULL, " +
                "PRIMARY KEY (idKlijenti, idLjubimac), " +
                "FOREIGN KEY (idKlijenti) REFERENCES klijent(id), " +
                "FOREIGN KEY (idLjubimac) REFERENCES ljubimac(id))"  ;     
        InsDelUpd(sql);       
    }
    
    /**
     *
     * @param id
     * @return
     * @throws SQLException
     */
   public ArrayList<Integer> vratiIdKlijenata(int id) {
    ArrayList<Integer> idKlijenti = new ArrayList<>();
    String sql = "SELECT idKlijenti FROM udomljavanje WHERE idLjubimac = ?";

    // Automatsko zatvaranje Connection, PreparedStatement i ResultSet
    try (Connection kon = getKon();
         PreparedStatement pstmt = kon.prepareStatement(sql)) {
        pstmt.setInt(1, id);
        try (ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                idKlijenti.add(rs.getInt("idKlijenti"));
            }
        }
    } catch (SQLException e) {
        System.err.println("Greška kod dohvata ID-ova klijenata: " + e.getMessage());
    }
    return idKlijenti;
}


    /**
     *
     * @param id
     * @return
     * @throws SQLException
     */
    public ArrayList<Integer> vratiIdLjubimaca(int id) {
        ArrayList<Integer> idLjubimci = new ArrayList<>();
        String sql = "SELECT idLjubimac FROM udomljavanje WHERE idKlijenti = ?";

        // Automatsko zatvaranje resursa (Connection, PreparedStatement, ResultSet)
        try (Connection konekcija = getKon();
             PreparedStatement pstmt = konekcija.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    // Direktno dodavanje u listu
                    idLjubimci.add(rs.getInt("idLjubimac"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Greška kod dohvata ID-ova ljubimaca: " + e.getMessage());
        }

    return idLjubimci;
}

   
}
