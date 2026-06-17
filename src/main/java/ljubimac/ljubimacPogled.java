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
 * Radi u opsegu @ViewScope i omogućuje sve potrebne operacije sa ljubimcima
 * 
 * @author Amel Džanić
 * @version 1.2
 */
 
@Named(value = "ljubimacPogled")
@ViewScoped
public class ljubimacPogled implements Serializable {
    /** Instanca klase LjubimacCRUD za rad sa podacima o ljubimcima. */
    private LjubimacCRUD LjubimacK ;
    /** Lista svih ljubimaca u sistemu. */
    private ArrayList<Ljubimac> ljub=new ArrayList<>();
    /** Lista rezervisanih ljubimaca. */
    private ArrayList<Udomljen> rezervisani = new ArrayList<>();
    /** Lista udomljenih ljubimaca. */
    private ArrayList<Udomljen> udomljeni = new ArrayList<>();
    /** Lista historije aktivnosti. */
    private ArrayList<Udomljen> historijaAktivnosti = new ArrayList<>();
    /** Lista slobodnih pasa. */
    private ArrayList<Ljubimac> psiSlobodni = new ArrayList<>();
    /** Lista slobodnih mačaka. */
    private ArrayList<Ljubimac> mackeSlobodne = new ArrayList<>();
    /** Lista rezervisanih ljubimaca od strane trenutno ulogovanog korisnika. */
    private ArrayList<Udomljen> rezervisaniOdKorisnika = new ArrayList<>();
    /** Lista rezultata pretrage. */
    private ArrayList<Ljubimac> pretragaLista = new ArrayList<>();
    /** Varijabla za čuvanje unosa korisnika za pretragu. */
    private String prLjubimca;
    /** Trenutno ulogovani korisnik. */
    private Korisnik koris = new Korisnik();
    /** ID trenutno selektovanog ljubimca. */
    private int selektovaniID;
    /** ID trenutno selektovanog korisnika. */
    private int selektovaniIDk;
    /** Varijabla za privremeno čuvanje datoteke (npr. slike ljubimca). */
    private Part datoteka;
    /** Indikator da li je trenutno aktivno pretraživanje. */
    private boolean pretrazivanje;
    /**
     * Konstruktor za inicijalizaciju managed bean-a.
     * Inicijalizuje instancu {@link LjubimacCRUD} i automatski 
     * poziva metodu za poništavanje isteklih rezervacija.
     * Ukoliko dođe do greške prilikom inicijalizacije, prikazuje se odgovarajuća poruka o grešci.
     */
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
                pretrazivanje = true;
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
        ucitajSveRezervisaneZaKorisnika(idK);
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
            else uc.vratiLjubimcaSaUdomljavanja(idK, idLJ);

            //String poruka = (opcija == 1) ? "Rezervacija otkazana." : "Ljubimac uspješno vraćen.";
            

            //ljubimci(idK); // Osvježavanje listi na UI
        } catch (SQLException ex) {
            webUtil.errPoruka("Greška: " + ex.getMessage());
        }
        return  "ljubimci?faces-redirect=true";
    }  
    /**
     * Metoda za učitavanje svih slobodnih pasa iz baze podataka
     * Koristi metodu {@link LjubimacCRUD#dobaviSveLjubimce(int, String, int)} 
     * sa parametrima za pse.
     * U slučaju greške prilikom učitavanja, prikazuje se odgovarajuća poruka o grešci.
     */
    public void ucitajSvePse() {
        try {            
            psiSlobodni = (ArrayList<Ljubimac>) 
            LjubimacK.dobaviSveLjubimce(1, "", 1);
        } catch (SQLException ex) {
            webUtil.errPoruka("Greška pri učitavanju pasa: " + ex.getMessage());
        }
    }
     /**
     * Metoda za učitavanje svih slobodnih mačaka iz baze podataka
     * Koristi metodu {@link LjubimacCRUD#dobaviSveLjubimce(int, String, int)} 
     * sa parametrima za mačke.
     * U slučaju greške prilikom učitavanja, prikazuje se odgovarajuća poruka o grešci.
     */
    public void ucitajSveMacke() {
        try {            
            mackeSlobodne = (ArrayList<Ljubimac>) 
            LjubimacK.dobaviSveLjubimce(2, "", 1);
        } catch (SQLException ex) {
            webUtil.errPoruka("Greška pri učitavanju mačaka: " + ex.getMessage());
        }
    }

    /**
     * Učitava sve rezervisane ljubimce (za administratore)
     * Koristi metodu {@link UdomljavanjeCRUD#dobaviSveRezervacije()} za dohvat svih rezervacija.
     * U slučaju greške prilikom učitavanja, prikazuje se odgovarajuća poruka o grešci.
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
    /**
     * Učitava sve rezervisane ljubimce specifično za trenutno ulogovanog korisnika.
     * Koristi metodu {@link UdomljavanjeCRUD#dobaviSveRezervacijeZaKorisnika(int)} sa ID-jem korisnika kao parametrom. 
     * Rezultati se pohranjuju u listu {@code rezervisaniOdKorisnika}.
     * @param idK Jedinstveni identifikator korisnika za kojeg se učitavaju rezervacije.
     */
     public void ucitajSveRezervisaneZaKorisnika(int idK) {
        try {
            rezervisaniOdKorisnika.clear();
            UdomljavanjeCRUD uc = new UdomljavanjeCRUD();
            rezervisaniOdKorisnika= (ArrayList<Udomljen>) uc.dobaviSveRezervacijeZaKorisnika(idK);
        } catch (SQLException ex) {
            webUtil.errPoruka("Greška pri učitavanju rezervisanih: " + ex.getMessage());
        }
    }

    /**
     * Učitava sve udomljene ljubimce (za administratore)
     * Koristi metodu {@link UdomljavanjeCRUD#dobaviSvaUdomljavanja()} za dohvat svih udomljavanja.
     * U slučaju greške prilikom učitavanja, prikazuje se odgovarajuća poruka o grešci.
     */
    public void ucitajSveUdomljene() {
        try {            
            UdomljavanjeCRUD uc = new UdomljavanjeCRUD();
            udomljeni = (ArrayList<Udomljen>) uc.dobaviSvaUdomljavanja();
        } catch (SQLException ex) {
            webUtil.errPoruka("Greška pri učitavanju udomljenih: " + ex.getMessage());
        }
    } 

    /**
     * Poništava sve trenutne liste ljubimaca i rezervacija, vraćajući ih u početno stanje.
     */
    public void resetujListe() {
        psiSlobodni.clear();
        mackeSlobodne.clear();
        rezervisani.clear();
        udomljeni.clear();
        rezervisaniOdKorisnika.clear();
        ljub.clear();
    }     
    /**
     * Vraća instancu {@link LjubimacCRUD} koja se koristi za upravljanje podacima o ljubimcima.
     * @return Instanca klase {@link LjubimacCRUD} koja se koristi u ovom managed bean-u.
     */    
    public LjubimacCRUD getLjubimacK() {
        return LjubimacK;
    }
    /**
     * Postavlja instancu {@link LjubimacCRUD} koja se koristi za upravljanje podacima o ljubimcima.
     * @param LjubimacK Instanca klase {@link LjubimacCRUD} koja se postavlja.
     */
    public void setLjubimacK(LjubimacCRUD LjubimacK) {
        this.LjubimacK = LjubimacK;
    }

    /**
     * Vraća listu svih ljubimaca. Ako je lista prazna,
     * učitava slobodne pse i mačke iz baze podataka i kombinuje ih u jednu listu.
     * Koristi se metoda {@link LjubimacCRUD#dobaviSveLjubimce(int, String, int)} 
     * za dohvat podataka o ljubimcima.
     * @return Lista svih ljubimaca.
     */
    public ArrayList<Ljubimac> getLjub() {
        if (ljub.isEmpty()) {
            try {
                psiSlobodni = (ArrayList<Ljubimac>)
                 LjubimacK.dobaviSveLjubimce(1, "", 1);
                mackeSlobodne = (ArrayList<Ljubimac>) 
                 LjubimacK.dobaviSveLjubimce(2, "", 1);
                ljub.clear();
                ljub.addAll(psiSlobodni);
                ljub.addAll(mackeSlobodne);
            } catch (SQLException ex) {
                webUtil.errPoruka("Greška pri učitavanju pasa: " + ex.getMessage());
            }
        }
        return ljub;
    }
    /**
     * Postavlja listu svih ljubimaca. Ova metoda se koristi za direktno postavljanje liste ljubimaca,
     * ali u praksi se lista obično popunjava pozivom metode {@link #getLjub()} koja dohvaća podatke iz baze podataka.
     * @param ljub Lista svih ljubimaca koja se postavlja.
     */
    public void setLjub(ArrayList<Ljubimac> ljub) {
        this.ljub = ljub;
    }

    /**
     * Vraća listu rezervisanih ljubimaca. Ova lista se obično popunjava pozivom metode {@link #ucitajSveRezervisane()}
     * @return Lista rezervisanih ljubimaca.
     */
    public ArrayList<Udomljen> getRezervisani() {        
        return rezervisani;
    }  
    /**
     * Setter za listu rezervisanih ljubimaca. Ova metoda se koristi za direktno postavljanje liste rezervisanih ljubimaca,
     * ali u praksi se lista obično popunjava pozivom metode {@link #ucitajSveRezervisane()} koja dohvaća podatke iz baze podataka.
     * @param rezervisani Lista rezervisanih ljubimaca koja se postavlja.
     */
    public void setRezervisani(ArrayList<Udomljen> rezervisani) {this.rezervisani = rezervisani;}
    /**
     * Vraća listu udomljenih ljubimaca. Ova lista se obično popunjava pozivom metode {@link #ucitajSveUdomljene()}
     * @return Lista udomljenih ljubimaca.
     */
    public ArrayList<Udomljen> getUdomljeni() {return udomljeni;}
    /**
     * Setter za listu udomljenih ljubimaca. Ova metoda se koristi za direktno postavljanje liste udomljenih ljubimaca,
     * ali u praksi se lista obično popunjava pozivom metode {@link #ucitajSveUdomljene()} koja dohvaća podatke iz baze podataka.
     * @param udomljeni Lista udomljenih ljubimaca koja se postavlja.
     */
    public void setUdomljeni(ArrayList<Udomljen> udomljeni) {this.udomljeni = udomljeni;}  
    /**
     * Vraća listu slobodnih pasa. Ova lista se obično popunjava pozivom metode {@link #ucitajSvePse()}
     * @return Lista slobodnih pasa.
     */  
    public ArrayList<Ljubimac> getPsiSlobodni() {return psiSlobodni;}
    /**
     * Setter za listu slobodnih pasa. Ova metoda se koristi za direktno postavljanje liste slobodnih pasa,
     * ali u praksi se lista obično popunjava pozivom metode {@link #ucitajSvePse()} koja dohvaća podatke iz baze podataka.
     * @param psiSlobodni Lista slobodnih pasa koja se postavlja.
     */
    public void setPsiSlobodni(ArrayList<Ljubimac> psiSlobodni) {this.psiSlobodni = psiSlobodni;}
    /**
     * Vraća listu slobodnih mačaka. Ova lista se obično popunjava pozivom metode {@link #ucitajSveMacke()}
     * @return Lista slobodnih mačaka.
     */
    public ArrayList<Ljubimac> getMackeSlobodne() {return mackeSlobodne;}
    /**
     * Setter za listu slobodnih mačaka. Ova metoda se koristi za direktno postavljanje liste slobodnih mačaka,
     * ali u praksi se lista obično popunjava pozivom metode {@link #ucitajSveMacke()} koja dohvaća podatke iz baze podataka.
     * @param mackeSlobodne Lista slobodnih mačaka koja se postavlja.
     */
    public void setMackeSlobodne(ArrayList<Ljubimac> mackeSlobodne) {
        this.mackeSlobodne = mackeSlobodne;
    }
    /** 
     * Vraća listu rezervisanih ljubimaca specifično za trenutno ulogovanog korisnika. Ova lista se obično popunjava pozivom metode {@link #ucitajSveRezervisaneZaKorisnika(int)}
     * @return Lista rezervisanih ljubimaca klijenta.
     */
    public ArrayList<Udomljen> getRezervisaniOdKorisnika() { 
        return rezervisaniOdKorisnika;
    }
    /**
     * Setter za listu rezervisanih ljubimaca specifično za trenutno ulogovanog korisnika. Ova metoda se koristi za direktno postavljanje liste rezervisanih ljubimaca klijenta,
     * ali u praksi se lista obično popunjava pozivom metode {@link #ucitajSveRezervisaneZaKorisnika(int)} koja dohvaća podatke iz baze podataka.
     * @param rezervisaniOdKorisnika Lista rezervisanih ljubimaca klijenta koja se postavlja.
     */
    public void setRezervisaniOdKorisnika(ArrayList<Udomljen> rezervisaniOdKorisnika) {
        this.rezervisaniOdKorisnika = rezervisaniOdKorisnika;
    }
    /**
     * Vraća listu rezultata pretrage ljubimaca. Ova lista se popunjava pozivom metode {@link #pretraga()} koja dohvaća podatke iz baze podataka na osnovu korisničkog unosa.
     * @return Lista rezultata pretrage ljubimaca.
     */
    public ArrayList<Ljubimac> getPretragaLista() {
        return pretragaLista;
    }
    /**
     * Setter za listu rezultata pretrage ljubimaca. Ova metoda se koristi za direktno postavljanje liste rezultata pretrage ljubimaca,
     * ali u praksi se lista obično popunjava pozivom metode {@link #pretraga()} koja dohvaća podatke iz baze podataka na osnovu korisničkog unosa.
     * @param pretragaLista Lista rezultata pretrage ljubimaca koja se postavlja.
     */
    public void setPretragaLista(ArrayList<Ljubimac> pretragaLista) {
        this.pretragaLista = pretragaLista;
    }
    /**
     * Vraća unos korisnika za pretragu ljubimaca. Ova varijabla se koristi kao parametar u metodi {@link #pretraga()} za dohvat podataka iz baze podataka na osnovu korisničkog unosa.
     * @return Unos korisnika za pretragu ljubimaca.
     */
    public String getPrLjubimca() {
        return prLjubimca;
    }
    /**
     * Setter za unos korisnika za pretragu ljubimaca. Ova metoda se koristi za postavljanje vrijednosti unosa korisnika koji se koristi kao parametar u metodi {@link #pretraga()} za dohvat podataka iz baze podataka na osnovu korisničkog unosa.
     * @param prLjubimca Unos korisnika za pretragu ljubimaca koji se postavlja.
     */
    public void setPrLjubimca(String prLjubimca) {
        this.prLjubimca = prLjubimca;
    }
    /**
     * Vraća trenutno ulogovanog korisnika. Ova varijabla se koristi za identifikaciju korisnika koji vrši operacije nad ljubimcima, kao što su rezervacije i udomljavanja.
     * @return Trenutno ulogovani korisnik.
     */
    public Korisnik getKoris() {
        return koris;
    }
    /**
     * Setter za trenutno ulogovanog korisnika. Ova metoda se koristi za postavljanje vrijednosti trenutno ulogovanog korisnika koji vrši operacije nad ljubimcima, kao što su rezervacije i udomljavanja.
     * @param koris Trenutno ulogovani korisnik koji se postavlja.
     */
    public void setKoris(Korisnik koris) {
        this.koris = koris;
    }
    /**
     * Vraća ID trenutno selektovanog ljubimca. Ova varijabla se koristi za identifikaciju ljubimca nad kojim se vrše operacije, kao što su rezervacije i udomljavanja.
     * @return ID trenutno selektovanog ljubimca.
     */
    public int getSelektovaniID() {
        return selektovaniID;
    }
    /**
    * Setter za ID trenutno selektovanog ljubimca. Ova metoda se koristi za postavljanje vrijednosti ID-a trenutno selektovanog ljubimca koji se koristi za identifikaciju ljubimca nad kojim se vrše operacije, kao što su rezervacije i udomljavanja.
    * @param selektovaniID ID trenutno selektovanog ljubimca koji se postavlja.
    */
    public void setSelektovaniID(int selektovaniID) {
        this.selektovaniID = selektovaniID;
    }
    /**
     * Vraća datoteku (npr. sliku ljubimca) koja je trenutno postavljena u bean-u. Ova varijabla se koristi za privremeno čuvanje datoteke koja se može koristiti prilikom dodavanja ili ažuriranja ljubimca.
     * @return Datoteka (npr. slika ljubimca) koja je trenutno postavljena u bean-u.
     */
    public Part getDatoteka() {
        return datoteka;
    }
    /**
     * Setter za datoteku (npr. sliku ljubimca) koja je trenutno postavljena u bean-u. Ova metoda se koristi za postavljanje vrijednosti datoteke koja se može koristiti prilikom dodavanja ili ažuriranja ljubimca.
     * @param datoteka Datoteka (npr. slika ljubimca) koja se postavlja u bean.
     */
    public void setDatoteka(Part datoteka) {
        this.datoteka = datoteka;
    } 
    /**
    * Vraća ID trenutno selektovanog korisnika. Ova varijabla se koristi za identifikaciju korisnika nad kojim se vrše operacije, kao što su rezervacije i udomljavanja.
    * @return ID trenutno selektovanog korisnika.
    */
    public int getSelektovaniIDk() {
        return selektovaniIDk;
    }
    /**
     * Setter za ID trenutno selektovanog korisnika. Ova metoda se koristi za postavljanje vrijednosti ID-a trenutno selektovanog korisnika koji se koristi za identifikaciju korisnika nad kojim se vrše operacije, kao što su rezervacije i udomljavanja.
     * @param selektovaniIDk ID trenutno selektovanog korisnika koji se postavlja.
     */
    public void setSelektovaniIDk(int selektovaniIDk) {
        this.selektovaniIDk = selektovaniIDk;
    }
    /**
    * Setter za listu historije aktivnosti ljubimca. Ova metoda se koristi za postavljanje vrijednosti liste historije aktivnosti ljubimca, koja se obično popunjava pozivom metode {@link #ucitajLjubimca()} koja dohvaća podatke iz baze podataka na osnovu ID-a ljubimca.
    * @param historijaAktivnosti Lista historije aktivnosti ljubimca koja se postavlja.
    */
    public void setHistorijaAktivnosti(ArrayList<Udomljen> historijaAktivnosti) {
        this.historijaAktivnosti = historijaAktivnosti;
    }    
    /**
     * Vraća listu historije aktivnosti ljubimca. Ova lista se obično popunjava pozivom metode {@link #ucitajLjubimca()} koja dohvaća podatke iz baze podataka na osnovu ID-a ljubimca.
     * @return Lista historije aktivnosti ljubimca.
     */
    public ArrayList<Udomljen> getHistorijaAktivnosti() {
        return historijaAktivnosti;
    }
    /**
     * Vraća indikator da li je trenutno aktivno pretraživanje ljubimaca. Ova varijabla se koristi za kontrolu prikaza rezultata pretrage na korisničkom interfejsu, gdje se različiti elementi mogu prikazivati ili skrivati ovisno o tome da li je pretraga aktivna.
     * @return Indikator da li je trenutno aktivno pretraživanje ljubimaca.
     */
    public boolean isPretrazivanje() {
        return pretrazivanje;
    }
    /**
     * Setter za indikator da li je trenutno aktivno pretraživanje ljubimaca. Ova metoda se koristi za postavljanje vrijednosti indikatora koji kontrolira prikaz rezultata pretrage na korisničkom interfejsu, gdje se različiti elementi mogu prikazivati ili skrivati ovisno o tome da li je pretraga aktivna.
     * @param pretrazivanje Indikator da li je trenutno aktivno pretraživanje ljubimaca koji se postavlja.
     */
    public void setPretrazivanje(boolean pretrazivanje) {
        this.pretrazivanje = pretrazivanje;
    }   
}