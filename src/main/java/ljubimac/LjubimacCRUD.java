
package ljubimac;

/**
 *
 * @author Amel Džanić
 */
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import klijent.KlijentCRUD;
import udomljavanje.UdomljavanjeCRUD;

public class LjubimacCRUD extends korisni.Kontroler{
    public static final String[] tip= {"pas", "mačka"};
    private Ljubimac ljubimac = new Ljubimac();
    List<Ljubimac> ljubimci = new ArrayList<>();
    private udomljavanje.UdomljavanjeCRUD uc;
    private final klijent.KlijentCRUD kc = new KlijentCRUD();
    
    /**
     *
     * @throws SQLException
     */
    public LjubimacCRUD() throws SQLException {
        createTable();
    }
    
    /**
     *
     * @param ljubimac
     * @throws SQLException
     */
    public void dodajLjubimca(Ljubimac ljubimac) throws SQLException {
        String sql = String.format("INSERT INTO ljubimac (ime, vrsta, starost, status) " +
        "VALUES ('%s', '%s', '%s', '%s')",
        escapeSql(ljubimac.getIme()),
        escapeSql(ljubimac.getVrsta()),
        escapeSql(ljubimac.getStarost()),
        escapeSql(ljubimac.getStatus()));
        
        InsDelUpd(sql);
    }
    /**
     * Dobavlja list ljubimaca zavisno od parametara
     * @param opcija govori koju vrstu ljubimca dobavlja(0 sve, 1 pse, 2 mascke)
     * @param ime ime ili pocetak imena ljubimca
     * @param opcija2 govori da li su 1-slobodan 2-rezervisan 3-udomljen
     * ili 0 ako nam to netreba ljubimci koji se dobavljaju
     * @return list ljubimaca
     * @throws SQLException
     */
 public List<Ljubimac> dobaviSveLjubimce(int opcija, String ime, int opcija2) throws SQLException {
    List<Ljubimac> ljubimci = new ArrayList<>();
    String sql = "";
    
    // 1. Izgradnja osnovnog SQL upita sa placeholderima (?)
    String uvjetVrsta = switch (opcija) {
        case 1 -> "vrsta = 'pas' AND ";
        case 2 -> "vrsta = 'mačka' AND ";
        default -> ""; 
    };

    String uvjetStatus = switch (opcija2) {
        case 1 -> " AND status = 'SLOBODAN'";
        case 2 -> " AND status = 'REZERVISAN'";
        case 3 -> " AND status = 'UDOMLJEN'"  ;  
        default -> "";
    };
            
            //(opcija2 == 0) ? "status = 'SLOBODAN'" : "status = 'REZERVISAN'";
    
    // Finalni SQL sa '?' za ime (sigurnost!)
    sql = "SELECT DISTINCT * FROM ljubimac WHERE " + uvjetVrsta + "ime LIKE ?  " + uvjetStatus;

    // 2. Try-with-resources za automatsko zatvaranje
    try (Connection konekcija = getKon();
         PreparedStatement pstmt = konekcija.prepareStatement(sql)) {
        
        // Postavljanje parametra za LIKE (ime + postotak)
        pstmt.setString(1, ime + "%");
        
        try (ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Ljubimac ljub = new Ljubimac(
                    rs.getInt("id"),
                    rs.getString("ime"),
                    rs.getString("vrsta"),
                    rs.getString("starost"),
                    rs.getString("status")
                );
                
                // Ovdje po potrebi možeš dohvatiti sliku ako je BLOB
                // byte[] slika = rs.getBytes("slika_kolona");
                
                ljubimci.add(ljub);
            }
        }
    } catch (SQLException e) {
        //System.err.println("Greška pri dohvatu ljubimaca: " + e.getMessage());
        throw new SQLException(e);
    }

    return ljubimci;
}


    /**
     *
     * @param id
     * @return
     * @throws SQLException
     */
    public Ljubimac dobaviLjubimcaPoId(int id) throws SQLException {
        String sql = "SELECT * FROM ljubimac WHERE id = ?";
        Ljubimac ljub = null;

        // Try-with-resources osigurava zatvaranje čak i ako postaviKorisnike baci error
        try (Connection konekcija = getKon();
             PreparedStatement pstmt = konekcija.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    ljub = new Ljubimac(
                        rs.getInt("id"),
                        rs.getString("ime"),
                        rs.getString("vrsta"),
                        rs.getString("starost"),
                        rs.getString("status")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Greška kod dohvata ljubimca po ID-u: " + e.getMessage());
        }

    // Dodatnu logiku za klijente pokrećemo samo ako je ljubimac pronađen
    if (ljub != null) {
        postaviKorisnikeDatogLjubimca(id);
    }

    return ljub;
}


    /**
     *
     * @param ljubimac
     * @throws SQLException
     */
    public void azurirajLjubimca(Ljubimac ljubimac) throws SQLException {
        String sql = String.format("UPDATE ljubimac SET " +
        "ime = '%s', " +
        "vrsta = '%s', " +
        "starost = '%s', " +
        "status = '%s' " +
        "WHERE id = %d",
        escapeSql(ljubimac.getIme()),
        escapeSql(ljubimac.getVrsta()),
        escapeSql(ljubimac.getStarost()),
        escapeSql(ljubimac.getStatus()),
        ljubimac.getId());        
        InsDelUpd(sql);
    }

    // Delete
    public void obrisiLjubimca(int id) throws SQLException {
        String sql = "DELETE FROM ljubimac WHERE id = " + id;        
        InsDelUpd(sql);
    } 
    
    /**
     *
     * @throws SQLException
     */
    public final void createTable() throws SQLException  {
        String sql ="CREATE TABLE IF NOT EXISTS ljubimac (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "ime TEXT NOT NULL, " +
                "vrsta TEXT CHECK(vrsta IN ('pas', 'mačka')) NOT NULL, " +
                "starost TEXT," +
                "status TEXT CHECK(status IN ('DA', 'NE')) NOT NULL DEFAULT 'NE')" ;       
        InsDelUpd(sql);       
    }   
    
    /**
     *
     * @param id
     * @throws SQLException
     */
    public void postaviKorisnikeDatogLjubimca(int id) throws SQLException{
        uc=new UdomljavanjeCRUD();        
        ArrayList<Integer> korisnikID = uc.vratiIdKlijenata(id);        
        ljubimac.getKorisnici().clear();
        for (Integer integer : korisnikID) {            
             ljubimac.getKorisnici().add(kc.dobaviKlijentaPoId(integer));
        }
    }
    
    /**
     *
     * @param ljub
     * @throws SQLException
     */
    public void ukloniUdomljavanje(Ljubimac ljub) throws SQLException{
        ljub.setStatus("NE");
        azurirajLjubimca(ljubimac);
    }
    public Ljubimac getLjubimac() {
        return ljubimac;
    }
    public void setLjubimac(Ljubimac ljubimac) {
        this.ljubimac = ljubimac;
    }
    
   
   
    
    
}
