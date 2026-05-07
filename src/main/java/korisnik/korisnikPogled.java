
package korisnik;

import jakarta.inject.Named;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.context.FacesContext;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import korisni.webUtil;

/**
 * JSF managed bean (kontroler) za upravljanje korisnicima u web aplikaciji.
 * Implementira login, registraciju, pretragu, ažuriranje i unos korisnika.
 * Koristi sesijski scope za čuvanje stanja prijavljenog korisnika.
 *
 * @author Amel Džanić
 * @version 1.0
 * @see CRUDKorisnik
 * @see Korisnik
 */
@Named(value = "korisnikPogled")
@SessionScoped
public class korisnikPogled implements Serializable {
    /** Korisničko ime unijeto na login/registracija formi. */
    private String korisnickoIme;

    /** Lozinka unijeta na login/registracija formi. */
    private String korisnickaLozinka;

    /** Ime i prezime unijeti kao jedan string (koristi se pri unosu). */
    private String imeIPrezime;

    /** Tip korisnika (npr. "ADMINISTRATOR", "KORISNIK"). */
    private String tip;

    /** Trenutno prijavljeni korisnik (popunjava se nakon uspješnog logina). */
    private Korisnik k;

    /** CRUD kontroler za rad sa bazom podataka. */
    private CRUDKorisnik kont;

    /** Lista dostupnih tipova korisnika za dropdown meni (npr. ADMINISTRATOR, KORISNIK). */
    private List<String> tipovi = new ArrayList<>();

    /** Rezultati pretrage korisnika po imenu. */
    private ArrayList<Korisnik> pretragaKorisnika = new ArrayList<>();

    /** Status prijave korisnika – {@code true} ako je korisnik ulogovan. */
    private boolean loged = false;

    /** Parametar za pretragu korisnika – početak imena ili prezimena. */
    private String imeiPrZaPretragu;

    /** Flash poruka za prikazivanje korisniku (npr. nakon uspješne akcije). */
    private String flasMessage;

    /** Adresa korisnika (koristi se prilikom unosa). */
    private String adresa;

    /** Telefon korisnika (koristi se prilikom unosa). */
    private String telefon;
    /** Varijabla se koristi da se vidi da li je već vršeno pretraživanje*/
    private boolean pretrazivanje=false;

    /**
     * Konstruktor – inicijalizuje CRUD operacije nad korisnicima.
     * U slučaju greške pri inicijalizaciji baze, prikazuje odgovarajuću poruku
     * kroz {@link webUtil#errPoruka(java.lang.String)}.
     */
    public korisnikPogled() {
        try {
            this.kont = new CRUDKorisnik();            
        } catch (SQLException ex) {
            webUtil.errPoruka
            ("Greška u inicijalizaciji drviera: " + ex.getLocalizedMessage());
        }
    }
  
    /**
     * Generiše pri administratorskom unosu korisnika
     * tipove korisnika u select komponenti
     */
    private void inicijalisiDropDown(){
        getTipovi().add("ADMINISTRATOR"); 
        getTipovi().add("KORISNIK"); 
    }
    
    private boolean isAdmin() {
        return kont != null && 
               kont.getKorisnik() != null && 
               "ADMINISTRATOR".equals(kont.getKorisnik().getTip());
    }
    /**
     * Vrši pretragu korisnika po imenu (početak imena).
     * Rezultati pretrage se čuvaju u listi {@link #pretragaKorisnika}.
     *
     * @return {@code null} (ostaje na istoj stranici).
     */
    public String pretragaK(){
        try {
            //kont.vratiKojiZadovoljavajuUvjet(imeiPrZaPretragu, 0);
            pretragaKorisnika=kont.vratiKojiZadovoljavajuUvjet(imeiPrZaPretragu);
            setPretrazivanje(true);
            return null;
        } catch (SQLException ex) {
            webUtil.errPoruka(ex.getMessage());
        }
        return null;
    }
    /**
     * Obavlja prijavu (login) korisnika na sistem.
     * U slučaju uspjeha postavlja {@link #loged} na {@code true} i preusmjerava na početnu stranicu.
     * U slučaju neuspjeha prikazuje grešku i ostaje na istoj stranici.
     *
     * @return Stranica za preusmjeravanje ili {@code null} ako login nije uspio.
     * @throws SQLException Ako dođe do greške pri radu sa bazom.
     */
    public String registracija() throws SQLException{
        kont = new CRUDKorisnik();
        if(kont.login(korisnickoIme, korisnickaLozinka))
        {
            k= kont.getKorisnik();   
            resetPolja();
            loged=true;
            webUtil.testUsp("Uspješno logiranje!!!");           
            return "pocetna?faces-redirect=true";
        }
        else 
        {
            loged=false;
            webUtil.errPoruka("Netacni korisnički podaci!!!",""); 
            resetPolja();
            return null;
        }
    }
    /**
     * Unosi novog korisnika putem web forme (administratorski unos).
     *
     * @param tip Tip korisnika: 1 za "KORISNIK", sve ostalo za "ADMINISTRATOR".
     * @return Stranica za preusmjeravanje na index nakon uspješnog unosa,
     *         ili {@code null} ako je došlo do greške.
     */
    public String unosNovogKorisnikaWeb(int tip) { 
//        if (!isAdmin()) {
//            webUtil.errPoruka("Nemate administratorska ovlaštenja za ovu akciju!");
//            return null;
//        }
        try { 
            Korisnik unos = new Korisnik();
            unos.setImeIPrezime(imeIPrezime);
            unos.setId(0);
            unos.setAdresa(adresa);
            unos.setTelefon(telefon);
            unos.setUser(korisnickoIme);
            unos.setPass(korisnickaLozinka);
            if (tip==1) unos.setTip("KORISNIK");
            else unos.setTip("ADMINISTRATOR");
            kont.unesiKorisnika(unos);           
            setFlasMessage("Korisnik: " + imeIPrezime + " je uspješno dodan u bazu.");
            webUtil.testUsp(flasMessage);
            resetPolja();
            return "index?faces-redirect=true";
        } catch (SQLException ex) {
            resetPolja();
            webUtil.errPoruka("Greška u unosu korisnika", ex.getMessage() + " " + ex.getSQLState(), "");
        }
        return null;
       
    }
     /**
     * Resetuje sva polja forme na početne vrijednosti ({@code null}).
     */
    public void resetPolja(){
        korisnickoIme=null;
        imeIPrezime=null;
        adresa=null;
        korisnickaLozinka=null;
        telefon=null;
    }
    /**
     * Unosi novog administratora putem web forme.
     * Ova metoda je specifična za brzi unos administratora (bez adrese i telefona).
     *
     * @return Stranica za preusmjeravanje na index nakon uspjeha,
     *         ili {@code null} u slučaju greške.
     */
    public String unosNovogAdministratoraWeb() { 
        if (!isAdmin()) {
            webUtil.errPoruka("Nemate administratorska ovlaštenja za ovu akciju!");
            return null;
        }
        try { 
            Korisnik unos = new Korisnik();
            unos.setImeIPrezime(imeIPrezime);
            unos.setId(0);
            unos.setUser(korisnickoIme);
            unos.setPass(korisnickaLozinka);
            unos.setTip("ADMINISTRATOR");
            kont.unesiKorisnika(unos);
            //kont.UnesiKorisnika(new Korisnik(0, imeIPrezime, korisnickoIme, korisnickaLozinka,"ADMINISTRATOR"));
            resetPolja();
            return "index?faces-redirect=true";
        } catch (SQLException ex) {
           webUtil.errPoruka("Greška u unosu korisnika", ex.getMessage() + " " + ex.getSQLState(), "");
           resetPolja();
        }
        return null;
       
    }
    /**
     * Ažurira podatke trenutno prijavljenog korisnika (ime, prezime, adresu, telefon).
     *
     * @return {@code null} (ostaje na istoj stranici nakon ažuriranja).
     * @throws SQLException Ako dođe do greške pri radu sa bazom.
     */
    public String azuriranjeKorisnika() throws SQLException{
        kont.azurirajKorisnika(k);
        //kont.promjenaPassworda(k, k.getPass(), tip);
        //webUtil.infoPoruka("Uspješno ažuriranje korisnika","");
        webUtil.infoPoruka("Uspješno ažuriranje podataka korisnika", "");
        resetPolja();
        return null;
    }
    /**
     * Ažurira podatke trenutno prijavljenog korisnika (ime, prezime, adresu, telefon).
     *
     * @param id korisnika koji se ažurira
     * @return {@code null} (ostaje na istoj stranici nakon ažuriranja).
     * @throws SQLException Ako dođe do greške pri radu sa bazom.
     */
    public String azuriranjeKorisnika(int id) throws SQLException{
        kont.azurirajKorisnika(kont.vratiKorisnikaPoID(id));
        //kont.promjenaPassworda(k, k.getPass(), tip);
        //webUtil.infoPoruka("Uspješno ažuriranje korisnika","");
        webUtil.infoPoruka("Uspješno ažuriranje podataka korisnika", "");
        resetPolja();
        return null;
    }
    /**
     * Ažurira lozinku trenutno prijavljenog korisnika.
     *
     * @return {@code null} (ostaje na istoj stranici).
     * @throws SQLException Ako dođe do greške pri radu sa bazom.
     */
    public String azuriranjePassworda() throws SQLException{
        //kont.azurirajKorisnika(k);
        kont.promjenaPassworda(k, k.getPass());
        //kont.promjenaPassworda(k, k.getPass(), tip);
        //webUtil.infoPoruka("Uspješno ažuriranje korisnika","");
        webUtil.infoPoruka("Uspješno ažuriranje podataka korisnika", "");
        return null;
    }
    /**
     * Odjavljuje korisnika – invalidira sesiju i resetuje polja.
     *
     * @return Stranica za preusmjeravanje na početnu (index).
     */
    public String logOff() {       
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        resetPolja();
        return "/index?faces-redirect=true";        
    }  
     
    // ---------- Getteri i setteri ----------

    public String getKorisnickoIme() {
        return korisnickoIme;
    }

    public void setKorisnickoIme(String korisnickoIme) {
        this.korisnickoIme = korisnickoIme;
    }

    public String getKorisnickaLozinka() {
        return korisnickaLozinka;
    }

    public void setKorisnickaLozinka(String korisnickaLozinka) {
        this.korisnickaLozinka = korisnickaLozinka;
    }

    public String getImeIPrezime() {
        return imeIPrezime;
    }

    public void setImeIPrezime(String imeIPrezime) {
        this.imeIPrezime = imeIPrezime;
    }

    public String getTip() {
        return tip;
    }

    public void setTip(String tip) {
        this.tip = tip;
    }

    public Korisnik getK() {
        return k;
    }

    public void setK(Korisnik k) {
        this.k = k;
    }

    public CRUDKorisnik getKont() {
        return kont;
    }

    public void setKont(CRUDKorisnik kont) {
        this.kont = kont;
    }

    public List<String> getTipovi() {
        return tipovi;
    }

    public void setTipovi(List<String> tipovi) {
        this.tipovi = tipovi;
    }

    public ArrayList<Korisnik> getPretragaKorisnika() {
        return pretragaKorisnika;
    }

    public void setPretragaKorisnika(ArrayList<Korisnik> pretragaKorisnika) {
        this.pretragaKorisnika = pretragaKorisnika;
    }

    public boolean isLoged() {
        return loged;
    }

    public void setLoged(boolean loged) {
        this.loged = loged;
    }

    public String getImeiPrZaPretragu() {
        return imeiPrZaPretragu;
    }

    public void setImeiPrZaPretragu(String imeiPrZaPretragu) {
        this.imeiPrZaPretragu = imeiPrZaPretragu;
    }

    public String getFlasMessage() {
        return flasMessage;
    }

    public void setFlasMessage(String flasMessage) {
        this.flasMessage = flasMessage;
    }
    
    public String getAdresa() {
        return adresa;
    }

    public void setAdresa(String adresa) {
        this.adresa = adresa;
    }

    public void setTelefon(String telefon) {
        this.telefon = telefon;
    }

    public String getTelefon() {
        return telefon;
    }

    public boolean isPretrazivanje() {
        return pretrazivanje;
    }

    public void setPretrazivanje(boolean pretrazivanje) {
        this.pretrazivanje = pretrazivanje;
    }
    
    
    
    
    
}
