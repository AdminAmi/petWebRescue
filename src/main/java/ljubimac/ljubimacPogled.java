/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */
package ljubimac;

import jakarta.inject.Named;
import jakarta.faces.view.ViewScoped;
import jakarta.servlet.http.Part;
import java.io.Serializable;
import java.util.ArrayList;
import korisnik.Korisnik;
import java.sql.SQLException;
import java.util.Date;
import korisni.DebugUtils;
import korisni.webUtil;
import udomljavanje.UdomljavanjeCRUD;

import udomljavanje.Udomljen;

/**
 * Managed Bean klasa za komunikaciju između XHTML stranica i logike ljubimaca.
 * Zadržani su originalni nazivi metoda radi kompatibilnosti sa postojećim JSF stranicama.
 * 
 * @author TFB5
 */
@Named(value = "ljubimacPogled")
@ViewScoped
public class ljubimacPogled implements Serializable {
    private LjubimacCRUD LjubimacK ;
    private ArrayList<Ljubimac> ljub=new ArrayList<>();
    private ArrayList<Udomljen> rezervisani = new ArrayList<>();
    private ArrayList<Udomljen> udomljeni = new ArrayList<>();
    private ArrayList<Udomljen> historijaAktivnosti = new ArrayList<>();
    private ArrayList<Ljubimac> psiSlobodni = new ArrayList<>();
    private ArrayList<Ljubimac> mackeSlobodne = new ArrayList<>();
    private ArrayList<Ljubimac> rezervisaniOdKorisnika = new ArrayList<>();
    private ArrayList<Ljubimac> pretragaLista = new ArrayList<>();
    private String prLjubimca;
    private Korisnik koris = new Korisnik();
    
    private int selektovaniID;
    private int selektovaniIDk;
    private Part datoteka;

    public ljubimacPogled() { 
        try {
            LjubimacK = new LjubimacCRUD();
            // Automatsko čišćenje isteklih rezervacija pri ulasku u pogled
            UdomljavanjeCRUD uc = new UdomljavanjeCRUD();
            uc.ponistiIstekleRezervacije();
        } catch (SQLException ex) {
            webUtil.errPoruka(ex.getLocalizedMessage());
        }
    }    
    /**
     * Trajno pohranjuje podatke o novom ljubimcu u bazu podataka.
     * Metoda automatski postavlja početni status ljubimca na "SLOBODAN" 
     * prije izvršavanja operacije dodavanja.
     * 
     * @return Navigacijski string za preusmjeravanje na početnu stranicu u slučaju uspjeha, 
     *         ili null ako dođe do greške.
     */
    public String snimiLjubimca(){
        try {
            LjubimacK.getLjubimac().setStatus(StanjeLjubimca.SLOBODAN.toString());
            LjubimacK.dodajLjubimca(LjubimacK.getLjubimac());
            webUtil.infoPoruka("Uspješan unos ljubimca", "");
            return "pocetna?faces-redirect=true";
        } catch (SQLException ex) {
            webUtil.errPoruka("Neuspješan unos: " + ex.getMessage(), "");
            return null;
        }         
    }
     /**
     * Vrši pretragu ljubimaca u bazi podataka na osnovu korisničkog unosa.
     * Pretraga se vrši prema imenu ljubimca koristeći vrijednost varijable {@code prLjubimca}.
     * Rezultati se pohranjuju u listu {@code pretragaLista}.
     * 
     * @return null (pogled ostaje na istoj stranici radi prikaza rezultata).
     */
    public String pretraga(){
         try {
            if (prLjubimca == null || prLjubimca.isEmpty()) {
                webUtil.infoPoruka("Unesite barem jedno slovo za pretraživanje", "");                                
            } else {
                pretragaLista = (ArrayList<Ljubimac>) LjubimacK.dobaviSveLjubimce(0, prLjubimca, 0);
                if (pretragaLista.isEmpty()) {
                    webUtil.testErr("Za unešene podatke nema rezultata!");
                }
            }
        } catch (SQLException ex) {
             webUtil.errPoruka("Greška u pretraživanju: " + ex.getMessage(), "");
        }
        return null;
    }
    /**
     * Inicijalizuje i osvježava sve kategorije listi ljubimaca iz baze podataka.
     * Dohvata slobodne pse, slobodne mačke, sve rezervacije, udomljene ljubimce, 
     * kao i specifične rezervacije vezane za trenutno ulogovanog korisnika.
     * 
     * @param idK Jedinstveni identifikator klijenta (korisnika) za kojeg se dohvataju personalizovane rezervacije.
     */
    public void ljubimci(int idK){         
        this.selektovaniID = idK; // Spremi ID za kasnije
//        ucitajMojeRezervacije();
        getLjub();
        ucitajSveRezervisane();
        ucitajSveUdomljene();
//      
    }  
   
    /**
     * Učitava podatke o specifičnom ljubimcu iz baze podataka na osnovu identifikatora 
     * postavljenog u varijabli {@code selektovaniID}.
     * Rezultat se postavlja u unutrašnji kontroler ljubimaca.
     */
    public void ucitajLjubimca(){
        try {
            LjubimacK.setLjubimac(LjubimacK.dobaviLjubimcaPoId(selektovaniID)); 
            UdomljavanjeCRUD uc = new UdomljavanjeCRUD();
            historijaAktivnosti=(ArrayList<Udomljen>) uc.dobaviHistorijuLjubimca(selektovaniID);
            } catch (SQLException ex) {
            webUtil.errPoruka("Greška u učitavanju ljubimaca" + ex, "");
        }
    }
    /**
     * VršI ažuriranje podataka postojećeg ljubimca u bazi podataka.
     * 
     * @param temp Objekat klase {@link Ljubimac} sa izmijenjenim podacima.
     */
    public void azurirajLjubimca(Ljubimac temp){
        try {
            LjubimacK.azurirajLjubimca(temp);
            webUtil.infoPoruka("Uspješno ažuriranje ljubimca", "");
        } catch (SQLException ex) {
           webUtil.errPoruka("Greška u učitavanju ljubimaca: " + ex, "");
        }
    }
     /**
     * Registruje novu rezervaciju ljubimca za određenog klijenta.
     * Metoda kreira zapis u tabeli udomljavanja i ažurira status samog ljubimca.
     * 
     * @param idK  Jedinstveni identifikator klijenta koji vrši rezervaciju.
     * @param temp Objekat ljubimca koji se rezerviše.
     * @return null (ostaje na istoj stranici).
     */
    public String rezervisiLjubimca(long idK, Ljubimac temp){        
        try {
            udomljavanje.UdomljavanjeCRUD rezervisi = new UdomljavanjeCRUD();
            if(!rezervisi.imaLiKlijentAktivnuRezervaciju(selektovaniIDk)){
                udomljavanje.Udomljen trenutni= new Udomljen();
                trenutni.setIdKlijenti((int) idK);
                trenutni.setIdLjubimac(selektovaniID);
                trenutni.setDatumUdomljavanja(webUtil.trenutniDatum());
                trenutni.setStatus(ljubimac.StanjeLjubimca.REZERVISAN.toString());
                rezervisi.dodajRelaciju(trenutni);
                temp.setStatus(ljubimac.StanjeLjubimca.REZERVISAN.toString());
                LjubimacK.azurirajLjubimca(temp);
                webUtil.infoPoruka("Uspješno ažuriranje ljubimca", "");
                psiSlobodni.clear();
                mackeSlobodne.clear();
                rezervisaniOdKorisnika.clear();
            }
            webUtil.infoPoruka("Imate aktivnu rezervaciju. Nemožete rezervirati više ljubimaca.", "");
        } catch (SQLException ex) {
            webUtil.errPoruka("Greška u rezervisanju ljubimaca" + ex, "");
        }
        return null;
    }
    /**
     * Potvrđuje proces udomljavanja za prethodno rezervisanog ili odabranog ljubimca.
     * Mijenja status relacije i ljubimca u "UDOMLJEN".
     * 
     * @param idK  Jedinstveni identifikator klijenta.
     * @param temp Objekat ljubimca koji se udomljava.
     * @return null (ostaje na istoj stranici).
     */
    public String udomiLjubimca(int idK, Ljubimac temp) {
        try {
            UdomljavanjeCRUD uc = new UdomljavanjeCRUD();
            Udomljen u = uc.dobaviRelaciju(idK, temp.getId());
            if (u != null) {
                u.setStatus(StanjeLjubimca.UDOMLJEN.toString());
                uc.azurirajUdomljavanje(u);
                temp.setStatus(StanjeLjubimca.UDOMLJEN.toString());
                LjubimacK.azurirajLjubimca(temp);
                webUtil.infoPoruka("Ljubimac uspješno udomljen", "");
                ljubimci(idK);
            }
        } catch (SQLException ex) {
            webUtil.errPoruka("Greška: " + ex.getMessage());
        }
        return null;
    }
    
 
    /**
     * Inicira proces vraćanja ljubimca koji je prethodno bio udomljen.
     * Koristi internu metodu {@link #izvrsiUklanjanje(int, int, int)} sa opcijom 2.
     * 
     * @param idK  ID klijenta.
     * @param idLJ ID ljubimca.
     * @return Rezultat izvršavanja interne metode.
     */
    public String ukloniUdomljenje(int idK, int idLJ) {        
        return izvrsiUklanjanje(idK, idLJ, 2);
    }

     /**
     * Inicira proces otkazivanja rezervacije ljubimca.
     * Koristi internu metodu {@link #izvrsiUklanjanje(int, int, int)} sa opcijom 1.
     * 
     * @param idK  ID klijenta.
     * @param idLJ ID ljubimca.
     * @return Rezultat izvršavanja interne metode.
     */
    public String ukloniRezervaciju(int idK, int idLJ) { 
        DebugUtils.debugPrint(idK,idLJ);        
        return izvrsiUklanjanje(idK, idLJ, 1);
    }

     /**
     * Zajednička interna metoda za procesuiranje povrata ili otkazivanja relacija.
     * Na osnovu opcije definiše da li je riječ o otkazivanju rezervacije ili povratu životinje.
     * 
     * @param idK    Jedinstveni identifikator klijenta.
     * @param idLJ   Jedinstveni identifikator ljubimca.
     * @param opcija Logički ključ (1 za otkaz rezervacije, 2 za povrat udomljenja).
     * @return null (ostaje na istoj stranici).
     */
    private String izvrsiUklanjanje (int idK, int idLJ, int opcija) {
        try {
            UdomljavanjeCRUD uc = new UdomljavanjeCRUD();
            if (opcija==1 ) 
            {
                if(uc.otkaziAktivnuRezervaciju(idK, idLJ)){
                     webUtil.infoPoruka("Rezervacija otkazana.", "");
                     ljubimci(selektovaniID);
                }
            }
            else uc.vratiLjubimcaSaUdomljavanja(idLJ, idLJ);

            //String poruka = (opcija == 1) ? "Rezervacija otkazana." : "Ljubimac uspješno vraćen.";
            

            //ljubimci(idK); // Osvježavanje listi na UI
        } catch (SQLException ex) {
            webUtil.errPoruka("Greška: " + ex.getMessage());
        }
        return  "ljubimci?faces-redirect=true";
    }
    
    
    

    /**
     * Učitava sve pse (AJAX) - poziva se sa f:ajax
     */
    public void ucitajSvePse() {
        try {
            System.out.println("AJAX - Učitavanje pasa...");
            psiSlobodni = (ArrayList<Ljubimac>) LjubimacK.dobaviSveLjubimce(1, "", 1);
        } catch (SQLException ex) {
            webUtil.errPoruka("Greška pri učitavanju pasa: " + ex.getMessage());
        }
    }

    /**
     * Učitava sve mačke (AJAX)
     */
    public void ucitajSveMacke() {
        try {
            System.out.println("AJAX - Učitavanje mačaka...");
            mackeSlobodne = (ArrayList<Ljubimac>) LjubimacK.dobaviSveLjubimce(2, "", 1);
        } catch (SQLException ex) {
            webUtil.errPoruka("Greška pri učitavanju mačaka: " + ex.getMessage());
        }
    }

    /**
     * Učitava sve rezervisane ljubimce (za administratore)
     */
    public void ucitajSveRezervisane() {
        try {
            rezervisani.clear();
            UdomljavanjeCRUD uc = new UdomljavanjeCRUD();
            rezervisani = (ArrayList<Udomljen>) uc.dobaviSveRezervacije();
        } catch (SQLException ex) {
            webUtil.errPoruka("Greška pri učitavanju rezervisanih: " + ex.getMessage());
        }
    }
     public void ucitajSveRezervisaneZaKorisnika(int idK) {
        try {
            rezervisani.clear();
            UdomljavanjeCRUD uc = new UdomljavanjeCRUD();
            rezervisani = (ArrayList<Udomljen>) uc.dobaviSveRezervacijeZaKorisnika(idK);
        } catch (SQLException ex) {
            webUtil.errPoruka("Greška pri učitavanju rezervisanih: " + ex.getMessage());
        }
    }

    /**
     * Učitava sve udomljene ljubimce (za administratore)
     */
    public void ucitajSveUdomljene() {
        try {
            System.out.println("AJAX - Učitavanje udomljenih...");
            UdomljavanjeCRUD uc = new UdomljavanjeCRUD();
            udomljeni = (ArrayList<Udomljen>) uc.dobaviSvaUdomljavanja();
        } catch (SQLException ex) {
            webUtil.errPoruka("Greška pri učitavanju udomljenih: " + ex.getMessage());
        }
    }

    /**
     * Učitava rezervacije trenutnog korisnika
     */
    public void ucitajMojeRezervacije() {
        if (selektovaniID > 0) {
            try {
                System.out.println("AJAX - Učitavanje mojih rezervacija za ID: " + selektovaniID);
                rezervisaniOdKorisnika = (ArrayList<Ljubimac>) LjubimacK.dobaviSveLjubimceKorisnika(selektovaniID);
            } catch (SQLException ex) {
                webUtil.errPoruka("Greška pri učitavanju rezervacija: " + ex.getMessage());
            }
        }
    }

    // OPCIONO: Metoda za resetovanje svih listi (ako želiš da osvježiš podatke nakon akcije)
    public void resetujListe() {
        psiSlobodni.clear();
        mackeSlobodne.clear();
        rezervisani.clear();
        udomljeni.clear();
        rezervisaniOdKorisnika.clear();
        ljub.clear();
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

    /**
     *
     * @return
     */
    public ArrayList<Ljubimac> getLjub() {
        if (ljub.isEmpty()) {
            try {
                psiSlobodni = (ArrayList<Ljubimac>) LjubimacK.dobaviSveLjubimce(1, "", 1);
                mackeSlobodne = (ArrayList<Ljubimac>) LjubimacK.dobaviSveLjubimce(2, "", 1);
                ljub.clear();
                ljub.addAll(psiSlobodni);
                ljub.addAll(mackeSlobodne);
            } catch (SQLException ex) {
                webUtil.errPoruka("Greška pri učitavanju pasa: " + ex.getMessage());
            }
        }
        return ljub;
    }

    public void setLjub(ArrayList<Ljubimac> ljub) {
        this.ljub = ljub;
    }

    /**
     *
     * @return
     */
    public ArrayList<Udomljen> getRezervisani() {
        
        
        return rezervisani;
    }  
    public void setRezervisani(ArrayList<Udomljen> rezervisani) {this.rezervisani = rezervisani;}
    public ArrayList<Udomljen> getUdomljeni() {return udomljeni;}
    public void setUdomljeni(ArrayList<Udomljen> udomljeni) {this.udomljeni = udomljeni;}    
    public ArrayList<Ljubimac> getPsiSlobodni() {return psiSlobodni;}
    public void setPsiSlobodni(ArrayList<Ljubimac> psiSlobodni) {this.psiSlobodni = psiSlobodni;}
    public ArrayList<Ljubimac> getMackeSlobodne() {return mackeSlobodne;}

    public void setMackeSlobodne(ArrayList<Ljubimac> mackeSlobodne) {
        this.mackeSlobodne = mackeSlobodne;
    }
    
    

    /**
 
 * @return Lista rezervisanih ljubimaca klijenta.
 */
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

    public int getSelektovaniIDk() {
        return selektovaniIDk;
    }

    public void setSelektovaniIDk(int selektovaniIDk) {
        this.selektovaniIDk = selektovaniIDk;
    }

    public void setHistorijaAktivnosti(ArrayList<Udomljen> historijaAktivnosti) {
        this.historijaAktivnosti = historijaAktivnosti;
    }
    
    

    public ArrayList<Udomljen> getHistorijaAktivnosti() {
        return historijaAktivnosti;
    }
    
    

   
}
