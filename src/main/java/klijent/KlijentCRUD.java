
package klijent;

/**
 *
 * @author Amel Džanić
 */
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import ljubimac.LjubimacCRUD;
import udomljavanje.UdomljavanjeCRUD;

public class KlijentCRUD extends korisni.Kontroler{
    private Klijent klijent;

    /**
     *
     * @throws SQLException
     */
    public KlijentCRUD() throws SQLException {
        createTable();
    }

    /**
     *
     * @param klijent
     * @throws SQLException
     */
    public void dodajKlijenta(Klijent klijent) throws SQLException {
        String sql = String.format("INSERT INTO klijent (ime_prezime, adresa, broj_telefona) " +
                "VALUES ('%s', '%s', '%s')",
                escapeSql(klijent.getImePrezime()),
                escapeSql(klijent.getAdresa()),
                escapeSql(klijent.getBrojTelefona()));        
        InsDelUpd(sql);
    }

    /**
     *
     * @return
     * @throws SQLException
     */
    public List<Klijent> dobaviSveKlijente() {
    List<Klijent> klijenti = new ArrayList<>();
    String sql = "SELECT * FROM klijent";

    // Try-with-resources automatski zatvara Connection, PreparedStatement i ResultSet
    try (Connection k = getKon();
         PreparedStatement pstmt = k.prepareStatement(sql);
         ResultSet rs = pstmt.executeQuery()) {
        
        while (rs.next()) {
            Klijent klijent1 = new Klijent(
                rs.getInt("id"),
                rs.getString("ime_prezime"),
                rs.getString("adresa"),
                rs.getString("broj_telefona")
            );
            
            // Ako klijent ima sliku u bazi, ovdje je dohvaćaš:
            // klijent.setSlika(rs.getBytes("slika_klijenta"));
            
            klijenti.add(klijent1);
        }
    } catch (SQLException e) {
        System.err.println("Greška prilikom dohvata svih klijenata: " + e.getMessage());
    }

    return klijenti;
}

     /**
     *
     * @param imePrezime
     * @return
     * @throws SQLException
     */
   public List<Klijent> dobaviSveKlijenteUvijet(String imePrezime) {
    List<Klijent> klijenti = new ArrayList<>();
    String sql = "SELECT * FROM klijent WHERE ime_prezime LIKE ?";

    // Try-with-resources automatski zatvara Connection i PreparedStatement
    try (Connection konekcija = getKon();
         PreparedStatement pstmt = konekcija.prepareStatement(sql)) {
        
        // Postavljanje parametra sigurno (ime + %)
        pstmt.setString(1, imePrezime + "%");
        
        try (ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Klijent k = new Klijent(
                    rs.getInt("id"),
                    rs.getString("ime_prezime"),
                    rs.getString("adresa"),
                    rs.getString("broj_telefona")
                );
                klijenti.add(k);
            }
        }
    } catch (SQLException e) {
        System.err.println("Greška kod filtriranja klijenata: " + e.getMessage());
    }

    return klijenti;
}


    /**
     *
     * @param id
     * @return
     * @throws SQLException
     */
   public Klijent dobaviKlijentaPoId(int id) {
    String sql = "SELECT * FROM klijent WHERE id = ?";
    Klijent k = null;

    // Try-with-resources automatski zatvara Connection, PreparedStatement i ResultSet
    try (Connection kon = getKon();
         PreparedStatement pstmt = kon.prepareStatement(sql)) {
        
        pstmt.setInt(1, id);
        
        try (ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                k = new Klijent(
                    rs.getInt("id"),
                    rs.getString("ime_prezime"),
                    rs.getString("adresa"),
                    rs.getString("broj_telefona")
                );
                
                // Ako klijent ima sliku u bazi, ovdje je dohvaćaš:
                // k.setSlika(rs.getBytes("slika_kolona"));
            }
        }
    } catch (SQLException e) {
        System.err.println("Greška kod dohvata klijenta po ID-u: " + e.getMessage());
    }

    return k;
}


    /**
     *
     * @param idLjubimca
     * @return
     */
   public Klijent dobaviKlijentaVlasnika(int idLjubimca) {
    // Koristimo moderniji JOIN sintaks i upitnik za parametar
    String sql = "SELECT k.id, k.ime_prezime, k.adresa, k.broj_telefona " +
                 "FROM klijent k " +
                 "JOIN udomljavanje u ON k.id = u.idKlijenti " +
                 "WHERE u.idLjubimac = ?";
    
    Klijent k = null;

    try (Connection konekcija = getKon();
         PreparedStatement pstmt = konekcija.prepareStatement(sql)) {
        
        pstmt.setInt(1, idLjubimca);
        
        try (ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                k = new Klijent(
                    rs.getInt("id"),
                    rs.getString("ime_prezime"),
                    rs.getString("adresa"),
                    rs.getString("broj_telefona")
                );
            }
        }
    } catch (SQLException e) {
        System.err.println("Greška kod dohvata vlasnika ljubimca: " + e.getMessage());
    }

    return k;
}


    /**
     *
     * @param klijent
     * @throws SQLException
     */
    public void azurirajKlijenta(Klijent klijent) throws SQLException {
        String sql = String.format("UPDATE klijent SET " +
                "ime_prezime = '%s', " +
                "adresa = '%s', " +
                "broj_telefona = '%s' " +
                "WHERE id = %d",
                escapeSql(klijent.getImePrezime()),
                escapeSql(klijent.getAdresa()),
                escapeSql(klijent.getBrojTelefona()),
                klijent.getId());
        InsDelUpd(sql);
    }

    /**
     *
     * @param id
     * @throws SQLException
     */
    public void obrisiKlijenta(int id) throws SQLException {
        String sql = "DELETE FROM klijent WHERE id = " + id;        
        InsDelUpd(sql);
    }

    /**
     *
     * @throws SQLException
     */
    public final void createTable() throws SQLException  {
        String sql =  "CREATE TABLE IF NOT EXISTS klijent (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "ime_prezime TEXT NOT NULL UNIQUE, " +
                "adresa TEXT, " +
                "broj_telefona TEXT)" ;     
        InsDelUpd(sql);       
    }
    
    /**
     *
     * @param id
     * @throws SQLException
     */
    public void postaviLjubimceDatogKorisnika(int id) throws SQLException{
        UdomljavanjeCRUD uc=new UdomljavanjeCRUD();        
        ArrayList<Integer> ljubimacID = uc.vratiIdLjubimaca(id);
        ljubimac.LjubimacCRUD lc = new LjubimacCRUD();
        klijent.getLjubimci().clear();
        for (Integer integer : ljubimacID) {
             klijent.getLjubimci().add(lc.dobaviLjubimcaPoId(integer));
        }
    }
    public void setKlijent(Klijent klijent) {
        this.klijent = klijent;
    }
    
    public void setKlijent(Object klijent) {
        this.klijent =(Klijent) klijent;
    }

    public Klijent getKlijent() {
        return klijent;
    }
    
}
