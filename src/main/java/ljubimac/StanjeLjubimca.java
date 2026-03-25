/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ljubimac;

public enum StanjeLjubimca {
    SLOBODAN("SLOBODAN"),
    REZERVISAN("REZERVISAN"),
    UDOMLJEN("UDOMLJEN");
    
    private final String opis;

    private StanjeLjubimca(String opis) {
        this.opis = opis;
    }

    @Override
    public String toString() {
        return opis;
    }

        
   
}

