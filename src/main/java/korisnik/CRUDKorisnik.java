package korisnik;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import korisni.MyLogger;

/**
 * Kontroler klasa za upravljanje podacima korisnika u bazi podataka.
 * Implementira CRUD operacije, login logiku i automatsko kreiranje tabela.
 * 
 * @author Amel Džanić
 * @version 2.0
 */
public class CRUDKorisnik extends korisni.Kontroler {
    
    /** Trenutno aktivni korisnik (npr. nakon logina). */
    private Korisnik korisnik = new Korisnik();

    /**
     * Inicijalizuje kontroler i osigurava postojanje tabele korisnik.
     * @throws SQLException Ako dođe do greške pri radu sa bazom.
     */
    public CRUDKorisnik() throws SQLException {        
        createTable();
    }
    
    /**
     * Pomoćna metoda (DRY) za mapiranje trenutnog reda ResultSet-a u objekat Korisnik.
     * 
     * @param rs ResultSet pozicioniran na red koji treba mapirati.
     * @return {@link Korisnik} objekat sa popunjenim podacima.
     * @throws SQLException Ako kolona ne postoji.
     */
    private Korisnik mapirajKorisnika(ResultSet rs) throws SQLException {
        Korisnik k = new Korisnik();
        k.setId(rs.getInt("id"));
        k.setUser(rs.getString("user"));
        k.setPass(rs.getString("pass"));
        k.setIme(rs.getString("ime"));
        k.setPrezime(rs.getString("prezime"));
        k.setTip(rs.getString("tip"));
        k.setAdresa(rs.getString("adresa"));
        k.setTelefon(rs.getString("telefon"));
        return k;
    }

    /**
     * Kreira tabelu 'korisnik' ako ista ne postoji.
     * @throws SQLException U slučaju SQL greške.
     */
    public final void createTable() throws SQLException  {
        String sql = "CREATE TABLE IF NOT EXISTS korisnik (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "user TEXT NOT NULL UNIQUE," +
                "pass TEXT NOT NULL," +
                "ime TEXT NOT NULL," +
                "prezime TEXT NOT NULL, " + 
                "tip TEXT, " + 
                "adresa TEXT, " +
                "telefon TEXT NOT NULL)";       
        try (Connection kon = getKone(); Statement st = kon.createStatement()) {
            st.execute(sql);
        }
    }
    
    /**
     * Unosi novog korisnika koristeći parametrizovani upit (sigurnost).
     * 
     * @param k Objekat korisnika za unos.
     * @throws SQLException Ako korisničko ime već postoji ili baza nije dostupna.
     */
    public void unesiKorisnika(Korisnik k) throws SQLException {
        String sql = "INSERT INTO korisnik (user, pass, ime, prezime, tip, adresa, telefon) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection kon = getKone(); PreparedStatement pstmt = kon.prepareStatement(sql)) {
            pstmt.setString(1, k.getUser());
            pstmt.setString(2, k.getPass());
            pstmt.setString(3, k.getIme());
            pstmt.setString(4, k.getPrezime());
            pstmt.setString(5, k.getTip());
            pstmt.setString(6, k.getAdresa());
            pstmt.setString(7, k.getTelefon());
            pstmt.executeUpdate();
        }
    }
    
    /**
     * Ažurira osnovne podatke korisnika na osnovu njegovog ID-a.
     * 
     * @param k Objekat sa novim podacima.
     * @throws SQLException Ako ID nije pronađen ili upit ne uspije.
     */
    public void azurirajKorisnika(Korisnik k) throws SQLException {
        String sql = "UPDATE korisnik SET ime = ?, prezime = ?, adresa = ?, telefon = ? WHERE id = ?";        
        try (Connection kon = getKone(); PreparedStatement pstmt = kon.prepareStatement(sql)) {
            pstmt.setString(1, k.getIme());
            pstmt.setString(2, k.getPrezime());
            pstmt.setString(3, k.getAdresa());
            pstmt.setString(4, k.getTelefon());
            pstmt.setInt(5, k.getId());
            pstmt.executeUpdate();
        }
    }
  
    /**
     * Postavlja novu lozinku za korisnika.
     * 
     * @param k Korisnik čija se lozinka mijenja.
     * @param novaLozinka Novi tekst lozinke.
     * @throws SQLException Ako ažuriranje ne uspije.
     */
    public void promjenaPassworda(Korisnik k, String novaLozinka) throws SQLException {
        String sql = "UPDATE korisnik SET pass = ? WHERE id = ?";
        try (Connection kon = getKone(); PreparedStatement pstmt = kon.prepareStatement(sql)) {
            pstmt.setString(1, novaLozinka);
            pstmt.setInt(2, k.getId());
            pstmt.executeUpdate();
        }
    }
    
    /**
     * Briše korisnika iz baze podataka.
     * @param id ID korisnika za brisanje.
     * @throws SQLException Ako brisanje nije moguće.
     */
    public void obrisiKorisnika(int id) throws SQLException {
        String sql = "DELETE FROM korisnik WHERE id = ?";        
        try (Connection kon = getKone(); PreparedStatement pstmt = kon.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    } 
   
    /** @return Vraća korisnika po ID-u ili null ako ne postoji. */
    public Korisnik vratiKorisnikaPoID(int id) throws SQLException {
        String sql = "SELECT * FROM korisnik WHERE id = ?";
        try (Connection kon = getKone(); PreparedStatement pstmt = kon.prepareStatement(sql)) {        
            pstmt.setInt(1, id);        
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return mapirajKorisnika(rs);
            }
        } 
        return null; 
    }

    /**
     * Provjerava kredencijale i popunjava lokalni objekat 'korisnik' ako je uspješno.
     * 
     * @param user Korisničko ime.
     * @param pass Lozinka.
     * @return true ako su podaci ispravni, inače false.
     */
    public boolean login(String user, String pass) throws SQLException {        
        String sql = "SELECT * FROM korisnik WHERE user = ? AND pass = ?";
        try (Connection kon = getKone(); PreparedStatement pstmt = kon.prepareStatement(sql)) { 
            pstmt.setString(1, user);
            pstmt.setString(2, pass);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    this.korisnik = mapirajKorisnika(rs);
                    MyLogger.info("Uspješno logiranje korisnika: " + korisnik.toString());
                    return true;
                }
            }
        }
        return false;
    }

    /** @return Lista svih korisnika u bazi. */
    public List<Korisnik> vratiSve() throws SQLException {        
        List<Korisnik> rezultat = new ArrayList<>();        
        String sql = "SELECT * FROM korisnik";  
        try (Connection kon = getKone(); Statement st = kon.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                rezultat.add(mapirajKorisnika(rs));
            }
        } 
        return rezultat;
    }
    
        /**
     * Pretražuje korisnike čije ime počinje zadanim nizom karaktera.
     * Koristi 'LIKE' operator sa parametrizovanim upitom radi sigurnosti.
     * 
     * @param uvjet Početna slova imena za pretragu.
     * @return Lista korisnika koji zadovoljavaju kriterij.
     * @throws SQLException U slučaju greške u radu sa bazom.
     */
    public ArrayList<Korisnik> vratiKojiZadovoljavajuUvjet(String uvjet) throws SQLException {
        ArrayList<Korisnik> rezultat = new ArrayList<>();
        String sql = "SELECT * FROM korisnik WHERE ime LIKE ?";
        
        try (Connection konekcija = getKone();
             PreparedStatement pstmt = konekcija.prepareStatement(sql)) {        
            
            // Postavljamo parametar sa džoker znakom (%) na kraju
            pstmt.setString(1, uvjet + "%");        
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    // Pozivamo zajedničku metodu za mapiranje podataka (DRY)
                    rezultat.add(mapirajKorisnika(rs));
                }
            }
        } 
        return rezultat;
    }


    /** @return Trenutno učitani korisnik. */
    public Korisnik getKorisnik() { return korisnik; }
    
    /** @param k Postavlja trenutnog korisnika. */
    public void setKorisnik(Korisnik k) { this.korisnik = k; }
}
