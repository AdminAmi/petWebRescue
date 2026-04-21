
package udomljavanje;

/**
 * Predstavlja entitet udomljavanja koji povezuje klijenta i ljubimca.
 * Ova klasa služi kao model za realizaciju M:N relacije i čuva informacije 
 * o datumu transakcije, uključenim objektima i statusu udomljavanja.
 * 
 * @author Amel Džanić
 * @version 1.1
 */
import java.util.Date;
import klijent.Klijent;
import ljubimac.Ljubimac;

public class Udomljen {
    /** Identifikator klijenta koji učestvuje u procesu. */
    private int idKlijenti;
    /** Identifikator ljubimca koji se udomljava ili rezerviše. */
    private int idLjubimac;
    /** Datum kada je proces udomljavanja iniciran ili izvršen. */
    private Date datumUdomljavanja;
    /** Referenca na objekat {@link Ljubimac}. */
    private Ljubimac ljub;
    /** Referenca na objekat {@link Klijent}. */
    private Klijent klijent;
    /** 
     * Status udomljavanja (npr. SLOBODAN, REZERVISAN, UDOMLJEN, VRACEN).
     * Inicijalno se postavlja na SLOBODAN koristeći {@link StanjeLjubimca}.
     */    
    private String status;

    
    /**
     * Podrazumijevani konstruktor za kreiranje praznog objekta.
     */
    public Udomljen() {
    }

    /**
     * Konstruktor za kreiranje udomljavanja na osnovu ID-ova i datuma.
     * Status se automatski postavlja na "SLOBODAN".
     *
     * @param idKlijenti        ID klijenta.
     * @param idLjubimac        ID ljubimca.
     * @param datumUdomljavanja Datum udomljavanja.
     */
    public Udomljen(int idKlijenti, int idLjubimac, Date datumUdomljavanja) {
        this.idKlijenti = idKlijenti;
        this.idLjubimac = idLjubimac;
        this.datumUdomljavanja = datumUdomljavanja;
        this.status=ljubimac.StanjeLjubimca.SLOBODAN.toString();
    }
    /**
     * Potpuni konstruktor koji inicijalizuje ID-ove, datum i reference na objekte.
     *
     * @param idKlijenti        ID klijenta.
     * @param idLjubimac        ID ljubimca.
     * @param datumUdomljavanja Datum procesa.
     * @param ljubi             Instanca objekta {@link Ljubimac}.
     * @param korisnik          Instanca objekta {@link Klijent}.
     */
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
    /** @return Vraća ID klijenta. */
    public int getIdKlijenti() {return idKlijenti;}
    /** @param idKlijenti Postavlja ID klijenta. */
    public void setIdKlijenti(int idKlijenti) {this.idKlijenti = idKlijenti;}
    /** @return Vraća ID ljubimca. */
    public int getIdLjubimac() {return idLjubimac;}
    /** @param k Postavlja referencu na klijenta. */
    public void setKlijent(Klijent k){klijent=k;}
    /** @param idLjubimac Postavlja ID ljubimca. */
    public void setIdLjubimac(int idLjubimac) {this.idLjubimac = idLjubimac;}
    /** @return Vraća datum udomljavanja. */
    public Date getDatumUdomljavanja() {return datumUdomljavanja;}
    /** @param datumUdomljavanja Postavlja datum udomljavanja. */
    public void setDatumUdomljavanja(Date datumUdomljavanja) {this.datumUdomljavanja = datumUdomljavanja;}
    /** @return Vraća referencu na klijenta. */
    public klijent.Klijent getKlijent() {return klijent;}
    /** @return Vraća referencu na ljubimca. */
    public Ljubimac getLjub() {return ljub;}
    /** @param lj Postavlja referencu na ljubimca. */
    public void setLjub(Ljubimac lj){ljub=lj;}
    /** @return Trenutni status procesa (npr. SLOBODAN, UDOMLJEN). */
    public String getStatus() {return status;}
    /** @param status Postavlja novi status procesa. */
    public void setStatus(String status) {this.status = status;}    
    /**
     * Vraća tekstualni prikaz objekta sa ključnim ID parametrima.
     * 
     * @return String sa podacima o udomljavanju.
     */
    @Override
    public String toString() {
        return "Udomljen{" +
                "idKlijenti=" + idKlijenti +
                ", idLjubimac=" + idLjubimac +
                ", datumUdomljavanja=" + datumUdomljavanja +
                '}';
    }
}
