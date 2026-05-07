package korisnik;

import jakarta.inject.Named;
import jakarta.faces.view.ViewScoped;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import korisni.webUtil;

@Named(value = "webKorisnikPogled")
@ViewScoped
public class webKorisnikPogled implements Serializable {

    // Polja za formu (ne čuvaju se u sesiji)
    private String korisnickoIme; 
    private String korisnickaLozinka;
    private String imeIPrezime;
    private String tip;
    private String adresa;
    private String telefon;
    private String imeiPrZaPretragu;
    private String flasMessage;
    private int selektovaniID;

    // Rezultati pretrage
    private ArrayList<Korisnik> pretragaKorisnika = new ArrayList<>();
    private List<String> tipovi = new ArrayList<>();
    private boolean pretrazivanje = false;
    
    // Session bean (injektiran)
    private SessionKorisnikPogled sessionKorisnik;
    
    

    
    public webKorisnikPogled() {
        inicijalisiDropDown();
    }

    @jakarta.inject.Inject
    public void setSessionKorisnik(SessionKorisnikPogled sessionKorisnik) {
        this.sessionKorisnik = sessionKorisnik;
    }

    private void inicijalisiDropDown() {
        tipovi.add("ADMINISTRATOR");
        tipovi.add("KORISNIK");
    }

    private boolean isAdmin() {
        return sessionKorisnik != null && 
               sessionKorisnik.getK() != null && 
               "ADMINISTRATOR".equals(sessionKorisnik.getK().getTip());
    }
    
    public void ucitajKorisnika(){
        try {
            sessionKorisnik.getKont().setKorisnik
            (sessionKorisnik.getKont().vratiKorisnikaPoID(selektovaniID));
        } catch (SQLException ex) {
            webUtil.errPoruka("Greška u učitavanju ljubimaca" + ex, "");
        }
    }

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

    public String azuriranjeKorisnika() throws SQLException {
        if (sessionKorisnik.getK() != null) {
            sessionKorisnik.getKont().azurirajKorisnika(sessionKorisnik.getK());
            webUtil.infoPoruka("Uspješno ažuriranje podataka korisnika", "");
            resetPolja();
        }
        return null;
    }

    public String azuriranjeKorisnika(Korisnik kor) throws SQLException {
        System.out.println("kor");
        if (sessionKorisnik.getKont() != null) {
            sessionKorisnik.getKont().azurirajKorisnika(kor);
            webUtil.infoPoruka("Uspješno ažuriranje podataka korisnika", "");
            resetPolja();
        }
        return null;
    }

    public String azuriranjePassworda() throws SQLException {
        if (sessionKorisnik.getK() != null && sessionKorisnik.getKont() != null) {
            sessionKorisnik.getKont().promjenaPassworda(
                sessionKorisnik.getK(), 
                sessionKorisnik.getK().getPass()
            );
            webUtil.infoPoruka("Uspješno ažuriranje podataka korisnika", "");
        }
        return null;
    }

    public void resetPolja() {
        korisnickoIme = null;
        imeIPrezime = null;
        adresa = null;
        korisnickaLozinka = null;
        telefon = null;
        imeiPrZaPretragu = null;
    }

    // Getteri i setteri za ViewScope polja
    public String getKorisnickoIme() { return korisnickoIme; }
    public void setKorisnickoIme(String korisnickoIme) { this.korisnickoIme = korisnickoIme; }
    public String getKorisnickaLozinka() { return korisnickaLozinka; }
    public void setKorisnickaLozinka(String korisnickaLozinka) { this.korisnickaLozinka = korisnickaLozinka; }
    public String getImeIPrezime() { return imeIPrezime; }
    public void setImeIPrezime(String imeIPrezime) { this.imeIPrezime = imeIPrezime; }
    public String getTip() { return tip; }
    public void setTip(String tip) { this.tip = tip; }
    public String getAdresa() { return adresa; }
    public void setAdresa(String adresa) { this.adresa = adresa; }
    public String getTelefon() { return telefon; }
    public void setTelefon(String telefon) { this.telefon = telefon; }
    public String getImeiPrZaPretragu() { return imeiPrZaPretragu; }
    public void setImeiPrZaPretragu(String imeiPrZaPretragu) { this.imeiPrZaPretragu = imeiPrZaPretragu; }
    public String getFlasMessage() { return flasMessage; }
    public void setFlasMessage(String flasMessage) { this.flasMessage = flasMessage; }
    public ArrayList<Korisnik> getPretragaKorisnika() { return pretragaKorisnika; }
    public void setPretragaKorisnika(ArrayList<Korisnik> pretragaKorisnika) { this.pretragaKorisnika = pretragaKorisnika; }
    public List<String> getTipovi() { return tipovi; }
    public void setTipovi(List<String> tipovi) { this.tipovi = tipovi; }
    public boolean isPretrazivanje() { return pretrazivanje; }
    public void setPretrazivanje(boolean pretrazivanje) { this.pretrazivanje = pretrazivanje; }
    public void setSelektovaniID(int selektovaniID) {this.selektovaniID = selektovaniID;}
    public int getSelektovaniID() {return selektovaniID;}
    
    
    
}