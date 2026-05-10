package ljubimac;

/**
 * Predstavlja moguća stanja u kojima se ljubimac može nalaziti unutar sistema.
 * Koristi se za praćenje statusa udomljavanja i dostupnosti životinja.
 * 
 * @author Amel Džanić
 * @version 1.0
 */
public enum StanjeLjubimca {
    
    /** Ljubimac je dostupan za udomljavanje. */
    SLOBODAN("SLOBODAN"),
    
    /** Ljubimac je trenutno rezervisan od strane potencijalnog udomitelja. */
    REZERVISAN("REZERVISAN"),
    
    /** Ljubimac je uspješno udomljen i više nije u skloništu. */
    UDOMLJEN("UDOMLJEN"),
    
    /** Ljubimac je vraćen u sklonište nakon prethodnog udomljavanja. */
    VRACEN("VRACEN"),
    
    /** Ljubimac je zaprimljen u sklonište  */
    ZAPRIMLJEN("ZAPRIMLJEN"),
    
    /** Ukoliko je istekla rezervacija */
    ISTEKLO("ISTEKLO");  // Dodaj ovo
    
    /** Kratki opis stanja ljubimca. */
    private final String opis;

    /**
     * Konstruktor za postavljanje opisa stanja.
     * 
     * @param opis Tekstualni opis stanja.
     */
    private StanjeLjubimca(String opis) {
        this.opis = opis;
    }

    /**
     * Vraća tekstualni prikaz stanja ljubimca.
     * 
     * @return Opis stanja kao String.
     */
    @Override
    public String toString() {
        return opis;
    }
}
