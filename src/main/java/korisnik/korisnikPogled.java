/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */
package korisnik;

import jakarta.inject.Named;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.context.FacesContext;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import klijent.Klijent;
import klijent.KlijentCRUD;
import korisni.webUtil;

/**
 *
 * @author TFB5
 */
@Named(value = "korisnikPogled")
@SessionScoped
public class korisnikPogled implements Serializable {
    private String korisnickoIme, korisnickaLozinka, imeIPrezime, tip;
    private Korisnik k;     
    private CRUDKorisnik kont;
    private KlijentCRUD kl;
    private List<String> tipovi = new ArrayList<>();
    private ArrayList<Korisnik> pretragaKorisnika = new ArrayList<>();
    private boolean loged = false;
    private String imeiPrZaPretragu;
    private String flasMessage;
    private String adresa;
    private String telefon;    

    public korisnikPogled() {
        try {
            this.kont = new CRUDKorisnik();
            this.kl = new KlijentCRUD();
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
    
    public String pretragaK(){
        //kont.vratiKojiZadovoljavajuUvjet(imeiPrZaPretragu, 0);
        pretragaKorisnika=kont.vratiKojiZadovoljavajuUvjet(imeiPrZaPretragu);
        return null;
    }
    
    public String registracija() throws SQLException{
        kont = new CRUDKorisnik();
        if(kont.login(korisnickoIme, korisnickaLozinka))
        {
            k= kont.getKorisnik();   
            korisnickoIme="";
            korisnickaLozinka="";
            loged=true;
            webUtil.testUsp("Uspješno logiranje!!!");
            return "pocetna?faces-redirect=true";
        }
        else 
        {
            loged=false;
            webUtil.errPoruka("Prijava na sistem","Netacni korisnički podaci!!!",""); 
            webUtil.infoPoruka("Test", "");
            webUtil.warPoruka("Test", "");
            return null;
        }
    }
    
    public String unosNovogKorisnikaWeb() {        
        try { 
            Korisnik unos = new Korisnik();
            unos.setImeIPrezime(imeIPrezime);
            unos.setId(0);
            unos.setUser(korisnickoIme);
            unos.setPass(korisnickaLozinka);
            unos.setTip("KORISNIK");
            kont.UnesiKorisnika(unos);
            //kont.UnesiKorisnika(new Korisnik(0, imeIPrezime, korisnickoIme, korisnickaLozinka,"KORISNIK")); 
            klijent.Klijent k = new Klijent(0,imeIPrezime, adresa, telefon);
            kl.dodajKlijenta(k);
            setFlasMessage("Korisnik: " + imeIPrezime + " je uspješno dodan u bazu.");
            webUtil.testUsp(flasMessage);
            korisnickoIme=null;
            return null;
        } catch (SQLException ex) {
            korisnickoIme=null;
            webUtil.errPoruka("Greška u unosu korisnika", ex.getMessage() + " " + ex.getSQLState(), "");
        }
        return null;
       
    }
     public String unosNovogAdministratoraWeb() {        
        try { 
            Korisnik unos = new Korisnik();
            unos.setImeIPrezime(imeIPrezime);
            unos.setId(0);
            unos.setUser(korisnickoIme);
            unos.setPass(korisnickaLozinka);
            unos.setTip("ADMINISTRATOR");
            kont.UnesiKorisnika(unos);
            //kont.UnesiKorisnika(new Korisnik(0, imeIPrezime, korisnickoIme, korisnickaLozinka,"ADMINISTRATOR"));             
            return "index?faces-redirect=true";
        } catch (SQLException ex) {
           webUtil.errPoruka("Greška u unosu korisnika", ex.getMessage() + " " + ex.getSQLState(), "");
        }
        return null;
       
    }
   
    public String azuriranjeKorisnika() throws SQLException{
        kont.azurirajKorisnika(k);
        kont.promjenaPassworda(k, k.getPass(), tip);
        //webUtil.infoPoruka("Uspješno ažuriranje korisnika","");
        webUtil.infoPoruka("Uspješno ažuriranje podataka korisnika", "");
        return null;
    }
    
     public String logOff() {       
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();     
        return "/index?faces-redirect=true";        
    }  
     
     public void infoMsg(){
         webUtil.infoPoruka("Testiram info", "");
     }

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
    
    
    
}
