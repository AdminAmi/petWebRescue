
package udomljavanje;

/**
 *
 * @author TFB5
 */
import java.util.Date;
import klijent.Klijent;
import korisnik.Korisnik;
import ljubimac.Ljubimac;

public class Udomljen {
    private int idKlijenti;
    private int idLjubimac;
    private Date datumUdomljavanja;
    private Ljubimac ljubimac;
    private Klijent klijent;    
    private int rezervacija;

    

    // Konstruktori
    public Udomljen() {
    }

    /**
     *
     * @param idKlijenti
     * @param idLjubimac
     * @param datumUdomljavanja
     */
    public Udomljen(int idKlijenti, int idLjubimac, Date datumUdomljavanja) {
        this.idKlijenti = idKlijenti;
        this.idLjubimac = idLjubimac;
        this.datumUdomljavanja = datumUdomljavanja;
        this.rezervacija=0;
    }
    public Udomljen(int idKlijenti, int idLjubimac, Date datumUdomljavanja,
            ljubimac.Ljubimac ljubimac, klijent.Klijent korisnik) {
        this.idKlijenti = idKlijenti;
        this.idLjubimac = idLjubimac;
        this.datumUdomljavanja = datumUdomljavanja;
        this.ljubimac=ljubimac;
        this.klijent=korisnik;
    }

    // Getteri i setteri
    public int getIdKlijenti() {
        return idKlijenti;
    }

    public void setIdKlijenti(int idKlijenti) {
        this.idKlijenti = idKlijenti;
    }

    public int getIdLjubimac() {
        return idLjubimac;
    }
    public void setKlijent(Klijent k){
        klijent=k;
    }

    public void setIdLjubimac(int idLjubimac) {
        this.idLjubimac = idLjubimac;
    }

    public Date getDatumUdomljavanja() {
        return datumUdomljavanja;
    }

    public void setDatumUdomljavanja(Date datumUdomljavanja) {
        this.datumUdomljavanja = datumUdomljavanja;
    }

    public klijent.Klijent getKlijent() {
        return klijent;
    }

    public Ljubimac getLjubimac() {
        return ljubimac;
    }
    public void setLjubimac(Ljubimac lj){
        ljubimac=lj;
    }
    
    public int getRezervacija() {
        return rezervacija;
    }

    public void setRezervacija(int rezervacija) {
        this.rezervacija = rezervacija;
    }
    

    @Override
    public String toString() {
        return "Udomljen{" +
                "idKlijenti=" + idKlijenti +
                ", idLjubimac=" + idLjubimac +
                ", datumUdomljavanja=" + datumUdomljavanja +
                '}';
    }
}
