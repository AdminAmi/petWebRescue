package korisnik;

import jakarta.inject.Named;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.context.FacesContext;
import java.io.IOException;
import java.io.Serializable;
import java.sql.SQLException;
import korisni.webUtil;
/**
 * Klasa <code>SessionKorisnikPogled</code> je JavaServer Faces (JSF) managed bean koja se 
 * koristi za upravljanje sesijom korisnika u web aplikaciji. Ova klasa omogućava korisnicima 
 * da se prijave, odjave i provjeravaju svoje privilegije tijekom trajanja sesije. 
 * Klasa koristi anotacije @Named i @SessionScoped kako bi bila prepoznata kao JSF 
 * managed bean i kako bi njena stanja bila sačuvana tijekom cijele sesije korisnika.
 * @see CRUDKorisnik
 * @see Korisnik    
 * @author Amel Džanić
 * @version 1.0
 * @since 2026-05-01
 */
@Named(value = "sessionKorisnikPogled")
@SessionScoped
public class SessionKorisnikPogled implements Serializable {
    
    /**
     * Varijabla <code>k</code> predstavlja trenutnog korisnika koji je prijavljen u sesiju. Ova varijabla se koristi za pohranu informacija o korisniku, kao što su njegovo korisničko ime, tip korisnika (npr. administrator ili običan korisnik) i druge relevantne podatke. Kroz ovu varijablu, aplikacija može pristupiti informacijama o korisniku i prilagoditi funkcionalnosti i prikaz na osnovu njegovih privilegija.
     */
    private Korisnik k;
    /**
     * Varijabla <code>kont</code> predstavlja instancu klase <code>CRUDKorisnik</code>, koja se koristi za upravljanje operacijama nad korisnicima, kao što su prijava, registracija, ažuriranje podataka i brisanje korisnika. Ova varijabla omogućava interakciju sa bazom podataka i izvršavanje CRUD (Create, Read, Update, Delete) operacija nad korisničkim podacima. Kroz ovu varijablu, aplikacija može provjeriti vjerodostojnost korisničkih podataka prilikom prijave i upravljati informacijama o korisnicima tijekom sesije.
     */
    private CRUDKorisnik kont;
    /**
     * Varijabla <code>loged</code> je boolean vrijednost koja označava da li je korisnik trenutno prijavljen u sesiju. Ova varijabla se koristi za kontrolu pristupa određenim funkcionalnostima i stranicama unutar aplikacije. Kada je korisnik uspješno prijavljen, vrijednost <code>loged</code> se postavlja na true, što omogućava pristup zaštićenim dijelovima aplikacije. Ako korisnik nije prijavljen, vrijednost ostaje false, što ograničava pristup i može preusmjeriti korisnika na stranicu za prijavu.
     */
    private boolean loged = false;
    
    // Polja za login formu (ostaju u sesiji tokom cijele sesije)
    /**
     * Varijabla <code>korisnickoIme</code> predstavlja korisničko ime koje korisnik unosi prilikom prijave. Ova varijabla se koristi za pohranu unesenog korisničkog imena i omogućava aplikaciji da provjeri vjerodostojnost korisnika prilikom prijave. Vrijednost ove varijable se koristi u kombinaciji sa lozinkom (pohranjenom u varijabli <code>korisnickaLozinka</code>) kako bi se utvrdilo da li su uneseni podaci ispravni i da li korisnik ima pravo pristupa aplikaciji.
     */
    private String korisnickoIme;
    /**
     * Varijabla <code>korisnickaLozinka</code> predstavlja lozinku koju korisnik unosi prilikom prijave. Ova varijabla se koristi za pohranu unesene lozinke i omogućava aplikaciji da provjeri vjerodostojnost korisnika prilikom prijave. Vrijednost ove varijable se koristi u kombinaciji sa korisničkim imenom (pohranjenim u varijabli <code>korisnickoIme</code>) kako bi se utvrdilo da li su uneseni podaci ispravni i da li korisnik ima pravo pristupa aplikaciji.
     */
    private String korisnickaLozinka;
    /**
     * Konstruktor klase <code>SessionKorisnikPogled</code> pokušava inicijalizirati kontroler za korisnike (instancu klase <code>CRUDKorisnik</code>). Ako dođe do greške prilikom inicijalizacije, kao što je problem sa povezivanjem na bazu podataka, konstruktor hvata <code>SQLException</code> i ispisuje poruku o grešci koristeći pomoćnu klasu <code>webUtil</code>. Ovaj konstruktor se poziva prilikom kreiranja instance <code>SessionKorisnikPogled</code>, što omogućava da se kontroler za korisnike odmah postavi i bude spreman za korištenje u funkcijama vezanim za prijavu i upravljanje korisnicima tijekom sesije.
     */
    public SessionKorisnikPogled() {
        try {
            this.kont = new CRUDKorisnik();
        } catch (SQLException ex) {
            webUtil.errPoruka("Greška u inicijalizaciji: " + ex.getLocalizedMessage());
        }
    }
    /**
     * Metoda <code>registracija</code> se koristi za provjeru korisničkih podataka prilikom prijave. Ova metoda koristi kontroler <code>CRUDKorisnik</code> (pohranjen u varijabli <code>kont</code>) da provjeri da li su uneseni korisničko ime i lozinka ispravni. Ako su podaci ispravni, metoda postavlja trenutnog korisnika (varijabla <code>k</code>) na korisnika koji je prijavljen, postavlja varijablu <code>loged</code> na true, i prikazuje poruku o uspješnom logiranju. Nakon toga, metoda resetuje polja za login i preusmjerava korisnika na početnu stranicu. Ako su podaci netačni, metoda postavlja varijablu <code>loged</code> na false, prikazuje poruku o grešci i ostaje na stranici za prijavu.
     * 
     * @return Vraća string koji predstavlja putanju za preusmjeravanje nakon uspješne prijave ili null ako prijava nije uspjela.
     * @throws SQLException ako dođe do greške prilikom provjere korisničkih podataka u bazi podataka.
     */
    public String registracija() throws SQLException {
        if (kont.login(korisnickoIme, korisnickaLozinka)) {
            k = kont.getKorisnik();
            loged = true;
            webUtil.testUsp("Uspješno logiranje!!!");
            
            // Resetuj polja za login
            korisnickoIme = null;
            korisnickaLozinka = null;
            
            return "pocetna?faces-redirect=true";
        } else {
            loged = false;
            webUtil.errPoruka("Netačni korisnički podaci!!!");
            return null;  // Ostaje na login stranici
        }
    }
    /**
     * Metoda <code>logOff</code> se koristi za odjavu korisnika iz sesije. Ova metoda invalidira trenutnu sesiju, što znači da se svi podaci povezani sa sesijom brišu, uključujući informacije o prijavljenom korisniku. Nakon invalidacije sesije, metoda preusmjerava korisnika na početnu stranicu (index.xhtml) koristeći redirekciju. Ova funkcionalnost osigurava da nakon odjave korisnik više nema pristup zaštićenim dijelovima aplikacije i da se njegovi podaci ne čuvaju u sesiji.
     * 
     * @return Vraća string koji predstavlja putanju za preusmjeravanje nakon odjave, u ovom slučaju "/index?faces-redirect=true".
     */
    public String logOff() {
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        return "/index?faces-redirect=true";
    }
    /**
     * Metoda <code>provjeraPrijave</code> se koristi za provjeru da li je korisnik trenutno prijavljen u sesiju. Ova metoda provjerava vrijednost varijable <code>loged</code>, koja označava da li je korisnik prijavljen. Ako korisnik nije prijavljen (vrijednost <code>loged</code> je false), metoda preusmjerava korisnika na početnu stranicu (index.xhtml) koristeći redirekciju. Ova funkcionalnost osigurava da neautorizirani korisnici nemaju pristup zaštićenim dijelovima aplikacije i da se uvijek provjerava status prijave prije nego što se dozvoli pristup određenim funkcionalnostima ili stranicama.
     */
    public void provjeraPrijave() {
    if (!loged) {
        try {
                FacesContext.getCurrentInstance()
                    .getExternalContext()
                    .redirect("index.xhtml");
            } catch (IOException e) { }
        }
    }
    
    /**    
     * Ova metoda provjerava da li je korisnik prijavljen i da li ima tip "ADMINISTRATOR". 
     * Ako korisnik nije prijavljen ili nema odgovarajući tip, metoda preusmjerava korisnika na 
     * početnu stranicu (pocetna.xhtml) koristeći redirekciju. 
     * Ova funkcionalnost osigurava da samo korisnici sa administratorskim privilegijama 
     * imaju pristup određenim dijelovima aplikacije koji su namijenjeni isključivo administratorima. 
     */
    public void provjeraAdministrator() {
        if( !(loged && k != null && "ADMINISTRATOR".equals(k.getTip()))){
            try {
                FacesContext.getCurrentInstance()
                    .getExternalContext()
                    .redirect("pocetna.xhtml");
            } catch (IOException e) { }
        }
    }

    /**
     * Ova metoda vraća trenutnog korisnika koji je prijavljen u sesiju. Varijabla <code>k</code> sadrži informacije o korisniku, kao što su njegovo korisničko ime, tip korisnika i druge relevantne podatke. Kroz ovu metodu, aplikacija može pristupiti informacijama o trenutnom korisniku i koristiti ih za prilagođavanje funkcionalnosti i prikaza unutar aplikacije.
     * 
     * @return Vraća trenutnog korisnika koji je prijavljen u sesiju. Ako nije prijavljen, vraća null.
     */
    public Korisnik getK() { return k; }
    /**
     * Ova metoda postavlja trenutnog korisnika koji je prijavljen u sesiju. Varijabla <code>k</code> se koristi za pohranu informacija o korisniku, kao što su njegovo korisničko ime, tip korisnika i druge relevantne podatke. Kroz ovu metodu, aplikacija može ažurirati informacije o trenutnom korisniku tijekom sesije, na primjer nakon uspješne prijave ili promjene podataka korisnika.
     * 
     * @param k Postavlja trenutnog korisnika koji je prijavljen u sesiju. Očekuje se da ovaj parametar bude instanca klase <code>Korisnik</code> koja sadrži relevantne informacije o korisniku. Nakon poziva ove metode, varijabla <code>k</code> će biti ažurirana sa novim informacijama o korisniku.
     */
    public void setK(Korisnik k) { this.k = k; }
    /**
     * Ova metoda vraća instancu kontrolera za korisnike (klasa <code>CRUDKorisnik</code>) koji se koristi za upravljanje operacijama nad korisnicima, kao što su prijava, registracija, ažuriranje podataka i brisanje korisnika. Kroz ovu metodu, aplikacija može pristupiti funkcionalnostima koje nudi kontroler za korisnike i koristiti ih za provjeru vjerodostojnosti korisničkih podataka prilikom prijave i upravljanje informacijama o korisnicima tijekom sesije.
     * 
     * @return Vraća instancu kontrolera za korisnike (klasa <code>CRUDKorisnik</code>) koji se koristi za upravljanje operacijama nad korisnicima. Ova instanca omogućava pristup funkcionalnostima vezanim za korisnike, kao što su provjera vjerodostojnosti prilikom prijave, registracija novih korisnika, ažuriranje podataka korisnika i brisanje korisnika iz baze podataka.
     */
    public CRUDKorisnik getKont() { return kont; }
    /**
     * Ova metoda postavlja instancu kontrolera za korisnike (klasa <code>CRUDKorisnik</code>) koji se koristi za upravljanje operacijama nad korisnicima, kao što su prijava, registracija, ažuriranje podataka i brisanje korisnika. Kroz ovu metodu, aplikacija može ažurirati instancu kontrolera za korisnike tijekom sesije, na primjer nakon promjene konfiguracije ili ponovne inicijalizacije kontrolera. Ova metoda omogućava fleksibilnost u upravljanju kontrolerom za korisnike i osigurava da aplikacija uvijek koristi odgovarajuću instancu za rad sa korisničkim podacima.
     * 
     * @param kont Postavlja instancu kontrolera za korisnike (klasa <code>CRUDKorisnik</code>) koji se koristi za upravljanje operacijama nad korisnicima. Očekuje se da ovaj parametar bude instanca klase <code>CRUDKorisnik</code> koja sadrži funkcionalnosti vezane za korisnike, kao što su provjera vjerodostojnosti prilikom prijave, registracija novih korisnika, ažuriranje podataka korisnika i brisanje korisnika iz baze podataka. Nakon poziva ove metode, varijabla <code>kont</code> će biti ažurirana sa novom instancom kontrolera za korisnike.
     */
    public void setKont(CRUDKorisnik kont) { this.kont = kont; }
    /**
     * Ova metoda provjerava da li je korisnik trenutno prijavljen u sesiju. Varijabla <code>loged</code> se koristi kao indikator statusa prijave, gdje true označava da je korisnik prijavljen, a false da nije. Kroz ovu metodu, aplikacija može provjeriti status prijave korisnika i prilagoditi funkcionalnosti i prikaz unutar aplikacije na osnovu toga. Na primjer, ako korisnik nije prijavljen, aplikacija može ograničiti pristup određenim dijelovima ili preusmjeriti korisnika na stranicu za prijavu.
     * 
     * @return Vraća true ako je korisnik prijavljen, a false ako nije.
     */
    public boolean isLoged() { return loged; }
    /**
     * Ova metoda postavlja status prijave korisnika u sesiju. Varijabla <code>loged</code> se koristi kao indikator statusa prijave, gdje true označava da je korisnik prijavljen, a false da nije. Kroz ovu metodu, aplikacija može ažurirati status prijave korisnika tijekom sesije, na primjer nakon uspješne prijave ili odjave. Ova metoda omogućava aplikaciji da pravilno upravlja pristupom i funkcionalnostima unutar aplikacije na osnovu statusa prijave korisnika.
     * 
     * @param loged Postavlja status prijave korisnika u sesiju. Očekuje se da ovaj parametar bude boolean vrijednost, gdje true označava da je korisnik prijavljen, a false da nije.
     */
    public void setLoged(boolean loged) { this.loged = loged; }
    /**
     * Ova metoda vraća korisničko ime koje korisnik unosi prilikom prijave. Varijabla <code>korisnickoIme</code> se koristi za pohranu unesenog korisničkog imena i omogućava aplikaciji da provjeri vjerodostojnost korisnika prilikom prijave. Kroz ovu metodu, aplikacija može pristupiti unesenom korisničkom imenu i koristiti ga u kombinaciji sa lozinkom (pohranjenom u varijabli <code>korisnickaLozinka</code>) kako bi se utvrdilo da li su uneseni podaci ispravni i da li korisnik ima pravo pristupa aplikaciji.
     * 
     * @return Vraća korisničko ime korisnika.
     */
    public String getKorisnickoIme() { return korisnickoIme; }
    /**
     * Ova metoda postavlja korisničko ime koje korisnik unosi prilikom prijave. Varijabla <code>korisnickoIme</code> se koristi za pohranu unesenog korisničkog imena i omogućava aplikaciji da provjeri vjerodostojnost korisnika prilikom prijave. Kroz ovu metodu, aplikacija može ažurirati korisničko ime tijekom sesije, na primjer nakon uspješne prijave ili promjene podataka.
     * 
     * @param korisnickoIme Postavlja korisničko ime koje korisnik unosi prilikom prijave. Očekuje se da ovaj parametar bude string vrijednost.
     */
    public void setKorisnickoIme(String korisnickoIme) { this.korisnickoIme = korisnickoIme; }
    /**
     * Ova metoda vraća lozinku koju korisnik unosi prilikom prijave. Varijabla <code>korisnickaLozinka</code> se koristi za pohranu unesene lozinke i omogućava aplikaciji da provjeri vjerodostojnost korisnika prilikom prijave. Kroz ovu metodu, aplikacija može pristupiti unesenoj lozinci i koristiti ju u kombinaciji sa korisničkim imenom (pohranjenim u varijabli <code>korisnickoIme</code>) kako bi se utvrdilo da li su uneseni podaci ispravni i da li korisnik ima pravo pristupa aplikaciji.
     * 
     * @return Vraća lozinku korisnika.
     */
    public String getKorisnickaLozinka() { return korisnickaLozinka; }
    /**
     * Ova metoda postavlja lozinku koju korisnik unosi prilikom prijave. Varijabla <code>korisnickaLozinka</code> se koristi za pohranu unesene lozinke i omogućava aplikaciji da provjeri vjerodostojnost korisnika prilikom prijave. Kroz ovu metodu, aplikacija može ažurirati lozinku tijekom sesije, na primjer nakon uspješne prijave ili promjene podataka.
     * 
     * @param korisnickaLozinka Postavlja lozinku koju korisnik unosi prilikom prijave. Očekuje se da ovaj parametar bude string vrijednost.
     */
    public void setKorisnickaLozinka(String korisnickaLozinka) { this.korisnickaLozinka = korisnickaLozinka; }
}