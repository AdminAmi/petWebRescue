
package ljubimac;

import java.util.ArrayList;
import klijent.Klijent;

/**
 *
 * @author Amel Džanić
 */
public class Ljubimac {
    private int id;
    private String ime;
    private String vrsta; // "pas" ili "mačka"
    private String starost;
    private String status; // "DA" ili "NE"
    //Zbog mxn relacije
    private ArrayList <Klijent> klijenti = new ArrayList<>();    

    // Konstruktori
    public Ljubimac() {
    }

    /**
     *
     * @param ime
     * @param vrsta
     * @param starost
     * @param korisnici
     */
    public Ljubimac(String ime, String vrsta, String starost) {
        this.ime = ime;
        this.vrsta = vrsta;
        this.starost = starost;
        this.status = "NE"; // Default vrijednost        
    }

    public Ljubimac(int id, String ime, String vrsta, String starost,
            String status) {
        this.id = id;
        this.ime = ime;
        this.vrsta = vrsta;
        this.starost = starost;
        this.status = status;        
    }

    // Getteri i setteri
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIme() {
        return ime;
    }

    public void setIme(String ime) {
        this.ime = ime;
    }

    public String getVrsta() {
        return vrsta;
    }

    public void setVrsta(String vrsta) {
        this.vrsta = vrsta;
    }

    public String getStarost() {
        return starost;
    }

    public void setStarost(String starost) {
        this.starost = starost;
    }

    @Override
    public String toString() {
        return "Ljubimac{" +
                "id=" + id +
                ", ime='" + ime + '\'' +
                ", vrsta='" + vrsta + '\'' +
                ", starost='" + starost + '\'' +
                '}';
    }

    
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ArrayList<Klijent> getKorisnici() {
        return klijenti;
    }

    public void setKorisnici(ArrayList<Klijent> klijenti) {
        this.klijenti = klijenti;
    }
    
}
