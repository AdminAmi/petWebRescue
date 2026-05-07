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
     * Pomoćna metoda za mapiranje trenutnog reda {@link ResultSet}-a u objekat {@link Korisnik}.
     * Implementira DRY princip – izbjegava dupliciranje koda za mapiranje.
     *
     * @param rs {@link ResultSet} pozicioniran na red koji treba mapirati.
     * @return {@link Korisnik} objekat sa popunjenim podacima iz trenutnog reda.
     * @throws SQLException Ako neka od kolona ne postoji u result setu.
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
     * Kreira tabelu 'korisnik' u bazi podataka ukoliko ista ne postoji.
     * Tabela sadrži sljedeće kolone:
     * <ul>
     *     <li>id – INTEGER PRIMARY KEY AUTOINCREMENT</li>
     *     <li>user – TEXT NOT NULL UNIQUE</li>
     *     <li>pass – TEXT NOT NULL</li>
     *     <li>ime – TEXT NOT NULL</li>
     *     <li>prezime – TEXT NOT NULL</li>
     *     <li>tip – TEXT</li>
     *     <li>adresa – TEXT</li>
     *     <li>telefon – TEXT NOT NULL</li>
     * </ul>
     *
     * @throws SQLException U slučaju SQL greške (npr. greška pri izvršavanju naredbe).
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
     * Unosi novog korisnika u bazu podataka koristeći parametrizovani SQL upit.
     * Na ovaj način se sprječava SQL injection.
     *
     * @param k Objekat {@link Korisnik} sa podacima za unos.
     * @throws SQLException Ako korisničko ime već postoji (zbog UNIQUE constraint-a)
     *                      ili ako baza nije dostupna.
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
     * Ažurira osnovne podatke postojećeg korisnika na osnovu njegovog ID-a.
     * Metoda ažurira: ime, prezime, adresu i telefon.
     *
     * @param k Objekat {@link Korisnik} koji sadrži nove podatke.
     *          ID korisnika se koristi kao identifikator.
     * @throws SQLException Ako korisnik sa datim ID-om ne postoji
     *                      ili ako SQL upit ne uspije.
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
     * Mijenja lozinku za specificiranog korisnika.     
     * U produkcijskom okruženju preporučuje se korištenje heširanja.
     *
     * @param k            Korisnik čija se lozinka mijenja.
     * @param novaLozinka  Novi tekst lozinke.
     * @throws SQLException Ako ažuriranje ne uspije (npr. korisnik ne postoji).
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
     * Briše korisnika iz baze podataka na osnovu njegovog ID-a.
     *
     * @param id ID korisnika koji se briše.
     * @throws SQLException Ako brisanje nije moguće (npr. korisnik ne postoji,
     *                      ili postoje strani ključevi koji referenciraju ovog korisnika).
     */
    public void obrisiKorisnika(int id) throws SQLException {
        String sql = "DELETE FROM korisnik WHERE id = ?";        
        try (Connection kon = getKone(); PreparedStatement pstmt = kon.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    } 
   
    /**
     * Vraća korisnika na osnovu njegovog ID-a.
     *
     * @param id ID korisnika koji se traži.
     * @return {@link Korisnik} objekat ako je pronađen, inače {@code null}.
     * @throws SQLException U slučaju SQL greške.
     */
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
     * Provjerava kredencijale korisnika (korisničko ime i lozinka).
     * U slučaju uspješnog logina, popunjava interni atribut {@link #korisnik}
     * sa podacima prijavljenog korisnika.
     *
     * @param user Korisničko ime.
     * @param pass Lozinka (otvoreni tekst).
     * @return {@code true} ako su kredencijali ispravni, inače {@code false}.
     * @throws SQLException Ako dođe do greške pri radu sa bazom.
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

    /**
     * Vraća listu svih korisnika iz baze podataka.
     *
     * @return {@link List} svih {@link Korisnik} objekata.
     *         Ukoliko nema korisnika, vraća praznu listu.
     * @throws SQLException U slučaju SQL greške.
     */
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
     * Koristi SQL 'LIKE' operator sa džoker znakom (%) na kraju šablona.
     * Parametrizovani upit osigurava zaštitu od SQL injection-a.
     *
     * @param uvjet Početna slova imena za pretragu (case-sensitive zavisi od baze).
     * @return {@link ArrayList} korisnika koji zadovoljavaju kriterij.
     *         Ukoliko nema rezultata, vraća praznu listu.
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


    /**
     * Vraća trenutno učitani (prijavljeni) objekat korisnika.
     *
     * @return {@link Korisnik} trenutno aktivnog korisnika.
     */
    public Korisnik getKorisnik() { return korisnik; }
    
    /**
     * Postavlja trenutno aktivnog korisnika.
     *
     * @param k Novi aktivni korisnik.
     */
    public void setKorisnik(Korisnik k) { this.korisnik = k; }
}
