/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ljubimac;

public enum StanjeLjubimca {
    SLOBODAN(0),
    REZERVISAN(1),
    UDOMLJEN(2);

    private final int sifra;

    // Konstruktor enuma
    StanjeLjubimca(int sifra) {
        this.sifra = sifra;
    }

    // Metoda za dobijanje broja
    public int getSifra() {
        return sifra;
    }
}

