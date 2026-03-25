
package korisnik;

import java.sql.SQLException;



/**
 *
 * @author TFB5
 */
public class test {
    public static void main(String[] args) throws SQLException {
        Korisnik k = new Korisnik(0, "ami", "ami", "Amel", "Dzanic","ADMINISTRATOR");
        CRUDKorisnik cr = new CRUDKorisnik();
        cr.UnesiKorisnika(k);
        //System.out.println(cr.VratiKorisnikaPoID(4).toString());
        System.out.println(Boolean.toString(cr.login("am", "ami")));
        
        
    }
    
}
