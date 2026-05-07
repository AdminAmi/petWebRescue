package korisnik;

/**
 * Klasa koja predstavlja entitet (model) korisnika u sistemu.
 * Sadrži sve relevantne podatke o korisniku i osnovne metode za rad sa njima.
 *
 * @author Amel Džanić
 * @version 1.0 
 */
public class Korisnik {
    /** Jedinstveni identifikator korisnika (primarni ključ u bazi). */
    private int id;
    /** Korisničko ime (jedinstveno u sistemu). */
    private String user;
    /** Lozinka korisnika  */
    private String pass;
    /** Ime korisnika. */
    private String ime;
    /** Prezime korisnika. */
    private String prezime;
    /** Tip korisnika (npr. "ADMINISTRATOR", "KORISNIK"). */
    private String tip;
    /** Adresa stanovanja korisnika (opciono polje). */
    private String adresa;
    /** Broj telefona korisnika. */
    private String telefon;
    
    /**
     * Prazan konstruktor – kreira korisnika sa svim poljima na default vrijednostima.
     * Neophodan za rad sa JavaBeans konvencijama (npr. u JSF).
     */
    public Korisnik() {}
    
    /**
     * Potpuni konstruktor – inicijalizuje sva polja korisnika.
     *
     * @param id       Jedinstveni identifikator.
     * @param user     Korisničko ime.
     * @param pass     Lozinka.
     * @param ime      Ime.
     * @param prezime  Prezime.
     * @param tip      Tip korisnika.
     * @param adresa   Adresa (može biti {@code null}).
     * @param telefon  Broj telefona.
     */
    public Korisnik(int id, String user, String pass, String ime, 
            String prezime, String tip, String adresa, String telefon) {
        this.id = id;
        this.user = user;        
        this.pass = pass;
        this.ime = ime;
        this.prezime = prezime;
        this.tip = tip;
        this.adresa = adresa;
        this.telefon = telefon;
    }

    
    
     /**
     * Vraća string reprezentaciju korisnika.
     * Prikazuje samo ID, ime i prezime iz sigurnosnih razloga (ne prikazuje lozinku).
     *
     * @return String u formatu "Osoba{id=..., ime='...', prezime='...'}"
     */
    @Override
    public String toString() {
        return "Osoba{" +
                "id=" + id +
                ", ime='" + ime + '\'' +
                ", prezime='" + prezime + '\'' +
                '}';    
    }
    //setteri i getteri
    /**
     * Vraća adresu korisnika.
     *
     * @return Adresa kao {@link String} ili {@code null} ako nije postavljena.
     */
    public String getAdresa() {
        return adresa;
    }
    /**
     * Postavlja adresu korisnika.
     *
     * @param adresa Nova adresa.
     */
    public void setAdresa(String adresa) {   
        this.adresa = adresa;
    }
    /**
     * Vraća telefon korisnika.
     *
     * @return Broj telefona kao {@link String}.
     */
    public String getTelefon() {
        return telefon;
    }

    /**
     * Postavlja broj telefona korisnika.
     *
     * @param telefon Novi broj telefona.
     */
    public void setTelefon(String telefon) {    
        this.telefon = telefon;
    }
    /**
     * Vraća ID korisnika.
     *
     * @return Jedinstveni identifikator.
     */
    public int getId() {
        return id;
    }
    /**
     * Vraća tip korisnika.
     *
     * @return Tip korisnika (npr. "ADMINISTRATOR").
     */
    public String getTip() {
        return tip;
    }
    /**
     * Postavlja tip korisnika.
     *
     * @param tip Novi tip (preporučene vrijednosti: "ADMINISTRATOR", "KORISNIK").
     */
    public void setTip(String tip) {
        this.tip = tip;
    }
     /**
     * Postavlja ID korisnika.
     *
     * @param id Novi identifikator.
     */
    public void setId(int id) {
        this.id = id;
    }
    /**
     * Vraća korisničko ime.
     *
     * @return Korisničko ime.
     */
    public String getUser() {
        return user;
    }
    /**
     * Postavlja korisničko ime.
     *
     * @param user Novo korisničko ime (mora biti jedinstveno u bazi).
     */
    public void setUser(String user) {
        this.user = user;
    }
     /**
     * Vraća lozinku korisnika.
     *
     * @return Lozinka (otvoreni tekst).
     */
    public String getPass() {
        return pass;
    }
     /**
     * Postavlja lozinku korisnika.
     *
     * @param pass Nova lozinka.
     */
    public void setPass(String pass) {
        this.pass = pass;
    }
    /**
     * Vraća ime korisnika.
     *
     * @return Ime.
     */
    public String getIme() {
        return ime;
    }
     /**
     * Postavlja ime korisnika.
     *
     * @param ime Novo ime.
     */
    public void setIme(String ime) {
        this.ime = ime;
    }
    /**
     * Vraća prezime korisnika.
     *
     * @return Prezime.
     */
    public String getPrezime() {
        return prezime;
    }
     /**
     * Postavlja prezime korisnika.
     *
     * @param prezime Novo prezime.
     */
    public void setPrezime(String prezime) {
        this.prezime = prezime;
    }  
    /**
     * Vraća spojeno ime i prezime u jednom stringu.
     *
     * @return String u formatu "ime prezime".
     */
    public String getImeIPrezime(){
        return ime + " " + prezime;
    }
    /**
     * Postavlja ime i prezime iz jednog stringa.
     * Ponašanje:
     * <ul>
     *     <li>Ako string sadrži razmak, prvi dio postaje ime, a ostatak prezime.</li>
     *     <li>Ako nema razmaka, i ime i prezime se postavljaju na taj string.</li>
     *     <li>Ako je ulazni string {@code null} ili prazan, metoda ne radi ništa.</li>
     * </ul>
     *
     * @param imeIprezime String koji sadrži ime i prezime (npr. "Amel Džanić").
     */
    public void setImeIPrezime(String imeIprezime) {
        if (imeIprezime == null || imeIprezime.trim().isEmpty()) {
        return;
        }

        String unos = imeIprezime.trim();
        int prviRazmak = unos.indexOf(" ");

        if (prviRazmak == -1) {
            // Samo jedna riječ
            this.ime = unos;
            this.prezime = unos;
        } else {
            // Više riječi: prva ide u ime, sve ostalo u prezime
            this.ime = unos.substring(0, prviRazmak);
            this.prezime = unos.substring(prviRazmak).trim();
        }
   
    }

}
