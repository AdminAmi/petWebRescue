/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package korisni;
import java.io.IOException;
import java.util.logging.*;

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
    public static void info(String poruka) {
        logger.info(poruka);
    }
    public static void warning(String poruka) {
        logger.warning(poruka);
    }
    public static void error(String poruka, Exception e) {
        logger.log(Level.SEVERE, poruka, e);
    }

    // Testiranje logera
    public static void main(String[] args) {
        MyLogger.info("Aplikacija je uspješno pokrenuta.");
        MyLogger.warning("Ovo je upozorenje.");
        MyLogger.error("Dogodila se greška!", new RuntimeException("Test iznimka"));
    }
}

