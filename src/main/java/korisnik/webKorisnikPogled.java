package korisnik;

import jakarta.inject.Named;
import jakarta.faces.view.ViewScoped;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import korisni.webUtil;
import udomljavanje.UdomljavanjeCRUD;
import udomljavanje.Udomljen;
/**
 * Managed bean koji služi kao kontroler za korisnički interfejs vezan za upravljanje korisnicima u sistemu.
 * Ova klasa je odgovorna za obradu korisničkih zahtjeva, interakciju sa poslovnom logikom kroz SessionKorisnikPogled, i pripremu podataka za prikaz na stranici.
 * @author Amel Džanić
 * @version 1.0
 */
@Named(value = "webKorisnikPogled")
@ViewScoped
public class webKorisnikPogled implements Serializable {    
    /**
     * Korisničko ime koje se unosi u formu za kreiranje ili ažuriranje korisnika. 
     * Ovo polje se koristi za unos i prikaz korisničkog imena, ali se ne čuva u sesiji kako bi se osigurala sigurnost i privatnost podataka.
     */
    private String korisnickoIme; 
    /**
     * Lozinka korisnika koja se unosi u formu za kreiranje ili ažuriranje korisnika. 
     * Ovo polje se koristi za unos i prikaz lozinke, ali se ne čuva u sesiji kako bi se osigurala sigurnost i privatnost podataka.
     */
    private String korisnickaLozinka;
    /**
     * Ime i prezime korisnika koje se unosi u formu za kreiranje ili ažuriranje korisnika. 
     * Ovo polje se koristi za unos i prikaz imena i prezimena, ali se ne čuva u sesiji kako bi se osigurala sigurnost i privatnost podataka.
     */
    private String imeIPrezime;
    /**
     * Tip korisnika koji se unosi u formu za kreiranje ili ažuriranje korisnika. 
     * Ovo polje se koristi za unos i prikaz tipa korisnika (npr. "ADMINISTRATOR" ili "KORISNIK"), ali se ne čuva u sesiji kako bi se osigurala sigurnost i privatnost podataka.
     */
    private String tip;
    /**
     * Adresa korisnika koja se unosi u formu za kreiranje ili ažuriranje korisnika. 
     * Ovo polje se koristi za unos i prikaz adrese, ali se ne čuva u sesiji kako bi se osigurala sigurnost i privatnost podataka.
     */
    private String adresa;
    /**
     * Broj telefona korisnika koji se unosi u formu za kreiranje ili ažuriranje korisnika. 
     * Ovo polje se koristi za unos i prikaz broja telefona, ali se ne čuva u sesiji kako bi se osigurala sigurnost i privatnost podataka.
     */
    private String telefon;
    /**
     * Ime i prezime korisnika koji se koristi za pretragu korisnika. 
     * Ovo polje se koristi kao kriterijum pretrage korisnika u sistemu, ali se ne čuva u sesiji kako bi se osigurala sigurnost i privatnost podataka.
     */
    private String imeiPrZaPretragu;
    /**
     * Poruka koja se koristi za prikaz rezultata operacija (npr. uspješno dodavanje korisnika) ili grešaka. 
     * Ovo polje se koristi za komunikaciju sa korisnikom putem korisničkog interfejsa, ali se ne čuva u sesiji kako bi se osigurala sigurnost i privatnost podataka.
     */
    private String flasMessage;
    /**
     * ID trenutno selektovanog korisnika koji se koristi za identifikaciju korisnika nad kojim se vrše operacije, kao što su rezervacije i udomljavanja. 
     * Ovo polje se koristi za praćenje ID-a korisnika koji je trenutno selektovan u korisničkom interfejsu, ali se ne čuva u sesiji kako bi se osigurala sigurnost i privatnost podataka.
     */
    private int selektovaniID;

    // Rezultati pretrage
    /**
     * Lista korisnika koji zadovoljavaju kriterijum pretrage. 
     * Ova lista se popunjava rezultatima pretrage korisnika na osnovu unesenog imena i prezimena,
     *  ali se ne čuva u sesiji kako bi se osigurala sigurnost i privatnost podataka.
     */
    private ArrayList<Korisnik> pretragaKorisnika = new ArrayList<>();
    /**
     * Lista tipova korisnika koja se koristi za popunjavanje dropdown menija u korisničkom interfejsu.
     * Ova lista se ne čuva u sesiji kako bi se osigurala sigurnost i privatnost podataka.
     */
    private List<String> tipovi = new ArrayList<>();
    /**
     * Indikator da li je trenutno aktivno pretraživanje korisnika. 
     * Ova varijabla se koristi za kontrolu prikaza rezultata pretrage na korisničkom interfejsu, gdje se različiti elementi mogu prikazivati ili skrivati ovisno o tome da li je pretraga aktivna.
     */
    private boolean pretrazivanje = false;
    /**
     * Lista udomljenih ljubimaca povezanih sa trenutno selektovanim korisnikom. 
     * Ova lista se popunjava podacima o ljubimcima koje je korisnik udomio, ali se ne čuva u sesiji kako bi se osigurala sigurnost i privatnost podataka.
     */
    private ArrayList<Udomljen> udomljeni = new ArrayList<>();
    /**
     * Lista trenutnih rezervacija ljubimaca povezanih sa trenutno selektovanim korisnikom. 
     * Ova lista se popunjava podacima o ljubimcima koje je korisnik rezervisao, ali se ne čuva u sesiji kako bi se osigurala sigurnost i privatnost podataka.
     */
    private ArrayList<Udomljen> trenutnaRezervacija = new ArrayList<>();    
    /**
     * Session bean koji sadrži informacije o trenutno prijavljenom korisniku i poslovnu logiku vezanu za upravljanje korisnicima. 
     * Ovaj bean se koristi za pristup podacima o korisniku i izvršavanje operacija nad korisnicima, ali se ne čuva u sesiji kako bi se osigurala sigurnost i privatnost podataka.
     */
    private SessionKorisnikPogled sessionKorisnik;
    /**
     * Konstruktor koji inicijalizuje dropdown meni sa tipovima korisnika. 
     * Ova metoda se poziva prilikom kreiranja instance webKorisnikPogled managed beana, i koristi se za popunjavanje liste tipova korisnika koja se koristi u korisničkom interfejsu.
     */
    public webKorisnikPogled() {
        inicijalisiDropDown();
    }
    /**
     * Setter za SessionKorisnikPogled bean. Ova metoda se koristi za injektovanje SessionKorisnikPogled beana u webKorisnikPogled managed bean, omogućavajući pristup informacijama o trenutno prijavljenom korisniku i poslovnoj logici vezanoj za upravljanje korisnicima.
     * @param sessionKorisnik Instanca SessionKorisnikPogled beana koji se postavlja u ovaj managed bean. Ova metoda se obično poziva od strane JSF frameworka prilikom injektovanja zavisnosti, i omogućava webKorisnikPogled managed beanu da koristi funkcionalnosti i podatke iz SessionKorisnikPogled beana.
     */
    @jakarta.inject.Inject
    public void setSessionKorisnik(SessionKorisnikPogled sessionKorisnik) {
        this.sessionKorisnik = sessionKorisnik;
    }
    /**
     * Inicijalizuje dropdown meni sa tipovima korisnika. Ova metoda popunjava listu tipova korisnika sa vrijednostima "ADMINISTRATOR" i "KORISNIK", koje se koriste u korisničkom interfejsu za odabir tipa korisnika prilikom kreiranja ili ažuriranja korisnika.
     */
    private void inicijalisiDropDown() {
        tipovi.add("ADMINISTRATOR");
        tipovi.add("KORISNIK");
    }
    /**
     * Provjerava da li trenutno prijavljeni korisnik ima administratorska ovlaštenja. Ova metoda se koristi za kontrolu pristupa određenim funkcionalnostima u korisničkom interfejsu koje su namijenjene samo administratorima, kao što su kreiranje novih korisnika ili ažuriranje postojećih korisnika.
     * @return {@code true} ako je trenutno prijavljeni korisnik administrator, {@code false} inače.
     */
    private boolean isAdmin() {
        return sessionKorisnik != null && 
               sessionKorisnik.getK() != null && 
               "ADMINISTRATOR".equals(sessionKorisnik.getK().getTip());
    }
    /**
     * Učitava podatke o trenutno selektovanom korisniku, uključujući informacije o korisniku,
     *  historiju udomljavanja i trenutne rezervacije. 
     * Ova metoda se obično poziva kada se korisnik selektuje u korisničkom interfejsu,
     *  i koristi se za dohvaćanje podataka iz baze podataka na osnovu ID-a selektovanog korisnika,
     *  te pripremu tih podataka za prikaz na stranici.
     */
    public void ucitajKorisnika(){
        try {
            sessionKorisnik.getKont().setKorisnik
            (sessionKorisnik.getKont().vratiKorisnikaPoID(selektovaniID));
            udomljavanje.UdomljavanjeCRUD uc = new UdomljavanjeCRUD();
            setUdomljeni((ArrayList<Udomljen>) uc.dobaviHistorijuKlijenta(selektovaniID));
            setTrenutnaRezervacija((ArrayList<Udomljen>) uc.dobaviSveRezervacijeZaKorisnika(selektovaniID));
        } catch (SQLException ex) {
            webUtil.errPoruka("Greška u učitavanju ljubimaca" + ex, "");
        }
    }
    /**
     * Pretražuje korisnike na osnovu unesenog imena i prezimena. 
     * Ova metoda koristi kriterijum pretrage unesen u polje {@code imeiPrZaPretragu} 
     * za dohvaćanje liste korisnika koji zadovoljavaju taj kriterijum iz baze podataka, 
     * i priprema te podatke za prikaz na stranici. Rezultati pretrage se čuvaju u 
     * listi {@code pretragaKorisnika}, a indikator {@code pretrazivanje} se postavlja 
     * na {@code true} kako bi se kontrolisao prikaz rezultata pretrage na korisničkom interfejsu.
     * @return {@code null} nakon izvršavanja pretrage, što obično znači da se ostaje na istoj stranici, ali sa ažuriranim rezultatima pretrage. U slučaju greške prilikom pretrage, metoda također vraća {@code null} nakon prikaza poruke o grešci.
     */
    public String pretragaK() {
        try {
            if (sessionKorisnik.getKont() != null) {
                pretragaKorisnika = sessionKorisnik.getKont()
                    .vratiKojiZadovoljavajuUvjet(imeiPrZaPretragu);
                pretrazivanje = true;
            }
            return null;
        } catch (SQLException ex) {
            webUtil.errPoruka(ex.getMessage());
        }
        return null;
    }
    /**
     * Kreira novog korisnika na osnovu unesenih podataka u formu. 
     * Ova metoda provjerava da li trenutno prijavljeni korisnik ima administratorska ovlaštenja 
     * prije nego što dozvoli kreiranje novog korisnika. Ako korisnik nema odgovarajuća 
     * ovlaštenja, prikazuje se poruka o grešci i metoda se prekida. Ako korisnik ima ovlaštenja, 
     * metoda kreira novi objekat {@link Korisnik} sa unesenim podacima, postavlja tip korisnika na 
     * osnovu unosa (1 za "KORISNIK", 2 za "ADMINISTRATOR"), i poziva poslovnu logiku 
     * kroz {@code sessionKorisnik.getKont().unesiKorisnika(unos)} da bi se novi korisnik 
     * sačuvao u bazi podataka. Nakon uspješnog kreiranja korisnika, prikazuje se poruka o uspjehu, 
     * polja forme se resetuju, i korisnik se preusmjerava na početnu stranicu. 
     * U slučaju greške prilikom kreiranja korisnika, prikazuje se poruka o grešci i 
     * polja forme se resetuju.
     * @param tip Tip korisnika koji se kreira (1 za "KORISNIK", 2 za "ADMINISTRATOR").
     * @return {@code "index?faces-redirect=true"} nakon uspješnog kreiranja korisnika, što preusmjerava korisnika na početnu stranicu. U slučaju greške ili nedostatka ovlaštenja, metoda vraća {@code null} nakon prikaza odgovarajuće poruke.
     */
    public String unosNovogKorisnikaWeb(int tip) {
        if (!isAdmin()) {
            webUtil.errPoruka("Nemate administratorska ovlaštenja za ovu akciju!");
            return null;
        }
        try {
            Korisnik unos = new Korisnik();
            unos.setImeIPrezime(imeIPrezime);
            unos.setId(0);
            unos.setAdresa(adresa);
            unos.setTelefon(telefon);
            unos.setUser(korisnickoIme);
            unos.setPass(korisnickaLozinka);
            unos.setTip(tip == 1 ? "KORISNIK" : "ADMINISTRATOR");
            
            sessionKorisnik.getKont().unesiKorisnika(unos);
            flasMessage = "Korisnik: " + imeIPrezime + " je uspješno dodan u bazu.";
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
     * Kreira novog administratora na osnovu unesenih podataka u formu. 
     * Ova metoda provjerava da li trenutno prijavljeni korisnik ima administratorska ovlaštenja
     *  prije nego što dozvoli kreiranje novog administratora. Ako korisnik nema odgovarajuća 
     * ovlaštenja, prikazuje se poruka o grešci i metoda se prekida. Ako korisnik ima ovlaštenja, 
     * metoda kreira novi objekat {@link Korisnik} sa unesenim podacima, 
     * postavlja tip korisnika na "ADMINISTRATOR", i poziva poslovnu logiku 
     * kroz {@code sessionKorisnik.getKont().unesiKorisnika(unos)} da bi se novi administrator 
     * sačuvao u bazi podataka. Nakon uspješnog kreiranja administratora, prikazuje se poruka o 
     * uspjehu, polja forme se resetuju, i korisnik se preusmjerava na početnu stranicu. 
     * U slučaju greške prilikom kreiranja administratora, prikazuje se poruka o grešci i 
     * polja forme se resetuju.
     * @return {@code "index?faces-redirect=true"} nakon uspješnog kreiranja administratora, što preusmjerava korisnika na početnu stranicu. U slučaju greške ili nedostatka ovlaštenja, metoda vraća {@code null} nakon prikaza odgovarajuće poruke.
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
            
            sessionKorisnik.getKont().unesiKorisnika(unos);
            resetPolja();
            return "index?faces-redirect=true";
        } catch (SQLException ex) {
            webUtil.errPoruka("Greška u unosu korisnika", ex.getMessage() + " " + ex.getSQLState(), "");
            resetPolja();
        }
        return null;
    }
    /**
     * Ažurira podatke o trenutno selektovanom korisniku na osnovu unesenih podataka u formu. 
     * Ova metoda provjerava da li trenutno prijavljeni korisnik ima administratorska ovlaštenja prije nego što dozvoli ažuriranje podataka korisnika. Ako korisnik nema odgovarajuća
     * @return {@code null} nakon uspješnog ažuriranja podataka korisnika, što obično znači da se ostaje na istoj stranici, ali sa ažuriranim podacima. U slučaju greške ili nedostatka ovlaštenja, metoda vraća {@code null} nakon prikaza odgovarajuće poruke.
     * @throws SQLException ako dođe do greške prilikom ažuriranja podataka korisnika u bazi podataka.
     */
    public String azuriranjeKorisnika() throws SQLException {
        if (sessionKorisnik.getK() != null) {
            sessionKorisnik.getKont().azurirajKorisnika(sessionKorisnik.getK());
            webUtil.infoPoruka("Uspješno ažuriranje podataka korisnika", "");
            resetPolja();
        }
        return null;
    }
    /**
     * Ažurira podatke o korisniku na osnovu unesenih podataka u formu. 
     * Ova metoda provjerava da li trenutno prijavljeni korisnik ima administratorska ovlaštenja prije nego što dozvoli ažuriranje podataka korisnika. Ako korisnik nema odgovarajuća
     * @param kor Objekat {@link Korisnik} koji sadrži ažurirane podatke o korisniku. Ovaj objekat se koristi za ažuriranje podataka korisnika u bazi podataka, ali se ne čuva u sesiji kako bi se osigurala sigurnost i privatnost podataka.
     * @return {@code null} nakon uspješnog ažuriranja podataka korisnika, što obično znači da se ostaje na istoj stranici, ali sa ažuriranim podacima. U slučaju greške ili nedostatka ovlaštenja, metoda vraća {@code null} nakon prikaza odgovarajuće poruke.
     * @throws SQLException ako dođe do greške prilikom ažuriranja podataka korisnika u bazi podataka.
     */
    public String azuriranjeKorisnika(Korisnik kor) throws SQLException {
        System.out.println("kor");
        if (sessionKorisnik.getKont() != null) {
            sessionKorisnik.getKont().azurirajKorisnika(kor);
            webUtil.infoPoruka("Uspješno ažuriranje podataka korisnika", "");
            resetPolja();
        }
        return null;
    }
    /**
     * Ažurira lozinku trenutno selektovanog korisnika na osnovu unesenih podataka u formu. 
     * Ova metoda provjerava da li trenutno prijavljeni korisnik ima administratorska ovlaštenja prije nego što dozvoli ažuriranje lozinke korisnika. Ako korisnik nema odgovarajuća
     * @return {@code null} nakon uspješnog ažuriranja lozinke korisnika, što obično znači da se ostaje na istoj stranici, ali sa ažuriranom lozinkom. U slučaju greške ili nedostatka ovlaštenja, metoda vraća {@code null} nakon prikaza odgovarajuće poruke.
     * @throws SQLException ako dođe do greške prilikom ažuriranja lozinke korisnika u bazi podataka.
     */
    public String azuriranjePassworda() throws SQLException {
        if (sessionKorisnik.getK() != null && sessionKorisnik.getKont() != null) {
            sessionKorisnik.getKont().promjenaPassworda(
                sessionKorisnik.getK(), 
                sessionKorisnik.getK().getPass()
            );
            webUtil.infoPoruka("Uspješno ažuriranje passworda korisnika", "");
        }
        return null;
    }
    /**
     * Ažurira lozinku korisnika na osnovu unesenih podataka u formu. 
     * Ova metoda provjerava da li trenutno prijavljeni korisnik ima administratorska ovlaštenja prije nego što dozvoli ažuriranje lozinke korisnika. Ako korisnik nema odgovarajuća ovlaštenja, prikazuje se poruka o grešci i metoda se prekida.
     * @param kor Objekat {@link Korisnik} koji sadrži ažurirane podatke o korisniku, uključujući novu lozinku. Ovaj objekat se koristi za ažuriranje lozinke korisnika u bazi podataka, ali se ne čuva u sesiji kako bi se osigurala sigurnost i privatnost podataka.
     * @return {@code null} nakon uspješnog ažuriranja lozinke korisnika, što obično znači da se ostaje na istoj stranici, ali sa ažuriranom lozinkom. U slučaju greške ili nedostatka ovlaštenja, metoda vraća {@code null} nakon prikaza odgovarajuće poruke.
     * @throws SQLException ako dođe do greške prilikom ažuriranja lozinke korisnika u bazi podataka.
     */
    public String azuriranjePassworda(Korisnik kor) throws SQLException {
        if ( sessionKorisnik.getKont() != null) {
            sessionKorisnik.getKont().promjenaPassworda(
                kor, 
                korisnickaLozinka
            );
            webUtil.infoPoruka("Uspješno ažuriranje passworda korisnika", "");
        }
        return null;
    }
    /**
     * Resetuje polja forme na početne vrijednosti. Ova metoda se koristi za čišćenje unesenih podataka u formi nakon izvršavanja operacija kao što su kreiranje ili ažuriranje korisnika, kako bi se osigurala sigurnost i privatnost podataka, te pripremila forma za unos novih podataka.
     */
    public void resetPolja() {
        korisnickoIme = null;
        imeIPrezime = null;
        adresa = null;
        korisnickaLozinka = null;
        telefon = null;
        imeiPrZaPretragu = null;
    }

   
    /**
     * Vraća korisničko ime korisnika.
     *
     * @return korisničko ime korisnika, ili {@code null} ako korisničko ime nije postavljeno
     */
    public String getKorisnickoIme() { return korisnickoIme; }
    /**
     * Postavlja korisničko ime korisnika.
     *
     * @param korisnickoIme korisničko ime koje se postavlja za korisnika
     */
    public void setKorisnickoIme(String korisnickoIme) { this.korisnickoIme = korisnickoIme; }
    /**
     * Vraća lozinku korisnika.
     * 
     * @return lozinka korisnika, ili {@code null} ako lozinka nije postavljena
     */
    public String getKorisnickaLozinka() { return korisnickaLozinka; }
    /**
     * Postavlja lozinku korisnika.
     * 
     * @param korisnickaLozinka lozinka koja se postavlja za korisnika
     */
    public void setKorisnickaLozinka(String korisnickaLozinka) { this.korisnickaLozinka = korisnickaLozinka; }
    /**
     * Vraća ime i prezime korisnika.
     * 
     * @return ime i prezime korisnika, ili {@code null} ako nisu postavljeni
     */
    public String getImeIPrezime() { return imeIPrezime; }
    /**
     * Postavlja ime i prezime korisnika.   
     * @param imeIPrezime ime i prezime koje se postavlja za korisnika
     */
    public void setImeIPrezime(String imeIPrezime) { this.imeIPrezime = imeIPrezime; }
    /**
     * Vraća tip korisnika.
     * @return tip korisnika, ili {@code null} ako tip nije postavljen
     */
    public String getTip() { return tip; }
    /**
     * Postavlja tip korisnika.
     * @param tip tip koji se postavlja za korisnika
     */
    public void setTip(String tip) { this.tip = tip; }
    /**
     * Vraća adresu korisnika.
     * 
     * @return adresa korisnika, ili {@code null} ako adresa nije postavljena
     */
    public String getAdresa() { return adresa; }
    /**
     * Postavlja adresu korisnika.
     * @param adresa adresa koja se postavlja za korisnika
     */
    public void setAdresa(String adresa) { this.adresa = adresa; }
    /**
     * Vraća broj telefona korisnika.
     * @return broj telefona korisnika, ili {@code null} ako nije postavljen
     */
    public String getTelefon() { return telefon; }
    /**
     * Postavlja broj telefona korisnika.
     * @param telefon broj telefona koji se postavlja za korisnika
     */
    public void setTelefon(String telefon) { this.telefon = telefon; }
    /**
     * Vraća ime i prezime korisnika koje se koristi za pretragu korisnika.
     * @return ime i prezime korisnika, ili {@code null} ako nisu postavljeni
     */
    public String getImeiPrZaPretragu() { return imeiPrZaPretragu; }
    /**
     * Postavlja ime i prezime korisnika koje se koristi za pretragu korisnika.
     * @param imeiPrZaPretragu ime i prezime koje se postavlja za pretragu
     */
    public void setImeiPrZaPretragu(String imeiPrZaPretragu) { this.imeiPrZaPretragu = imeiPrZaPretragu; }
    /**
     * Vraća poruku koja se koristi za prikaz rezultata operacija ili grešaka.
     * @return poruka koja se koristi za prikaz rezultata operacija ili grešaka, ili {@code null} ako poruka nije postavljena
     */
    public String getFlasMessage() { return flasMessage; }
    /**
     * Postavlja poruku koja se koristi za prikaz rezultata operacija ili grešaka.
     * @param flasMessage poruka koja se postavlja za prikaz rezultata operacija ili grešaka, 
     */
    public void setFlasMessage(String flasMessage) { this.flasMessage = flasMessage; }
    /**
     * Vraća listu korisnika koji zadovoljavaju kriterijum pretrage.
     * @return lista korisnika koji zadovoljavaju kriterijum pretrage, ili prazna lista ako nema rezultata
     */
    public ArrayList<Korisnik> getPretragaKorisnika() { return pretragaKorisnika; }
    /**
     * Postavlja listu korisnika koji zadovoljavaju kriterijum pretrage.
     * @param pretragaKorisnika lista korisnika koja se postavlja kao rezultat pretrage
     */
    public void setPretragaKorisnika(ArrayList<Korisnik> pretragaKorisnika) { this.pretragaKorisnika = pretragaKorisnika; }
    /**
     * Vraća listu tipova korisnika koja se koristi za popunjavanje dropdown menija u korisničkom interfejsu.
     * @return lista tipova korisnika, ili prazna lista ako tipovi nisu postavljeni
     */
    public List<String> getTipovi() { return tipovi; }
    /**
     * Postavlja listu tipova korisnika koja se koristi za popunjavanje dropdown menija u korisničkom interfejsu.
     * @param tipovi lista tipova korisnika koja se postavlja
     */
    public void setTipovi(List<String> tipovi) { this.tipovi = tipovi; }
    /**
     * Vraća indikator da li je trenutno aktivno pretraživanje korisnika.
     * @return {@code true} ako je trenutno aktivno pretraživanje korisnika, {@code false} inače
     */
    public boolean isPretrazivanje() { return pretrazivanje; }
    /**
     * Postavlja indikator da li je trenutno aktivno pretraživanje korisnika.
     * @param pretrazivanje indikator koji se postavlja za kontrolu prikaza rezultata pretrage
     */
    public void setPretrazivanje(boolean pretrazivanje) { this.pretrazivanje = pretrazivanje; }
    /**
     * Postavlja ID trenutno selektovanog korisnika.
     * @param selektovaniID ID korisnika koji se postavlja
     */
    public void setSelektovaniID(int selektovaniID) {this.selektovaniID = selektovaniID;}
    /**
     * Vraća ID trenutno selektovanog korisnika.
     * @return ID trenutno selektovanog korisnika
     */
    public int getSelektovaniID() {return selektovaniID;}
    /**
     * Vraća listu udomljenih ljubimaca povezanih sa trenutno selektovanim korisnikom.
     * @return lista udomljenih ljubimaca, ili prazna lista ako nema udomljenih
     */
    public ArrayList<Udomljen> getUdomljeni() {return udomljeni;}
    /**
     * Postavlja listu udomljenih ljubimaca povezanih sa trenutno selektovanim korisnikom.
     * @param udomljeni lista udomljenih ljubimaca koja se postavlja
     */
    public void setUdomljeni(ArrayList<Udomljen> udomljeni) {this.udomljeni = udomljeni;}
    /**
     * Postavlja listu trenutnih rezervacija ljubimaca povezanih sa trenutno selektovanim korisnikom.
     * @param trenutnaRezervacija lista trenutnih rezervacija koja se postavlja
     */
    public void setTrenutnaRezervacija(ArrayList<Udomljen> trenutnaRezervacija) {this.trenutnaRezervacija = trenutnaRezervacija;}
    /**
     * Vraća listu trenutnih rezervacija ljubimaca povezanih sa trenutno selektovanim korisnikom.
     * @return lista trenutnih rezervacija, ili prazna lista ako nema rezervacija
     */
    public ArrayList<Udomljen> getTrenutnaRezervacija() {return trenutnaRezervacija;} 
    
}