
package ljubimac;

import java.util.ArrayList;

import korisnik.Korisnik;

/**
 * Predstavlja entitet ljubimca u sistemu.
 * Sadrži informacije o identitetu, vrsti, starosti i statusu udomljavanja,
 * kao i listu klijenata povezanih sa ljubimcem kroz M:N relaciju.
 * 
 * @author Amel Džanić
 * @version 1.1
 * @since 2026-05-01
 * @see Korisnik
 * @see StanjeLjubimca
 */
public class Ljubimac {
    /** Jedinstveni identifikator ljubimca u bazi podataka. */
    private int id;
    /** Ime ljubimca. */
    private String ime;
    /** Vrsta životinje (npr. "pas" ili "mačka"). */
    private String vrsta; 
    /** Opisna starost ljubimca (npr. "2 godine" ili "štene"). */
    private String starost;
     /** Status udomljavanja ljubimca ("SLOBODAN" ili "REZERVISAN"). */
    private String status; 
    /** Datum prijema u sklonište ljubimca*/
    private String datumPrijema;
    /** Lista klijenata koji su povezani sa ovim ljubimcem. */
    private ArrayList <Korisnik> klijenti = new ArrayList<>();
    /** URL slike ljubimca. */
    private String slikaURL;

    /**
     * Podrazumijevani konstruktor koji se koristi za kreiranje praznog objekta ljubimca.
     */
    public Ljubimac() {
    }

    /**
     * Konstruktor za kreiranje novog ljubimca bez ID-a (obično prije spašavanja u bazu).
     * Status se automatski postavlja na "NE".
     *
     * @param ime     Ime ljubimca.
     * @param vrsta   Vrsta životinje.
     * @param starost Starost ljubimca.
     * @param datum   Datum prijema ljubimca u sklonište.
     */
    public Ljubimac(String ime, String vrsta, String starost, String datum) {
        this.ime = ime;
        this.vrsta = vrsta;
        this.starost = starost;
        this.datumPrijema=datum;
        this.status = StanjeLjubimca.SLOBODAN.toString(); // Default vrijednost        
    }
    /**
     * Konstruktor za kreiranje potpunog objekta ljubimca sa svim podacima.
     *
     * @param id      Jedinstveni identifikator.
     * @param ime     Ime ljubimca.
     * @param vrsta   Vrsta životinje.
     * @param starost Starost ljubimca.
     * @param status  Status udomljavanja.
     * @param datum   Datum prijema ljubimca u sklonište.
     */
    public Ljubimac(int id, String ime, String vrsta, String starost,
            String status, String datum) {
        this.id = id;
        this.ime = ime;
        this.vrsta = vrsta;
        this.starost = starost;
        this.datumPrijema=datum;
        this.status = status;        
    }

    
    /** 
     * Postavlja ID ljubimca.
     * @param id Postavlja ID ljubimca.
     */
    public void setId(int id) { this.id = id;  }  
    /** 
     * Vraća ID ljubimca.
     * @return Vraća ID ljubimca. 
     */
    public int getId() { return id; }
    /** 
     * Postavlja ime ljubimca.
     * @param ime Postavlja ime ljubimca.
     */
    public void setIme(String ime) {  this.ime = ime;   } 
    /** 
     * Vraća ime ljubimca.
     * @return Vraća ime ljubimca.
     */
    public String getIme() { return ime; } 
    /** 
     * Postavlja vrstu ljubimca.
     * @param vrsta Postavlja vrstu ljubimca.
     */
    public void setVrsta(String vrsta) { this.vrsta = vrsta;}
    /** 
     * Vraća vrstu ljubimca.
     * @return Vraća vrstu ljubimca.
     */
    public String getVrsta() { return vrsta;}
    /** 
     * Vraća starost ljubimca.
     * @return Vraća starost ljubimca.
     */
    public String getStarost() {return starost;}
    /** 
     * Postavlja starost ljubimca.
     * @param starost Postavlja starost ljubimca.
     */
    public void setStarost(String starost) {this.starost = starost;}
    /** 
     * Vraća status udomljavanja.
     * @return Vraća status udomljavanja.
     */
    public String getStatus() {return status;}
    /** 
     * Postavlja status udomljavanja.
     * @param status Postavlja status udomljavanja.
     */
    public void setStatus(String status) {this.status = status;}
    /**
     * Vraća listu korisnika povezanih sa ljubimcem.
     * 
     * @return Lista objekata tipa {@link Korisnik}.
     */
    public ArrayList<Korisnik> getKorisnici() {return klijenti;}
    /** 
     * Postavlja listu korisnika povezanih sa ljubimcem.
     * @param klijenti Postavlja listu korisnika. 
     */
    public void setKorisnici(ArrayList<Korisnik> klijenti) 
    {this.klijenti = klijenti;}

    /** 
     * Postavlja datum prijema ljubimca u sklonište.
     * @param datumPrijema Datum prijema ljubimca.
     */
    public void setDatumPrijema(String datumPrijema) {
        this.datumPrijema = datumPrijema;
    }
    /** 
     * Vraća datum prijema ljubimca u sklonište.
     * @return Datum prijema ljubimca.
     */
    public String getDatumPrijema() {
        return datumPrijema;
    }    
    /**
     * Vraća stringovnu reprezentaciju objekta sa osnovnim podacima.
     * 
     * @return String sa podacima o ljubimcu.
     */
    @Override
    public String toString() {
        return "Ljubimac{" +
                "id=" + id +
                ", ime='" + ime + '\'' +
                ", vrsta='" + vrsta + '\'' +
                ", starost='" + starost + '\'' +
                '}';
    }

    /**
     * Postavlja URL slikе ljubimca.
     * @param slikaURL URL slike ljubimca.
     */
    public void setSlikaURL(String slikaURL) {
        this.slikaURL = slikaURL;
    }
    /**
     * Vraća URL slike ljubimca.
     * @return URL slike ljubimca.
     */
    public String getSlikaURL() {
        return slikaURL;
    }
    
    
}
