/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */
package ljubimac;

import jakarta.inject.Named;
import jakarta.faces.view.ViewScoped;
import jakarta.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.util.ArrayList;
import korisnik.Korisnik;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import korisni.webUtil;
import korisnik.CRUDKorisnik;

/**
 *
 * @author TFB5
 */
@Named(value = "ljubimacPogled")
@ViewScoped
public class ljubimacPogled implements Serializable {
    private LjubimacCRUD LjubimacK ;
    private ArrayList<Ljubimac> ljub=new ArrayList<>();
    private ArrayList<Ljubimac> rezervisani = new ArrayList<>();
    private ArrayList<Ljubimac> udomljeni = new ArrayList<>();
    private ArrayList<Ljubimac> psiSlobodni = new ArrayList<>();
    private ArrayList<Ljubimac> mackeSlobodne = new ArrayList<>();
    private ArrayList<Ljubimac> rezervisaniOdKorisnika = new ArrayList<>();
    private ArrayList<Ljubimac> pretragaLista = new ArrayList<>();
    private String prLjubimca;
    private Korisnik koris = new Korisnik();
    private int selektovaniID;
    private Part datoteka;

    public ljubimacPogled() { 
        try {
            LjubimacK = new LjubimacCRUD();
        } catch (SQLException ex) {
            webUtil.errPoruka(ex.getLocalizedMessage());
        }
    }    
    
    public String snimiLjubimca(){
//        try {
//            LjubimacK.getLjubimac().setStatus("SLOBODAN");
//            //LjubimacK.getLjubimac().setIdK(0);
//            LjubimacK.dodajLjubimca(LjubimacK.getLjubimac());
//            //snimiSliku(LjubimacK.Ljubimac.getId());
//            webUtil.infoPoruka("Uspješan unos ljubimca", "");
//        } catch (SQLException ex) {
//            webUtil.errPoruka("Neuspješan unos!!!" + ex,"");
//            return null;
//        } 
        return "pocetna"; 
//        
    }
    
    public String pretraga(){
        try {
            if(prLjubimca.isEmpty()) {
                webUtil.infoPoruka("Unesite baren jedbo slovo za pretraživanje", "");
                pretragaLista=null;
                return null;
            }
            pretragaLista = (ArrayList<Ljubimac>) LjubimacK.dobaviSveLjubimce(0,prLjubimca,0);
            System.out.println("Ljubimac.ljubimacPogled.pretraga()" + String.valueOf(pretragaLista.size()));
        } catch (SQLException ex) {
             webUtil.errPoruka("Greška u pretraživanju ljubimaca" + ex, "");
        }
        return null;
    }
   
    public void ljubimci(int idK){
//        try {
//            ArrayList<ljubimac> ukloniRez = new ArrayList<>();
//            ukloniRez = LjubimacK.vratiLjubimceIstekleRezervacije();            
//            for (ljubimac object : ukloniRez) {
//                LjubimacK.ukloniRezervacija(object.getId());  
//                //webUtil.infoPoruka("Broj za obrisati rez: " + String.valueOf(object.getId()), "");
//            }
//            ljub=LjubimacK.vratiLjubimce(1);
//            rezervisani=LjubimacK.vratiLjubimce(2);
//            udomljeni=LjubimacK.vratiLjubimce(3);
//            psiSlobodni=LjubimacK.vratiLjubimce(4);
//            mackeSlobodne=LjubimacK.vratiLjubimce(5);
//            rezervisaniOdKorisnika=LjubimacK.vratiRezervisane(idK);
//            
//            
//        } catch (SQLException ex) {
//            webUtil.errPoruka("Greška u učitavanju ljubimaca" + ex, "");
//        }
    }
    public void slobodniLjubimci(){
//        try {
//            ljub=LjubimacK.vratiLjubimce(1);            
//        } catch (SQLException ex) {
//            webUtil.errPoruka("Greška u učitavanju ljubimaca" + ex, "");
//        }
    }
    public void rezervisaniLjubimci(){
//        try {
//            rezervisani=LjubimacK.vratiLjubimce(2);            
//        } catch (SQLException ex) {
//            webUtil.errPoruka("Greška u učitavanju ljubimaca" + ex, "");
//        }
    }
    public void udomljeniLjubimci(){
//        try {
//            udomljeni=LjubimacK.vratiLjubimce(3);            
//        } catch (SQLException ex) {
//            webUtil.errPoruka("Greška u učitavanju ljubimaca" + ex, "");
//        }
    }
    public void dostupniPsi(){
//        try {
//            psiSlobodni=LjubimacK.vratiLjubimce(4);            
//        } catch (SQLException ex) {
//            webUtil.errPoruka("Greška u učitavanju ljubimaca" + ex, "");
//        }
    }
    public void dostupneMacke(){
//        try {
//            mackeSlobodne=LjubimacK.vratiLjubimce(5);            
//        } catch (SQLException ex) {
//            webUtil.errPoruka("Greška u učitavanju ljubimaca" + ex, "");
//        }
    }
    
    public void ucitajLjubimca(){
//        try {
//            LjubimacK.setLjubimac(LjubimacK.vratiLjubimcaPoID(selektovaniID));
//            if(LjubimacK.getLjubimac().getIdK()>0){
//                korisnikKontroler kk = new korisnikKontroler();
//                setKoris(kk.VratiKorisnikaPoID(LjubimacK.getLjubimac().getIdK()));
//                }
//            } catch (SQLException ex) {
//            webUtil.errPoruka("Greška u učitavanju ljubimaca" + ex, "");
//        }
    }
    
    public void azurirajLjubimca(Ljubimac temp){
//        try {
//            LjubimacK.azurirajLjubimca(temp);
//            webUtil.infoPoruka("Uspješno ažuriranje ljubimca", "");
//        } catch (SQLException ex) {
//           webUtil.errPoruka("Greška u učitavanju ljubimaca" + ex, "");
//        }
    }
    public String rezervisiLjubimca(long idK){
//        try {
//            LjubimacK.unesiRezervacija(idK, selektovaniID);
//            webUtil.infoPoruka("Uspješno ažuriranje ljubimca", "");
//        } catch (SQLException ex) {
//            webUtil.errPoruka("Greška u rezervisanju ljubimaca" + ex, "");
//        }
        return null;
    }
     public String udomiLjubimca(){
//        try {
//            LjubimacK.unesiUdomljenje(selektovaniID);
//            webUtil.infoPoruka("Uspješno udomljenje ljubimca", "");
//        } catch (SQLException ex) {
//            webUtil.errPoruka("Greška u udoljenju ljubimaca" + ex, "");
//        }
        return null;
    }
    
    public String snimiSliku(int id) {
//        String naziv,ekstenzija="";        
//        if(getDatoteka()!=null){
//            naziv=getDatoteka().getSubmittedFileName();
//            webUtil.infoPoruka(naziv, "");
//            int i=naziv.lastIndexOf('.');
//            if(i>0)ekstenzija=naziv.substring(i+1);
//            naziv=String.valueOf(id)+"."+ekstenzija;
//            try {
//                //PK.unesiAvatar(naziv,id );
//                LjubimacK.unesiSliku("/resources/img/"+naziv, id);
//            } catch (SQLException ex) {
//                webUtil.errPoruka("Greška u SQL operaciji :" + ex, "");
//                return null;
//            }           
//            Path p = (Path)Paths.get(webUtil.vratiPath(), String.valueOf(id)+"."+ekstenzija);
//            try (InputStream input = getDatoteka().getInputStream()) {
//                
//                Files.copy(input,p,StandardCopyOption.REPLACE_EXISTING);
//                webUtil.resize(p.toString());                
//            } catch (IOException ex) {
//                webUtil.errPoruka("Greška u IO operaciji :" + ex, "");
//                return null;
//            }
//            return "ljubimacInfo?faces-redirect=true&includeViewParams=true";
//        } 
        return null;
    }
    public String ukloniUdomljenje(){        
//        try {
//            LjubimacK.ukloniUdomljenje(selektovaniID);
//            webUtil.infoPoruka("Uspješno ukolonjeno udomljenje ljubimca", "");
//        } catch (SQLException ex) {
//            webUtil.errPoruka("Greška u uklonjenju udomljenja ljubimaca" + ex, "");
//        }
        return null;
    }
    public String ukloniUdomljenje(int idLJ){        
//        try {
//            LjubimacK.ukloniUdomljenje(idLJ);
//            webUtil.infoPoruka("Uspješno ukolonjeno udomljenje ljubimca", "");
//        } catch (SQLException ex) {
//            webUtil.errPoruka("Greška u uklonjenju udomljenja ljubimaca" + ex, "");
//        }
        return null;
    }
     public String ukloniRezervaciju(int idLJ){        
//        try {
//            LjubimacK.ukloniRezervacija(idLJ);
//            webUtil.infoPoruka("Uspješno ukolonjena rezervacija ljubimca", "");
//        } catch (SQLException ex) {
//            webUtil.errPoruka("Greška u uklonjenju udomljenja ljubimaca" + ex, "");
//        }
        return null;
    }
     
     public void pretragaSaTastaturom(){
         System.out.println("aaaa");
         this.pretraga();
     }
    
    public LjubimacCRUD getLjubimacK() {
        return LjubimacK;
    }

    public void setLjubimacK(LjubimacCRUD LjubimacK) {
        this.LjubimacK = LjubimacK;
    }

    public ArrayList<Ljubimac> getLjub() {
        return ljub;
    }

    public void setLjub(ArrayList<Ljubimac> ljub) {
        this.ljub = ljub;
    }

    public ArrayList<Ljubimac> getRezervisani() {
        return rezervisani;
    }

    public void setRezervisani(ArrayList<Ljubimac> rezervisani) {
        this.rezervisani = rezervisani;
    }

    public ArrayList<Ljubimac> getUdomljeni() {
        return udomljeni;
    }

    public void setUdomljeni(ArrayList<Ljubimac> udomljeni) {
        this.udomljeni = udomljeni;
    }

    public ArrayList<Ljubimac> getPsiSlobodni() {
        return psiSlobodni;
    }

    public void setPsiSlobodni(ArrayList<Ljubimac> psiSlobodni) {
        this.psiSlobodni = psiSlobodni;
    }

    public ArrayList<Ljubimac> getMackeSlobodne() {
        return mackeSlobodne;
    }

    public void setMackeSlobodne(ArrayList<Ljubimac> mackeSlobodne) {
        this.mackeSlobodne = mackeSlobodne;
    }

    public ArrayList<Ljubimac> getRezervisaniOdKorisnika() {
        return rezervisaniOdKorisnika;
    }

    public void setRezervisaniOdKorisnika(ArrayList<Ljubimac> rezervisaniOdKorisnika) {
        this.rezervisaniOdKorisnika = rezervisaniOdKorisnika;
    }

    public ArrayList<Ljubimac> getPretragaLista() {
        return pretragaLista;
    }

    public void setPretragaLista(ArrayList<Ljubimac> pretragaLista) {
        this.pretragaLista = pretragaLista;
    }

    public String getPrLjubimca() {
        return prLjubimca;
    }

    public void setPrLjubimca(String prLjubimca) {
        this.prLjubimca = prLjubimca;
    }

    public Korisnik getKoris() {
        return koris;
    }

    public void setKoris(Korisnik koris) {
        this.koris = koris;
    }

    public int getSelektovaniID() {
        return selektovaniID;
    }

    public void setSelektovaniID(int selektovaniID) {
        this.selektovaniID = selektovaniID;
    }

    public Part getDatoteka() {
        return datoteka;
    }

    public void setDatoteka(Part datoteka) {
        this.datoteka = datoteka;
    }

   
}
