
package udomljavanje;
import java.util.Date;
import korisnik.Korisnik;
import ljubimac.Ljubimac;

/**
 * Predstavlja entitet udomljavanja koji povezuje klijenta i ljubimca.
 * Ova klasa služi kao model za realizaciju M:N relacije i čuva informacije 
 * o datumu transakcije, uključenim objektima i statusu udomljavanja.
 * 
 * @author Amel Džanić
 * @version 1.1
 */


public class Udomljen {
    /** Identifikator klijenta koji učestvuje u procesu. */
    private int idKlijenti;
    /** Identifikator ljubimca koji se udomljava ili rezerviše. */
    private int idLjubimac;
    /** Datum kada je proces udomljavanja iniciran ili izvršen. */
    private String datumUdomljavanja;
    /** Referenca na objekat {@link Ljubimac}. */
    private Ljubimac ljub;
    /** Referenca na objekat {@link Korisnik}. */
    private Korisnik klijent;
    /** 
     * Status udomljavanja (npr. SLOBODAN, REZERVISAN, UDOMLJEN, VRACEN).
     * Inicijalno se postavlja na SLOBODAN koristeći {@link ljubimac.StanjeLjubimca}.
     */    
    private String status;

    
    /**
     * Podrazumijevani konstruktor za kreiranje praznog objekta.
     */
    public Udomljen() {}

    /**
     * Konstruktor za kreiranje udomljavanja na osnovu ID-ova i datuma.
     * Status se automatski postavlja na "SLOBODAN".
     *
     * @param idKlijenti        ID klijenta/korisnika.
     * @param idLjubimac        ID ljubimca.
     * @param datumUdomljavanja Datum udomljavanja.
     */
    public Udomljen(int idKlijenti, int idLjubimac, String datumUdomljavanja) {
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
     * @param korisnik          Instanca objekta {@link Korisnik}.
     */
    public Udomljen(int idKlijenti, int idLjubimac, String datumUdomljavanja,
            ljubimac.Ljubimac ljubi, Korisnik korisnik) {
        this.idKlijenti = idKlijenti;
        this.idLjubimac = idLjubimac;
        this.datumUdomljavanja = datumUdomljavanja;
        this.ljub=ljubi;
        this.klijent=korisnik;
        this.status=ljubimac.StanjeLjubimca.SLOBODAN.toString();
    }

    
    /** 
     * Vraća ID klijenta koji je povezan s ovim udomljavanjem. 
     * @return ID klijenta koji je povezan s ovim udomljavanjem.
    */
    public int getIdKlijenti() {return idKlijenti;}
    /** 
     * Postavlja ID klijenta koji je povezan s ovim udomljavanjem.
     * @param idKlijenti ID klijenta za postavljanje.
     */
    public void setIdKlijenti(int idKlijenti) {this.idKlijenti = idKlijenti;}
    /** 
     * Vraća ID ljubimca koji je povezan s ovim udomljavanjem.
     * 
     * @return Vraća ID ljubimca. 
     */
    public int getIdLjubimac() {return idLjubimac;}
    /** 
     * Postavlja referencu na klijenta koji je povezan s ovim udomljavanjem.
     * @param k Referenca na klijenta za postavljanje.
     */
    public void setKlijent(Korisnik k){klijent=k;}
    /** 
     * Postavlja ID ljubimca koji je povezan s ovim udomljavanjem.
     * @param idLjubimac ID ljubimca za postavljanje.
     */
    public void setIdLjubimac(int idLjubimac) {this.idLjubimac = idLjubimac;}
    /** 
     * Vraća datum udomljavanja.
     * @return Datum udomljavanja.
     */
    public String getDatumUdomljavanja() {return datumUdomljavanja;}
    /** 
     * Postavlja datum udomljavanja.
     * @param datumUdomljavanja Datum udomljavanja za postavljanje.
     */
    public void setDatumUdomljavanja(String datumUdomljavanja) {this.datumUdomljavanja = datumUdomljavanja;}
    /** 
     * Vraća referencu na klijenta koji je povezan s ovim udomljavanjem.
     * @return Referenca na klijenta.
     */
    public Korisnik getKlijent() {return klijent;}
    /** 
     * Vraća referencu na ljubimca koji je povezan s ovim udomljavanjem.
     * @return Referenca na ljubimca.
     */
    public Ljubimac getLjub() {return ljub;}
    /** 
     * Postavlja referencu na ljubimca koji je povezan s ovim udomljavanjem.
     * @param lj Referenca na ljubimca za postavljanje.
     */
    public void setLjub(Ljubimac lj){ljub=lj;}
    /** 
     * Vraća trenutni status procesa (npr. SLOBODAN, UDOMLJEN).
     * @return Trenutni status procesa.
     */
    public String getStatus() {return status;}
    /** 
     * Postavlja novi status procesa.
     * @param status Novi status procesa.
     */
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
