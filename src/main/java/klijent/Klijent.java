
package klijent;

import java.util.ArrayList;
import ljubimac.Ljubimac;


public class Klijent {
    private int id;
    private String imePrezime;
    private String adresa;
    private String brojTelefona;
    //Zbog mxn relacije
    private ArrayList <ljubimac.Ljubimac> ljubimci = new ArrayList<>();

    // Konstruktori
    public Klijent() {
    }

    public Klijent(String imePrezime, String adresa, String brojTelefona) {
        this.imePrezime = imePrezime;
        this.adresa = adresa;
        this.brojTelefona = brojTelefona;
    }

    public Klijent(int id, String imePrezime, String adresa, String brojTelefona) {
        this.id = id;
        this.imePrezime = imePrezime;
        this.adresa = adresa;
        this.brojTelefona = brojTelefona;
    }

    // Getteri i setteri
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImePrezime() {
        return imePrezime;
    }

    public void setImePrezime(String imePrezime) {
        this.imePrezime = imePrezime;
    }

    public String getAdresa() {
        return adresa;
    }

    public void setAdresa(String adresa) {
        this.adresa = adresa;
    }

    public String getBrojTelefona() {
        return brojTelefona;
    }

    public void setBrojTelefona(String brojTelefona) {
        this.brojTelefona = brojTelefona;
    }

    @Override
    public String toString() {
        return "Klijent{" +
                "id=" + id +
                ", imePrezime='" + imePrezime + '\'' +
                ", adresa='" + adresa + '\'' +
                ", brojTelefona='" + brojTelefona + '\'' +
                '}';
    }

    public void setLjubimci(ArrayList<Ljubimac> ljubimci) {
        this.ljubimci = ljubimci;
    }

    public ArrayList<Ljubimac> getLjubimci() {
        return ljubimci;
    }
    
    
}