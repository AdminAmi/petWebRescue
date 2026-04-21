
package ljubimac;

import java.util.ArrayList;
import klijent.Klijent;

/**
 * Predstavlja entitet ljubimca u sistemu.
 * Sadrži informacije o identitetu, vrsti, starosti i statusu udomljavanja,
 * kao i listu klijenata povezanih sa ljubimcem kroz M:N relaciju.
 * 
 * @author Amel Džanić
 * @version 1.1
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
    private String status; // 
    /** Lista klijenata koji su povezani sa ovim ljubimcem. */
    private ArrayList <Klijent> klijenti = new ArrayList<>();    

    
    public Ljubimac() {
    }

    /**
     * Konstruktor za kreiranje novog ljubimca bez ID-a (obično prije spašavanja u bazu).
     * Status se automatski postavlja na "NE".
     *
     * @param ime     Ime ljubimca.
     * @param vrsta   Vrsta životinje.
     * @param starost Starost ljubimca.
     */
    public Ljubimac(String ime, String vrsta, String starost) {
        this.ime = ime;
        this.vrsta = vrsta;
        this.starost = starost;
        this.status = "NE"; // Default vrijednost        
    }
    /**
     * Konstruktor za kreiranje potpunog objekta ljubimca sa svim podacima.
     *
     * @param id      Jedinstveni identifikator.
     * @param ime     Ime ljubimca.
     * @param vrsta   Vrsta životinje.
     * @param starost Starost ljubimca.
     * @param status  Status udomljavanja.
     */
    public Ljubimac(int id, String ime, String vrsta, String starost,
            String status) {
        this.id = id;
        this.ime = ime;
        this.vrsta = vrsta;
        this.starost = starost;
        this.status = status;        
    }

    // Getteri i setteri
    /** @param id Postavlja ID ljubimca. */
    public void setId(int id) { this.id = id;  }  
    /** @return Vraća ID ljubimca. */
    public int getId() { return id; }
    /** @param ime Postavlja ime ljubimca. */
    public void setIme(String ime) {  this.ime = ime;   } 
    /** @return Vraća ime ljubimca. */
    public String getIme() { return ime; } 
    /** @param vrsta Postavlja vrstu ljubimca. */
    public void setVrsta(String vrsta) { this.vrsta = vrsta;}
    /** @return Vraća vrstu ljubimca. */    
    public String getVrsta() { return vrsta;}
    /** @return Vraća starost ljubimca. */
    public String getStarost() {return starost;}
    /** @param starost Postavlja starost ljubimca. */
    public void setStarost(String starost) {this.starost = starost;}
    /** @return Vraća status udomljavanja. */
    public String getStatus() {return status;}
    /** @param status Postavlja status udomljavanja. */
    public void setStatus(String status) {this.status = status;}
    /**
     * Vraća listu klijenata povezanih sa ljubimcem.
     * 
     * @return Lista objekata tipa {@link Klijent}.
     */
    public ArrayList<Klijent> getKorisnici() {return klijenti;}
    /** @param klijenti Postavlja listu klijenata. */
    public void setKorisnici(ArrayList<Klijent> klijenti) 
    {this.klijenti = klijenti;}
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
}
