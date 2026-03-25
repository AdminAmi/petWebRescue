package korisnik;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import korisni.MyLogger;

/**
 *
 * @author Amel Dzanic
 */
public class CRUDKorisnik extends korisni.Kontroler{
    Korisnik Korisnik = new Korisnik();

    public CRUDKorisnik() throws SQLException {        
        createTable();
    }
    
    /**
     * Ova metoda kreira tabelu u bazi podataka
     * @throws SQLException
     */
    public final void createTable() throws SQLException  {
        String sql = "CREATE TABLE IF NOT EXISTS korisnik (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "user TEXT NOT NULL UNIQUE," +
                "pass TEXT NOT NULL," +
                "ime TEXT NOT NULL," +
                "prezime TEXT NOT NULL, "
                + "tip TEXT)";       
        InsDelUpd(sql);       
    }
     
      /**
     *Metoda unesiKorisnika unosi sve podatke o novom korisniku sistema
     * @param temp predstavlja objekat korisnik koji se unosi u bazu
     * @throws SQLException ukoliko nije bilo uspjesno povezivanje sa bazom podataka
     */
    public void UnesiKorisnika(Korisnik temp) throws SQLException {
        String sql = "INSERT INTO korisnik (user,pass,ime,prezime,tip)"
                + " VALUES ('" + temp.getUser()+"','"
                + temp.getPass()+"','"+temp.getIme()+"','"
                + temp.getPrezime()+"','"+temp.getTip()+ "' )";  
        
        InsDelUpd(sql);       
    }
    /**
     * Metoda <code>azurirajKorisnika</code> vrši azuiriranje podataka korisnka
     * i to ime i prezime korisnika
     * @param temp objekat korisnika nad kojim se vrsi azuriranje
     * @throws SQLException ukoliko nije bilo uspjesno povezivanje sa bazom podataka
     */
    public void azurirajKorisnika(Korisnik temp) throws SQLException {
        String sql = "UPDATE korisnik SET ime = '"+ 
                    temp.getIme()+ "', prezime = '"+
                    temp.getPrezime()+"' WHERE id = " + temp.getId();        
        InsDelUpd(sql);
    }
    /**
     * Metoda koja vrši izmjenu korisnicke sifre
     * @param temp objekat korisnika koji mjenja sifru
     * @param New nova sifra
     * @param Rep ponovni unos nove sifre
     * @throws SQLException ukoliko nije bilo uspjesno povezivanje sa bazom podataka
     */
    public void promjenaPassworda(Korisnik temp, String New, String Rep) throws SQLException {
        String sql = "UPDATE korisnik SET pass ='"+New+"' WHERE id =" + temp.getId();
        InsDelUpd(sql);        
    }
    
    /**
     *
     * @param id
     * @throws SQLException
     */
    public void obrisiKorisnika(int id) throws SQLException {
        String sql = "DELETE FROM korisnik WHERE id = " + id;        
        InsDelUpd(sql);
    } 
    /**
     * Metoda vraca objekat korisnika koji ima trazeni ID
     * @param id korisnika koji se trazi
     * @return objekat korisnik koji ima taj ID, ukoliko ga ne nadje vraca null
     */
    public Korisnik VratiKorisnikaPoID(int id) {
    String sql = "SELECT * FROM korisnik WHERE id = ?";
    // Bolje je inicijalizirati na null da znaš ako korisnik ne postoji
    Korisnik kor = null;
    // Svi resursi u zagradama se automatski zatvaraju 
    // (Connection, PreparedStatement, ResultSet)
    try (Connection konekcija = getKon();
        PreparedStatement pstmt = konekcija.prepareStatement(sql)) {        
        pstmt.setInt(1, id); // Sigurno postavljanje ID-a        
        try (ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) { // Koristimo 'if' jer tražimo jednog korisnika po ID-u
                kor = new Korisnik();
                kor.setId(rs.getInt("id"));
                kor.setUser(rs.getString("user"));
                kor.setPass(rs.getString("pass"));
                kor.setIme(rs.getString("ime"));
                kor.setPrezime(rs.getString("prezime"));
                kor.setTip(rs.getString("tip"));
                // Ako imaš sliku u bazi, ovdje bi je dodao:
                // kor.setSlika(rs.getBytes("photo")); 
            }
        }
    } catch (SQLException e) {
        System.err.println("Greška prilikom dohvata korisnika: " + e.getMessage());
        // Ovdje možeš baciti vlastiti exception ili logirati grešku
    }

    return kor; 
}

    
    /**
     * metoda login je zaduzena za logovanje na sistem ukoliko su uneseni parametri tacni
     * pored toga ucitava iz baze sve podatke o zadanom korisniku
     * @param log predstavlja korisnicko ime
     * @param pass predstavlja korisnicku sifru
     * @return true ukoliko su podaci ispravni
     * @throws SQLException ukoliko nije bilo uspjesno povezivanje sa bazom podataka
     */
    public boolean login(String log, String pass) throws SQLException {        
        boolean zastavica = false;
        String sql = "SELECT * FROM korisnik WHERE user = ? AND pass = ? ";
        try (Connection konekcija = getKon();
            PreparedStatement pstmt = konekcija.prepareStatement(sql)) { 
            pstmt.setString(1, log);
            pstmt.setString(2, pass);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) 
                {
                    Korisnik.setId(rs.getInt("id"));
                    Korisnik.setIme(rs.getString("ime"));
                    Korisnik.setUser(rs.getString("user"));
                    Korisnik.setPass(rs.getString("pass"));
                    Korisnik.setPrezime(rs.getString("prezime"));
                    Korisnik.setTip(rs.getString("tip"));
                    zastavica = true;
                }
            }
        }
        return zastavica;
    }

   
    
    /**
     *
     * @param uvjet
     * @return
     */
    public ArrayList<Korisnik> vratiKojiZadovoljavajuUvjet(String uvjet) {
    ArrayList<Korisnik> rezultat = new ArrayList<>();
    // Koristimo upitnik za parametar kako bismo spriječili SQL Injection
    String sql = "SELECT * FROM korisnik WHERE ime LIKE ?";
    // Automatsko zatvaranje konekcije i statement-a
    try (Connection konekcija = getKon();
         PreparedStatement pstmt = konekcija.prepareStatement(sql)) {        
        // Postavljamo uvjet: string + postotak za LIKE operator
        pstmt.setString(1, uvjet + "%");        
        try (ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Korisnik kor = new Korisnik();
                kor.setId(rs.getInt("id"));
                kor.setIme(rs.getString("ime"));
                kor.setUser(rs.getString("user"));
                kor.setPass(rs.getString("pass"));
                kor.setPrezime(rs.getString("prezime"));
                kor.setTip(rs.getString("tip"));
                rezultat.add(kor);
            }
        }
    } catch (SQLException e) {
        // Ispis greške u konzolu (u produkciji koristi Logger)
        System.err.println("Greška u pretrazi korisnika: " + e.getMessage());
    }
    
    // Čak i ako se dogodi greška, vraćamo (praznu) listu umjesto null, 
    // što je bolja praksa u Javi (izbjegava NullPointerException).
    return rezultat;
}


    /**
     *
     * @return
     * @throws SQLException
     */
    public ArrayList<Korisnik> vratiSve() throws SQLException {        
        ArrayList<Korisnik> rezultat = new ArrayList();        
        String sql =  "SELECT * FROM korisnik " ;  
         // Automatsko zatvaranje konekcije i statement-a
        try (Connection konekcija = getKon();
            PreparedStatement pstmt = konekcija.prepareStatement(sql)) {
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Korisnik kor = new Korisnik();
                    kor.setId(rs.getInt("id"));
                    kor.setIme(rs.getString("ime"));
                    kor.setUser(rs.getString("user"));
                    kor.setPass(rs.getString("pass"));
                    kor.setPrezime(rs.getString("prezime"));
                    kor.setTip(rs.getString("tip"));
                    rezultat.add(kor);
                }
            }
        } catch (SQLException e) {
            // Ispis greške u konzolu (u produkciji koristi Logger)
            System.err.println("Greška u pretrazi korisnika: " + e.getMessage());
        }
        return rezultat;
    }
    
    public Korisnik getKorisnik() {
        return Korisnik;
    }

    public void setKorisnik(Korisnik Korisnik) {
        this.Korisnik = Korisnik;
    }

}
