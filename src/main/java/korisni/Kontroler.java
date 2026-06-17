
package korisni;
import java.io.File;
import java.io.IOException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.ResultSet;
import javax.swing.JOptionPane;

/**
 * Klasa <code>Kontroler</code> je abstraktna klasa koja nemoze imati instancu.
 * Osmisljena je da otvara konekciju sa bazom i da moze izvrsiti osnovne
 * SQL naredbe CREATE, INSERT, UPDATE i DELETE pomocu 
 * metode {@link #InsDelUpd(java.lang.String) }
 * @author Amel Dzanic
 */
public abstract class Kontroler {
    /**
     * Varijabla <code>dbPath</code> sadrži putanju do baze podataka koja se koristi u ovoj klasi.
     */
    protected final String      dbPath = "jdbc:sqlite:" + webUtil.vratiPathDB()+"registracija";
    /**
     * Varijabla <code>kone</code> predstavlja konekciju sa bazom podataka. Ova varijabla se koristi za uspostavljanje veze sa bazom i izvršavanje SQL naredbi.
     */
    protected Connection        kone;
    /**
     * Konstruktor klase <code>Kontroler</code> pokušava učitati SQLite JDBC driver. Ako driver nije pronađen, ispisuje se poruka o grešci. Ovaj konstruktor se poziva prilikom kreiranja objekata klasa koje nasljeđuju <code>Kontroler</code>.
     */
    public Kontroler() {
        try {            
           System.out.println(dbPath + "\nU kontroleru sam");
            Class.forName("org.sqlite.JDBC");       
        } catch (ClassNotFoundException ex) {
            //Ispisati grešku da nema dobrog drivera za bazu
        }
    }
    
    /**
     * Metoda koja služi za povezivanje sa bazom podataka. Ako se ne poveže
     * izbacuje izuzetak. Kao argument za povezivanje koristi se varijabla 
     * <code>dbPath</code>
     * @return objecat Connection
     * @throws SQLException ukoliko nije uspješno povezivanje sa bazom podataka
     */
    protected Connection getKone() throws SQLException {
        Connection conn = DriverManager.getConnection(dbPath); 
        // 2. KLJUČNI DIO: Omogućavanje stranih ključeva
//        try (Statement st = conn.createStatement()) {
//            st.execute("PRAGMA foreign_keys = ON;");
//        }

        return conn;
    }
    
    /**
     * Metoda <b><code>InsDelUpd</code></b> obavlja sve operacije sa
     * bazom što se tiče - isključivo sa alfanumeričkim podacima:
     * insertovanja sloga,
     * brisanja sloga,
     * te ažuriranja sloga u bazi podataka.
     * <p><b>Bitan je samo tacan sql upit!!!</b>
     * Treba još napomenuti da ova metoda sama sebi otvara i zatvara
     * konekciju sa bazom,tako da je programer oslobođen toga.
     * <p>Evo primjer:<br>
     * Neka imamo bazu sa samo dva polja jedno int id, a drugo varchar(45)
     * koje mi sluzi za unos imena. Tada bi za insert bio sljedeci kod:<br>
     * <code><br>
     * utilDB db = new utilDB();<br>
     * db.InsDelUpd("insert into test values(2,'Neko ime')");</code>
     * @param sql upit z Bazu
     * @throws SQLException ukoliko se desila neka greska sa bazom podataka
     */
    public void InsDelUpd (String sql) throws SQLException{
        try(Connection k = DriverManager.getConnection(dbPath);
                Statement st = k.createStatement()){
                st.execute(sql);            
        } catch (SQLException e) {
            throw e;
        }
    }       
  
    /**
     * Metoda <code>getDbPath</code> vraća putanju do baze podataka koja se koristi u ovoj klasi.
     * @return String koji predstavlja putanju do baze podataka
     */
    public String getDbPath() {
        return dbPath;
    }
    

}
