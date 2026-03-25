
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
    private Ljubimac ljub;
    private Klijent klijent;    
    private String status;

    

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
        this.status=ljubimac.StanjeLjubimca.SLOBODAN.toString();
    }
    public Udomljen(int idKlijenti, int idLjubimac, Date datumUdomljavanja,
            ljubimac.Ljubimac ljubi, klijent.Klijent korisnik) {
        this.idKlijenti = idKlijenti;
        this.idLjubimac = idLjubimac;
        this.datumUdomljavanja = datumUdomljavanja;
        this.ljub=ljubi;
        this.klijent=korisnik;
        this.status=ljubimac.StanjeLjubimca.SLOBODAN.toString();
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

    public Ljubimac getLjub() {
        return ljub;
    }
    public void setLjub(Ljubimac lj){
        ljub=lj;
    }
    
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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
