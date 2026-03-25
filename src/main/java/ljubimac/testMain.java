/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ljubimac;

import java.sql.SQLException;
import java.util.List;

public class testMain {
    public static void main(String[] args) throws SQLException {
       
            
            LjubimacCRUD ljubimacCRUD = new LjubimacCRUD();
            
            // Dodavanje ljubimca
            Ljubimac noviLjubimac = new Ljubimac("Rex", "pas", "5 godina");
            ljubimacCRUD.dodajLjubimca(noviLjubimac);
            
            // Dodavanje mačke
            Ljubimac macka = new Ljubimac("Micka", "mačka", "3 godine");
            ljubimacCRUD.dodajLjubimca(macka);
            
            // Čitanje svih ljubimaca
            List<Ljubimac> ljubimci = ljubimacCRUD.dobaviSveLjubimce(0,"",0);
            for (Ljubimac l : ljubimci) {
                System.out.println(l);
            }
            
        
    }
}
