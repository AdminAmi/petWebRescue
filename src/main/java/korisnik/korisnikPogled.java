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
    private List<String> tipovi = new ArrayList<>();
    private ArrayList<Korisnik> pretragaKorisnika = new ArrayList<>();
    private boolean loged = false;
    private String imeiPrZaPretragu;
    private String flasMessage;
    private String adresa;
    private String telefon;    

    /**
     * Konstruktor - inicijalizira CRUD operacije nad korisnicima.
     * U slučaju greške pri inicijalizaciji baze, prikazuje odgovarajuću poruku.
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
    
    public String pretragaK(){
        try {
            //kont.vratiKojiZadovoljavajuUvjet(imeiPrZaPretragu, 0);
            pretragaKorisnika=kont.vratiKojiZadovoljavajuUvjet(imeiPrZaPretragu);
            return null;
        } catch (SQLException ex) {
            System.getLogger(korisnikPogled.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
        return null;
    }
    
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
    
    public String unosNovogKorisnikaWeb(int tip) {        
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
    public void resetPolja(){
        korisnickoIme=null;
        imeIPrezime=null;
        adresa=null;
        korisnickaLozinka=null;
        telefon=null;
    }
    
    public String unosNovogAdministratoraWeb() {        
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
   
    public String azuriranjeKorisnika() throws SQLException{
        kont.azurirajKorisnika(k);
        //kont.promjenaPassworda(k, k.getPass(), tip);
        //webUtil.infoPoruka("Uspješno ažuriranje korisnika","");
        webUtil.infoPoruka("Uspješno ažuriranje podataka korisnika", "");
        resetPolja();
        return null;
    }
    
    public String azuriranjePassworda() throws SQLException{
        //kont.azurirajKorisnika(k);
        kont.promjenaPassworda(k, k.getPass());
        //kont.promjenaPassworda(k, k.getPass(), tip);
        //webUtil.infoPoruka("Uspješno ažuriranje korisnika","");
        webUtil.infoPoruka("Uspješno ažuriranje podataka korisnika", "");
        return null;
    }
    
     public String logOff() {       
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        resetPolja();
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
