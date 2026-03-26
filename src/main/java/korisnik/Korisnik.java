package korisnik;

/**
 *
 * @author Amel Dzanic
 */
public class Korisnik {
    private int id;
    private String user;
    private String pass;
    private String ime;
    private String prezime;
    private String tip;
    private String adresa;
    private String telefon;

    public Korisnik() {}

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

    
    

    @Override
    public String toString() {
        return "Osoba{" +
                "id=" + id +
                ", ime='" + ime + '\'' +
                ", prezime='" + prezime + '\'' +
                '}';    
    }
    //setteri i getteri
    public String getAdresa() {
        return adresa;
    }

    public void setAdresa(String adresa) {   
        this.adresa = adresa;
    }

    public String getTelefon() {
        return telefon;
    }

    
    public void setTelefon(String telefon) {    
        this.telefon = telefon;
    }

    public int getId() {
        return id;
    }
    
    public String getTip() {
        return tip;
    }

    public void setTip(String tip) {
        this.tip = tip;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getIme() {
        return ime;
    }

    public void setIme(String ime) {
        this.ime = ime;
    }

    public String getPrezime() {
        return prezime;
    }

    public void setPrezime(String prezime) {
        this.prezime = prezime;
    }  
    
    public String getImeIPrezime(){
        return ime + " " + prezime;
    }
    
    public void setImeIPrezime(String imeIprezime) {
        String[] dijelovi = imeIprezime.trim().split(" ", 2);

        this.ime = dijelovi[0];
        this.prezime = dijelovi[1];
   
    }

}
