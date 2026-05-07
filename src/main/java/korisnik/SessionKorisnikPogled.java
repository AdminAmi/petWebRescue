package korisnik;

import jakarta.inject.Named;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.context.FacesContext;
import java.io.IOException;
import java.io.Serializable;
import java.sql.SQLException;
import korisni.webUtil;

@Named(value = "sessionKorisnikPogled")
@SessionScoped
public class SessionKorisnikPogled implements Serializable {
    
    // Podaci o prijavljenom korisniku
    private Korisnik k;
    private CRUDKorisnik kont;
    private boolean loged = false;
    
    // Polja za login formu (ostaju u sesiji tokom cijele sesije)
    private String korisnickoIme;
    private String korisnickaLozinka;

    public SessionKorisnikPogled() {
        try {
            this.kont = new CRUDKorisnik();
        } catch (SQLException ex) {
            webUtil.errPoruka("Greška u inicijalizaciji: " + ex.getLocalizedMessage());
        }
    }

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

    public String logOff() {
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        return "/index?faces-redirect=true";
    }
    
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
     * Provjera da li je trenutni korisnik administrator 
     * 
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

    // Getteri i setteri
    public Korisnik getK() { return k; }
    public void setK(Korisnik k) { this.k = k; }
    public CRUDKorisnik getKont() { return kont; }
    public void setKont(CRUDKorisnik kont) { this.kont = kont; }
    public boolean isLoged() { return loged; }
    public void setLoged(boolean loged) { this.loged = loged; }
    public String getKorisnickoIme() { return korisnickoIme; }
    public void setKorisnickoIme(String korisnickoIme) { this.korisnickoIme = korisnickoIme; }
    public String getKorisnickaLozinka() { return korisnickaLozinka; }
    public void setKorisnickaLozinka(String korisnickaLozinka) { this.korisnickaLozinka = korisnickaLozinka; }
}