/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package klijent;

import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author TFB5
 */
public class testKorisnik {
    public static void main(String[] args) throws SQLException {
        KlijentCRUD klijentCRUD = new KlijentCRUD();
            
            // Dodavanje klijenta
            Klijent noviKlijent = new Klijent("Irfan Džanić", "Savska 5", "0987654321");
            klijentCRUD.dodajKlijenta(noviKlijent);
            
            // Čitanje svih klijenata
            List<Klijent> klijenti = klijentCRUD.dobaviSveKlijente();
            for (Klijent k : klijenti) {
                System.out.println(k);
            }
    }
    
}
