package korisni;

import java.io.IOException;
import java.util.logging.*;

/**
 * Klasa {@code MyLogger} je prilagođeni logger koji koristi Java Logging API
 * za zapisivanje logova u datoteku "log.txt" unutar direktorija "DB" u resursima web aplikacije.
 * Logger podržava različite razine logiranja (info, warning, error) i 
 * koristi {@link SimpleFormatter} za čitljiv format zapisa. 
 * Inicijalizacija loggera se odvija statičkim blokom, gdje se postavlja FileHandler i formatiranje. 
 * Metode info, warning i error omogućavaju jednostavno zapisivanje poruka loga s odgovarajućim nivoom ozbiljnosti.
 * 
 * @author Amel Džanić
 * @version 1.0
 * @since 2026-05-01
 */
public class MyLogger {
   
    private static final Logger logger = Logger.getLogger(MyLogger.class.getName());
    
    static {
        try {
            // Postavljanje FileHandler-a (putanja do datoteke, append mode postavljen na true)
            FileHandler fileHandler = new FileHandler(webUtil.vratiPathDB() + "log.txt", true);            
            // Postavljanje formata zapisa (SimpleFormatter je čitljiv ljudima)
            fileHandler.setFormatter(new SimpleFormatter());            
            logger.addHandler(fileHandler);            
            // Opcionalno: Isključivanje slanja logova u konzolu ako želiš samo u file
            // logger.setUseParentHandlers(false);
        } catch (IOException e) {
            System.err.println("Neuspjelo inicijaliziranje logera: " + e.getMessage());
        }
    }

    /**
     * Privatni konstruktor koji sprječava instanciranje ove pomoćne klase.
     * Baca {@link AssertionError} ako se pokuša pozvati unutar klase.
     *
     * @throws AssertionError ako se ovaj konstruktor pozove
     */
    private MyLogger() {
        throw new AssertionError("Nije dozvoljeno instanciranje pomoćne klase.");
    }

    /**
     * Metoda {@code info} prima poruku kao argument i zapisuje je u log s nivoom INFO.
     * @param poruka Poruka za zapisivanje u log.
     */
    public static void info(String poruka) {
        logger.info(poruka);
    }

    /**
     * Metoda {@code warning} prima poruku kao argument i zapisuje je u log s nivoom WARNING.
     * @param poruka Poruka za zapisivanje u log.
     */
    public static void warning(String poruka) {
        logger.warning(poruka);
    }

    /**
     * Metoda {@code error} prima poruku i iznimku kao argumente i zapisuje ih u log s nivoom ERROR.
     * @param poruka Poruka za zapisivanje u log.
     * @param e Iznimka koja se zapisuje u log.
     */
    public static void error(String poruka, Exception e) {
        logger.log(Level.SEVERE, poruka, e);
    }

    /**
     * Glavna metoda za testiranje loggera. Zapisuje različite vrste poruka u log.
     * @param args Argumenti komandne linije (nisu korišteni).
     */
    public static void main(String[] args) {
        MyLogger.info("Aplikacija je uspješno pokrenuta.");
        MyLogger.warning("Ovo je upozorenje.");
        MyLogger.error("Dogodila se greška!", new RuntimeException("Test iznimka"));
    }
}
