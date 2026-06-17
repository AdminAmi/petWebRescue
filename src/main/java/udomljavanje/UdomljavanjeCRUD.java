package udomljavanje;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import ljubimac.StanjeLjubimca;
import korisnik.Korisnik;

/**
 * Kontroler klasa koja upravlja veznom tabelom 'udomljavanje'.
 * Optimizovana verzija sa JOIN-ovima za eliminaciju N+1 problema.
 * Ova klasa omogućava kreiranje, čitanje, ažuriranje i brisanje relacija između klijenata i ljubimaca,
 * te pruža metode za dohvat kompletnog konteksta udomljavanja, uključujući informacije o klijentima i ljubimcima,
 * kao i historiju udomljavanja za pojedinačne klijente i ljubimce. Sve metode su dizajnirane da budu efikasne i da minimiziraju broj upita prema bazi podataka.
 * Ova klasa je ključna za implementaciju funkcionalnosti udomljavanja u aplikaciji, omogućavajući praćenje i upravljanje procesom udomljavanja ljubimaca na transparentan i efikasan način.
 * Ova klasa koristi SQL JOIN-ove kako bi dohvatila sve relevantne informacije u jednom upitu, čime se značajno smanjuje broj potrebnih upita i poboljšava performanse aplikacije, posebno kada se radi o dohvaćanju historije udomljavanja ili aktivnih rezervacija.
 * Ova klasa je dizajnirana da bude fleksibilna i da podržava različite scenarije udomljavanja, uključujući rezervacije, aktivna udomljavanja, te historiju udomljavanja za pojedinačne klijente i ljubimce. Korištenje JOIN-ova omogućava da se sve relevantne informacije o klijentima i ljubimcima dobiju u jednom upitu, što je posebno važno za funkcionalnosti koje zahtijevaju prikaz kompletnog konteksta udomljavanja.
 * 
 * @author Amel Džanić
 * @version 2.0
 * @since 2026-05-01
 * @see Udomljen
 * 
 */

public class UdomljavanjeCRUD extends korisni.Kontroler {
    /**
     * SimpleDateFormat se koristi za parsiranje i formatiranje datuma u formatu "yyyy-MM-dd".
     */
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    /**
     * Konstruktor koji inicijalizuje veznu tabelu 'udomljavanje' u bazi podataka.
     * 
     * @throws SQLException ako dođe do greške pri kreiranju tabele u bazi podataka.
     */
    public UdomljavanjeCRUD() throws SQLException {
        createTable();
    }
    
    /**
     * Kreira tabelu 'udomljavanje' u bazi podataka ako već ne postoji.
     * Tabela sadrži kolone za ID klijenta, ID ljubimca, datum udomljavanja i status, te postavlja primarni ključ na kombinaciju ovih kolona kako bi se omogućila historija udomljavanja.
     * @throws SQLException ako dođe do greške pri izvršavanju SQL upita za kreiranje tabele.
     */
    public final void createTable() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS udomljavanje (" +
                     "idKlijenti INTEGER NOT NULL, " +
                     "idLjubimac INTEGER NOT NULL, " +
                     "datumUdomljavanja TEXT NOT NULL, " +
                     "status TEXT, " +
                     "PRIMARY KEY (idKlijenti, idLjubimac, datumUdomljavanja), " + // Promijenjen PK zbog historije
                     "FOREIGN KEY (idKlijenti) REFERENCES korisnik(id) ON DELETE CASCADE, " +
                     "FOREIGN KEY (idLjubimac) REFERENCES ljubimac(id) ON DELETE CASCADE" +
                     ")";
        try (Connection kon = getKone(); Statement st = kon.createStatement()) {
            st.execute(sql);
        }
    }
    
    /**
     * Mapira rezultat SQL upita sa JOIN-ovima na Udomljen objekat.
     * Ova metoda očekuje da ResultSet sadrži kolone sa aliasima koji su definirani u SQL upitima, kako bi se mogli pravilno mapirati podaci o klijentu i ljubimcu.
     * @param rs ResultSet koji sadrži rezultate SQL upita sa JOIN-ovima.
     * @return Udomljen objekat koji sadrži informacije o klijentu, ljubimcu, datumu udomljavanja i statusu.
     * @throws SQLException ako dođe do greške pri čitanju podataka iz ResultSet-a.
     * @throws ParseException ako dođe do greške pri parsiranju datuma iz ResultSet-a.
     */
    private Udomljen mapirajSaJoinom(ResultSet rs) throws SQLException, ParseException {
        // Podaci iz udomljavanje tabele
        Udomljen u = new Udomljen(
            rs.getInt("u_idKlijenti"),
            rs.getInt("u_idLjubimac"),
            rs.getString("u_datumUdomljavanja")
        );
        
        // Mapiranje statusa
        String statusStr = rs.getString("u_status");
        if (statusStr != null) {
            switch (statusStr) {
                case "SLOBODAN" -> u.setStatus(StanjeLjubimca.SLOBODAN.toString());
                case "REZERVISAN" -> u.setStatus(StanjeLjubimca.REZERVISAN.toString());
                case "UDOMLJEN" -> u.setStatus(StanjeLjubimca.UDOMLJEN.toString());
                case "VRACEN" -> u.setStatus(StanjeLjubimca.VRACEN.toString());
                case "ISTEKLO" -> u.setStatus(StanjeLjubimca.ISTEKLO.toString());
                default -> u.setStatus(StanjeLjubimca.ZAPRIMLJEN.toString());
            }
        }
        
        // Mapiranje klijenta iz JOIN-a (bez dodatnog upita)
        Korisnik klijent = new Korisnik();
        klijent.setId(rs.getInt("k_id"));
        klijent.setIme(rs.getString("k_ime"));
        klijent.setPrezime(rs.getString("k_prezime"));        
        klijent.setTelefon(rs.getString("k_telefon"));
        klijent.setAdresa(rs.getString("k_adresa"));
        u.setKlijent(klijent);
        
        // Mapiranje ljubimca iz JOIN-a (bez dodatnog upita)
        ljubimac.Ljubimac ljubimac = new ljubimac.Ljubimac();
        ljubimac.setId(rs.getInt("l_id"));
        ljubimac.setIme(rs.getString("l_ime"));
        ljubimac.setVrsta(rs.getString("l_vrsta"));        
        ljubimac.setStarost(rs.getString("l_starost"));
        
        
        String statusLjubimca = rs.getString("l_status");
        if (statusLjubimca != null) {
            switch (statusLjubimca) {
                case "SLOBODAN" -> ljubimac.setStatus(StanjeLjubimca.SLOBODAN.toString());
                case "REZERVISAN" -> ljubimac.setStatus(StanjeLjubimca.REZERVISAN.toString());
                case "UDOMLJEN" -> ljubimac.setStatus(StanjeLjubimca.UDOMLJEN.toString());
                case "VRACEN" -> ljubimac.setStatus(StanjeLjubimca.VRACEN.toString());
                case "ISTEKLO" -> ljubimac.setStatus(StanjeLjubimca.ISTEKLO.toString());
                default -> ljubimac.setStatus(StanjeLjubimca.ZAPRIMLJEN.toString());
            }
        }
        
        u.setLjub(ljubimac);
        return u;
    }
    
    /**
     * Dohvata sve udomljavanja sa statusom "UDOMLJEN" (jedan upit!).
     * Ova metoda koristi SQL JOIN-ove kako bi dohvatila sve relevantne informacije o klijentima i ljubimcima u jednom upitu, eliminirajući potrebu za dodatnim upitima i time poboljšavajući performanse aplikacije. Rezultati su sortirani po datumu udomljavanja u opadajućem redoslijedu, tako da se najnovija udomljavanja prikazuju prva.
     * @return Lista Udomljen objekata koji imaju status "UDOMLJEN".
     * @throws SQLException ako dođe do greške pri čitanju podataka iz baze podataka.
     */
    public List<Udomljen> dobaviSvaUdomljavanja() throws SQLException {
        String sql = "SELECT "+aliasi()+" FROM udomljavanje u " +
                     "JOIN korisnik k ON u.idKlijenti = k.id " +
                     "JOIN ljubimac l ON u.idLjubimac = l.id " +
                     "WHERE u.status = '" +StanjeLjubimca.UDOMLJEN.toString()+"' "+
                     "ORDER BY u.datumUdomljavanja DESC";
        
        return izvrsiJoinUpit(sql);
    }
    /**
     * Pomoćna metoda koja vraća string sa aliasima za sve kolone koje se koriste u JOIN upitima.
     * @return String sa aliasima za kolone iz tabela udomljavanje, korisnik i ljubimac, formatiran tako da se može direktno koristiti u SQL SELECT upitima sa JOIN-ovima. Ovi aliasi omogućavaju da se izbjegne konflikt imena kolona i olakšavaju mapiranje rezultata na Udomljen objekat.
     */
    private String aliasi(){
        return  "u.idKlijenti AS u_idKlijenti, " +
                 "u.idLjubimac AS u_idLjubimac, " +
                 "u.datumUdomljavanja AS u_datumUdomljavanja, " +
                 "u.status AS u_status, " +
                 // Klijent kolone
                 "k.id AS k_id, " +
                 "k.ime AS k_ime, " +
                 "k.prezime AS k_prezime, " +                
                 "k.telefon AS k_telefon, " +
                 "k.adresa AS k_adresa, " +
                 // Ljubimac kolone
                 "l.id AS l_id, " +
                 "l.ime AS l_ime, " +
                 "l.vrsta AS l_vrsta, " +                 
                 "l.starost AS l_starost, " +                
                 "l.status AS l_status " ;
    }
    
    /**
     * Dohvata sve aktivne rezervacije (jedan upit!).
     * Ova metoda koristi SQL JOIN-ove kako bi dohvatila sve relevantne informacije o klijentima i ljubimcima u jednom upitu, eliminirajući potrebu za dodatnim upitima i time poboljšavajući performanse aplikacije. Rezultati su sortirani po datumu udomljavanja u opadajućem redoslijedu, tako da se najnovije rezervacije prikazuju prve.
     * @return Lista Udomljen objekata koji imaju status "REZERVISAN".
     * @throws SQLException ako dođe do greške pri čitanju podataka iz baze podataka.
     */
    public List<Udomljen> dobaviSveRezervacije() throws SQLException {
        String sql = "SELECT " + aliasi() + "FROM udomljavanje u " +
                 "JOIN korisnik k ON u.idKlijenti = k.id " +
                 "JOIN ljubimac l ON u.idLjubimac = l.id " +
                 "WHERE u.status = 'REZERVISAN' " +
                 "ORDER BY u.datumUdomljavanja DESC";
        return izvrsiJoinUpit(sql);
    }
    
    /**
     * Dohvata sve aktivne rezervacije (jedan upit!).
     * Ova metoda koristi SQL JOIN-ove kako bi dohvatila sve relevantne informacije o klijentima i ljubimcima u jednom upitu, eliminirajući potrebu za dodatnim upitima i time poboljšavajući performanse aplikacije. Rezultati su sortirani po datumu udomljavanja u opadajućem redoslijedu, tako da se najnovije rezervacije prikazuju prve.
     * @param idK ID klijenta za kojeg se dohvaćaju rezervacije
     * @return Lista Udomljen objekata koji imaju status "REZERVISAN" i pripadaju datom klijentu.
     * @throws SQLException ako dođe do greške pri čitanju podataka iz baze podataka.
     */
    public List<Udomljen> dobaviSveRezervacijeZaKorisnika(int idK) throws SQLException {
        System.out.println(idK);
        String sql = "SELECT " + aliasi() + "FROM udomljavanje u " +
                 "JOIN korisnik k ON u.idKlijenti = k.id " +
                 "JOIN ljubimac l ON u.idLjubimac = l.id " +
                 "WHERE u.status = 'REZERVISAN' AND u.idKlijenti = " + String.valueOf(idK) +
                 " ORDER BY u.datumUdomljavanja DESC";
        return izvrsiJoinUpit(sql);
    }
    
    /**
     * Dohvata specifičnu relaciju sa svim podacima. 
     * Ova metoda koristi SQL JOIN-ove kako bi dohvatila sve relevantne informacije o klijentu i ljubimcu u jednom upitu, eliminirajući potrebu za dodatnim upitima i time poboljšavajući performanse aplikacije. Rezultati su sortirani po datumu udomljavanja u opadajućem redoslijedu, tako da se najnovija relacija prikazuje prva.
     * Ova metoda je posebno korisna za provjeru da li postoji aktivna rezervacija između datog klijenta i ljubimca, ili za dohvat najnovije relacije između njih, bez obzira na status. Ako postoji više relacija, vraća se ona sa najsvježijim datumom udomljavanja.
     * 
     * @param idKlijenti ID klijenta
     * @param idLjubimac ID ljubimca
     * @return Udomljen objekat ako postoji, inače null
     * @throws SQLException ako dođe do greške pri čitanju podataka iz baze podataka.
     */
    public Udomljen dobaviRelaciju(int idKlijenti, int idLjubimac) throws SQLException {
        String sql = "SELECT "+aliasi()+" FROM udomljavanje u " +
                     "JOIN korisnik k ON u.idKlijenti = k.id " +
                     "JOIN ljubimac l ON u.idLjubimac = l.id " +
                     "WHERE u.idKlijenti = ? AND u.idLjubimac = ? " +
                     "ORDER BY u.datumUdomljavanja DESC LIMIT 1";
        
        try (Connection kon = getKone(); 
             PreparedStatement pstmt = kon.prepareStatement(sql)) {
            
            pstmt.setInt(1, idKlijenti);
            pstmt.setInt(2, idLjubimac);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapirajSaJoinom(rs);
                }
            }
        } catch (ParseException e) {
            throw new SQLException("Greška pri parsiranju datuma: " + e.getMessage());
        }
        return null;
    }
    
    /**
     * Dohvata najnoviju aktivnu rezervaciju za datog klijenta i ljubimca.
     * Aktivna znači: status = REZERVISAN i datum nije stariji od 3 dana.
     * Ako postoji više rezervacija, vraća se ona sa najsvježijim datumom.
     * 
     * @param idKlijenti ID klijenta
     * @param idLjubimac ID ljubimca
     * @return Udomljen objekat ako postoji aktivna rezervacija, inače null
     * @throws SQLException ako dođe do greške pri čitanju
     */
    public Udomljen dohvatiNajnovijuAktivnuRezervaciju(int idKlijenti, int idLjubimac) throws SQLException {
        String sql = "SELECT "+aliasi()+"FROM udomljavanje u " +
                     "JOIN korisnik k ON u.idKlijenti = k.id " +
                     "JOIN ljubimac l ON u.idLjubimac = l.id " +
                     "WHERE u.idKlijenti = ? AND u.idLjubimac = ? " +
                     "AND u.status = ? " +
                     "ORDER BY u.datumUdomljavanja DESC LIMIT 1";
        
        try (Connection kon = getKone(); 
             PreparedStatement pstmt = kon.prepareStatement(sql)) {
            
            pstmt.setInt(1, idKlijenti);
            pstmt.setInt(2, idLjubimac);
            pstmt.setString(3, StanjeLjubimca.REZERVISAN.toString());
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Udomljen u = mapirajSaJoinom(rs);
                    
                    // Provjera da li je istekla (3 dana)
                    if (!jeRezervacijaIstekla(dateFormat.parse(u.getDatumUdomljavanja()))) {
                        return u;
                    }
                }
            }
        } catch (ParseException e) {
            throw new SQLException("Greška pri parsiranju datuma: " + e.getMessage());
        }
        return null;
    }
    
    /**
     * Dohvata kompletnu historiju ljubimca (jedan upit!).
     * Ova metoda koristi SQL JOIN-ove kako bi dohvatila sve relevantne informacije o klijentima i ljubimcima u jednom upitu, eliminirajući potrebu za dodatnim upitima i time poboljšavajući performanse aplikacije. Rezultati su sortirani po datumu udomljavanja u opadajućem redoslijedu, tako da se najnovija udomljavanja prikazuju prva.
     * @param idLjubimca ID ljubimca
     * @return Lista Udomljen objekata koji predstavljaju kompletnu historiju udomljavanja datog ljubimca, uključujući informacije o klijentima, datumu udomljavanja i statusu svake relacije.
     * @throws SQLException ako dođe do greške pri čitanju podataka iz baze podataka.
     */
    public List<Udomljen> dobaviHistorijuLjubimca(int idLjubimca) throws SQLException {
        String sql = "SELECT "+aliasi()+" FROM udomljavanje u " +
                     "JOIN korisnik k ON u.idKlijenti = k.id " +
                     "JOIN ljubimac l ON u.idLjubimac = l.id " +
                     "WHERE u.idLjubimac = ? " +
                     "ORDER BY u.datumUdomljavanja DESC";
        
        try (Connection kon = getKone(); 
             PreparedStatement pstmt = kon.prepareStatement(sql)) {
            
            pstmt.setInt(1, idLjubimca);
            return izvrsiJoinUpit(pstmt);
        }
    }
    
    /**
     * Dohvata kompletnu historiju klijenta (jedan upit!).
     * Ova metoda koristi SQL JOIN-ove kako bi dohvatila sve relevantne informacije o klijentima i ljubimcima u jednom upitu, eliminirajući potrebu za dodatnim upitima i time poboljšavajući performanse aplikacije. Rezultati su sortirani po datumu udomljavanja u opadajućem redoslijedu, tako da se najnovija udomljavanja prikazuju prva.
     * @param idKlijenta ID klijenta
     * @return Lista Udomljen objekata koji predstavljaju kompletnu historiju udomljavanja datog klijenta, uključujući informacije o ljubimcima, datumu udomljavanja i statusu svake relacije.
     * @throws SQLException ako dođe do greške pri čitanju podataka iz baze podataka.
     */
    public List<Udomljen> dobaviHistorijuKlijenta(int idKlijenta) throws SQLException {
        String sql = "SELECT "+aliasi()+" FROM udomljavanje u " +
                     "JOIN korisnik k ON u.idKlijenti = k.id " +
                     "JOIN ljubimac l ON u.idLjubimac = l.id " +
                     "WHERE u.idKlijenti = ? " +
                     "ORDER BY u.datumUdomljavanja DESC";
        
        try (Connection kon = getKone(); 
             PreparedStatement pstmt = kon.prepareStatement(sql)) {
            
            pstmt.setInt(1, idKlijenta);
            return izvrsiJoinUpit(pstmt);
        }
    }
    
    /**
     * Dohvata kompletnu historiju rezervacija klijenta za jednog ljubimca.
     * Ova metoda koristi SQL JOIN-ove kako bi dohvatila sve relevantne informacije o klijentima i ljubimcima u jednom upitu, eliminirajući potrebu za dodatnim upitima i time poboljšavajući performanse aplikacije. Rezultati su sortirani po datumu udomljavanja u opadajućem redoslijedu, tako da se najnovija udomljavanja prikazuju prva.
     * @param idKlijenti ID klijenta
     * @param idLjubimac ID ljubimca
     * @return Lista Udomljen objekata koji predstavljaju kompletnu historiju rezervacija datog klijenta za datog ljubimca, uključujući informacije o klijentima, datumu rezervacije i statusu svake relacije.
     * @throws SQLException ako dođe do greške pri čitanju podataka iz baze podataka.
     */
    public List<Udomljen> dohvatiHistorijuRezervacijaKlijentaZaLjubimca(
            int idKlijenti, int idLjubimac) throws SQLException {
        
        String sql = "SELECT "+aliasi()+" FROM udomljavanje u " +
                     "JOIN korisnik k ON u.idKlijenti = k.id " +
                     "JOIN ljubimac l ON u.idLjubimac = l.id " +
                     "WHERE u.idKlijenti = ? AND u.idLjubimac = ? " +
                     "ORDER BY u.datumUdomljavanja DESC";
        
        try (Connection kon = getKone(); 
             PreparedStatement pstmt = kon.prepareStatement(sql)) {
            
            pstmt.setInt(1, idKlijenti);
            pstmt.setInt(2, idLjubimac);
            return izvrsiJoinUpit(pstmt);
        }
    }
    
    
    
    /**
     * Izvršava JOIN upit i vraća listu Udomljen objekata.
     * Ova metoda je dizajnirana da bude fleksibilna i da podržava različite scenarije udomljavanja, uključujući rezervacije, aktivna udomljavanja, te historiju udomljavanja za pojedinačne klijente i ljubimce. Korištenje JOIN-ova omogućava da se sve relevantne informacije o klijentima i ljubimcima dobiju u jednom upitu, što je posebno važno za funkcionalnosti koje zahtijevaju prikaz kompletnog konteksta udomljavanja.
     * @param sql SQL upit sa JOIN-ovima koji se izvršava. Očekuje se da ovaj upit sadrži sve potrebne kolone sa odgovaraju
     * nim aliasima kako bi se mogli pravilno mapirati podaci o klijentima i ljubimcima na Udomljen objekat.
     * @return Lista Udomljen objekata koji su rezultat izvršavanja SQL upita
     * @throws SQLException ako dođe do greške pri čitanju podataka iz baze podataka ili pri parsiranju datuma.
     */
    private List<Udomljen> izvrsiJoinUpit(String sql) throws SQLException {
        List<Udomljen> lista = new ArrayList<>();
        
        try (Connection kon = getKone();
             Statement st = kon.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            
            while (rs.next()) {
                try {
                    lista.add(mapirajSaJoinom(rs));
                } catch (ParseException e) {
                    throw new SQLException("Greška pri parsiranju datuma: " + e.getMessage());
                }
            }
        }
        return lista;
    }
    
    /**
     * Izvršava PreparedStatement JOIN upit i vraća listu Udomljen objekata.
     * Ova metoda je dizajnirana da bude fleksibilna i da podržava različite scenarije udomljavanja, uključujući rezervacije, aktivna udomljavanja, te historiju udomljavanja za pojedinačne klijente i ljubimce. Korištenje JOIN-ova omogućava da se sve relevantne informacije o klijentima i ljubimcima dobiju u jednom upitu, što je posebno važno za funkcionalnosti koje zahtijevaju prikaz kompletnog konteksta udomljavanja.
     * 
     * @param pstmt PreparedStatement sa JOIN upitom koji se izvršava.
     * @return Lista Udomljen objekata koji su rezultat izvršavanja SQL upita
     * @throws SQLException ako dođe do greške pri čitanju podataka iz baze podataka ili pri parsiranju datuma.
     */
    private List<Udomljen> izvrsiJoinUpit(PreparedStatement pstmt) throws SQLException {
        List<Udomljen> lista = new ArrayList<>();
        
        try (ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                try {
                    lista.add(mapirajSaJoinom(rs));
                } catch (ParseException e) {
                    throw new SQLException("Greška pri parsiranju datuma: " + e.getMessage());
                }
            }
        }
        return lista;
    }
    
    /**
     * Provjerava da li je rezervacija istekla (stara više od 3 dana).
     * Ova metoda se koristi za provjeru da li je aktivna rezervacija i dalje validna ili je prošlo više od 3 dana od datuma rezervacije, što bi značilo da je rezervacija istekla i da ljubimac može biti ponovo dostupan za rezervaciju ili udomljavanje.
     * @param datumRezervacije Datum rezervacije koji se provjerava.
     * @return true ako je rezervacija istekla (stara više od 3 dana), inače false.
     */
    private boolean jeRezervacijaIstekla(java.util.Date datumRezervacije) {
        long triDanaUMilis = 3L * 24 * 60 * 60 * 1000;
        long trenutno = System.currentTimeMillis();
        long datumRez = datumRezervacije.getTime();
        return (trenutno - datumRez) > triDanaUMilis;
    }
    
   
    /**
     * Dodaje novu relaciju između klijenta i ljubimca u tabelu 'udomljavanje'.
     * Ova metoda prima Udomljen objekat koji sadrži ID klijenta, ID ljubimca, datum udomljavanja i status. Ako status nije postavljen, podrazumijeva se da je rezervacija i postavlja se na "REZERVISAN". Metoda koristi PreparedStatement za sigurno umetanje podataka u bazu podataka i izbjegavanje SQL injekcija.
     * @param u Udomljen objekat koji sadrži informacije o klijentu, ljubimcu, datumu udomljavanja i statusu relacije koja se dodaje.
     * @throws SQLException ako dođe do greške pri umetanja podataka u bazu podataka.
    */
    public void dodajRelaciju(Udomljen u) throws SQLException {
        String sql = "INSERT INTO udomljavanje (idKlijenti, idLjubimac, datumUdomljavanja, status) " +
                     "VALUES (?, ?, ?, ?)";
        
        try (Connection kon = getKone(); 
             PreparedStatement pstmt = kon.prepareStatement(sql)) {
            
            pstmt.setInt(1, u.getIdKlijenti());
            pstmt.setInt(2, u.getIdLjubimac());
            pstmt.setString(3, u.getDatumUdomljavanja());
            pstmt.setString(4, u.getStatus() != null ? u.getStatus() : StanjeLjubimca.REZERVISAN.toString());
            pstmt.executeUpdate();
        }
    }
    
    /**
     * Dodaje rezervaciju ako ne postoji aktivna rezervacija između datog klijenta i ljubimca.
     * 
     * @param u Udomljen objekat koji sadrži ID klijenta, ID ljubimca, datum udomljavanja i status. Ova metoda će provjeriti da li već postoji aktivna rezervacija (status = REZERVISAN i datum nije stariji od 3 dana) između datog klijenta i ljubimca. Ako postoji, metoda neće dodati novu rezervaciju i vratit će false. Ako ne postoji, metoda će postaviti status na "REZERVISAN", dodati novu relaciju u bazu podataka i vratiti true.
     * @return true ako je rezervacija uspješno dodana, false ako već postoji aktivna rezervacija između datog klijenta i ljubimca.
     * @throws SQLException ako dođe do greške pri čitanju ili pisanju podataka u bazu podataka.
     */
    public boolean dodajRezervacijuAkoNePostojiAktivna(Udomljen u) throws SQLException {
        Udomljen postojeca = dohvatiNajnovijuAktivnuRezervaciju(
            u.getIdKlijenti(), 
            u.getIdLjubimac()
        );
        
        if (postojeca != null) {
            return false;
        }
        
        u.setStatus(StanjeLjubimca.REZERVISAN.toString());
        dodajRelaciju(u);
        return true;
    }
    
    /**
     * Otkazuje aktivnu rezervaciju između datog klijenta i ljubimca.
     * Ova metoda prvo provjerava da li postoji aktivna rezervacija između datog klijenta i ljubimca koristeći metodu dohvatiNajnovijuAktivnuRezervaciju. Ako ne postoji aktivna rezervacija, metoda vraća false. Ako postoji, metoda ažurira status rezervacije na "ISTEKLO" i istovremeno ažurira status ljubimca na "SLOBODAN", koristeći transakciju kako bi osigurala da oba ažuriranja budu izvršena atomarno. Ako je sve uspješno, metoda vraća true.
     * 
     * @param idKlijenti ID klijenta koji želi otkazati rezervaciju
     * @param idLjubimac ID ljubimca za kojeg se otkazuje rezervacija
     * @return true ako je rezervacija uspješno otkazana, false ako ne postoji aktivna rezervacija između datog klijenta i ljubimca.
     * @throws SQLException ako dođe do greške pri čitanju ili pisanju podataka u bazu podataka, ili pri upravljanju transakcijom.
     */
    public boolean otkaziAktivnuRezervaciju(int idKlijenti, int idLjubimac) throws SQLException {
        Udomljen aktivnaRez = dohvatiNajnovijuAktivnuRezervaciju(idKlijenti, idLjubimac);
        
        if (aktivnaRez == null) {
            return false;
        }
        
        String sqlRezervacija = "UPDATE udomljavanje SET status = ? " +
                                "WHERE idKlijenti = ? AND idLjubimac = ? AND status = ?";
        
        String sqlLjubimac = "UPDATE ljubimac SET status = ? WHERE id = ?";
        
        try (Connection kon = getKone()) {
            kon.setAutoCommit(false);
            
            try {
                // Ažuriraj rezervaciju
                try (PreparedStatement pstmt1 = kon.prepareStatement(sqlRezervacija)) {
                    pstmt1.setString(1, "ISTEKLO");
                    pstmt1.setInt(2, idKlijenti);
                    pstmt1.setInt(3, idLjubimac);
                    pstmt1.setString(4, StanjeLjubimca.REZERVISAN.toString());
                    pstmt1.executeUpdate();
                }
                
                // Oslobodi ljubimca
                try (PreparedStatement pstmt2 = kon.prepareStatement(sqlLjubimac)) {
                    pstmt2.setString(1, StanjeLjubimca.SLOBODAN.toString());
                    pstmt2.setInt(2, idLjubimac);
                    pstmt2.executeUpdate();
                }
                
                kon.commit();
                return true;
                
            } catch (SQLException e) {
                kon.rollback();
                throw e;
            } finally {
                kon.setAutoCommit(true);
            }
        }
    }
    
    
    /**
     * Vraća ljubimca sa udomljavanja, tj. ažurira status rezervacije na "VRACEN" i status ljubimca na "SLOBODAN".
     * Ova metoda koristi transakciju kako bi osigurala da oba ažuriranja budu izvršena atomarno. Prvo ažurira status rezervacije na "VRACEN" samo ako trenutno ima status "UDOMLJEN", a zatim ažurira status ljubimca na "SLOBODAN". Ako je sve uspješno, metoda se commit-a, inače se rollback-a u slučaju greške.
     * 
     * @param idKlijenti ID klijenta koji vraća ljubimca
     * @param idLjubimac ID ljubimca kojeg vraća klijent
     * @throws SQLException ako dođe do greške pri čitanju ili pisanju podataka u bazu podataka, ili pri upravljanju transakcijom.
     */
    public void vratiLjubimcaSaUdomljavanja(int idKlijenti, int idLjubimac) throws SQLException {
        String sqlUdomljavanje = "UPDATE udomljavanje SET status = ? " +
                                 "WHERE idKlijenti = ? AND idLjubimac = ? AND status = ?";
        
        try (Connection kon = getKone()) {
            kon.setAutoCommit(false);
            
            try (PreparedStatement pstmt = kon.prepareStatement(sqlUdomljavanje)) {
                pstmt.setString(1, StanjeLjubimca.VRACEN.toString());
                pstmt.setInt(2, idKlijenti);
                pstmt.setInt(3, idLjubimac);
                pstmt.setString(4, StanjeLjubimca.UDOMLJEN.toString());
                
                int redova = pstmt.executeUpdate();
                
                if (redova > 0) {
                    String sqlLjubimac = "UPDATE ljubimac SET status = ? WHERE id = ?";
                    try (PreparedStatement pstmt2 = kon.prepareStatement(sqlLjubimac)) {
                        pstmt2.setString(1, StanjeLjubimca.SLOBODAN.toString());
                        pstmt2.setInt(2, idLjubimac);
                        pstmt2.executeUpdate();
                    }
                    kon.commit();
                } else {
                    kon.rollback();
                }
            } catch (SQLException e) {
                kon.rollback();
                throw e;
            } finally {
                kon.setAutoCommit(true);
            }
        }
    }
    
    /**
     * Ažurira udomljavanje na "UDOMLJEN" i postavlja datum udomljavanja na trenutni datum.
     * Ova metoda koristi PreparedStatement za sigurno ažuriranje podataka u bazu podataka. Ažurira status udomljavanja na "UDOMLJEN" i postavlja datum udomljavanja na trenutni datum samo ako trenutno ima status "REZERVISAN". Ova metoda je ključna za proces finalizacije udomljavanja, gdje se rezervacija pretvara u aktivno udomljavanje.
     * 
     * @param u Udomljen objekat koji sadrži ID klijenta i ID ljubimca za koje se ažurira udomljavanje. Očekuje se da ovaj objekat ima postavljene ID-jeve, dok će datum udomljavanja i status biti postavljeni unutar metode.
     * @throws SQLException ako dođe do greške pri čitanju ili pisanju podataka u bazu podataka.
     */
    public void azurirajUdomljavanje(Udomljen u) throws SQLException {
        String sql = "UPDATE udomljavanje SET datumUdomljavanja = ?, status = ? " +
                     "WHERE idKlijenti = ? AND idLjubimac = ?";
        java.util.Date d = new java.util.Date();
        
        
        try (Connection kon = getKone(); 
             PreparedStatement pstmt = kon.prepareStatement(sql)) {
            
            pstmt.setString(1, dateFormat.format(d));
            pstmt.setString(2, u.getStatus());
            pstmt.setInt(3, u.getIdKlijenti());
            pstmt.setInt(4, u.getIdLjubimac());
            pstmt.executeUpdate();
        }
    }
    
    /**
     * Vraća listu ID-jeva klijenata koji su imali udomljavanje sa datim ljubimcem.
     * @param idLjubimac ID ljubimca za kojeg se traže klijenti koji su ga udomili. Ova metoda koristi SQL upit za dohvat svih ID-jeva klijenata iz tabele 'udomljavanje' koji su imali relaciju sa datim ljubimcem, bez obzira na status te relacije. Rezultati su vraćeni kao lista cijelih brojeva (ID-jeva klijenata).
     * @return Lista ID-jeva klijenata koji su imali udomljavanje sa datim ljubimcem. Ako nema takvih klijenata, vraća se prazna lista.
     */
    public ArrayList<Integer> vratiIdKlijenata(int idLjubimac) {
        return vratiListuId("SELECT idKlijenti FROM udomljavanje WHERE idLjubimac = ?", idLjubimac);
    }
    /**
     * Vraća listu ID-jeva ljubimaca koje je klijent imao udomljene.
     * @param idKlijenti ID klijenta za kojeg se traže udomljeni ljubimci. Ova metoda koristi SQL upit za dohvat svih ID-jeva ljubimaca iz tabele 'udomljavanje' koji su bili udomljeni od strane datog klijenta, bez obzira na status te relacije. Rezultati su vraćeni kao lista cijelih brojeva (ID-jeva ljubimaca).
     * @return Lista ID-jeva ljubimaca koje je klijent imao udomljene. Ako nema takvih ljubimaca, vraća se prazna lista.
     */
    public ArrayList<Integer> vratiIdLjubimaca(int idKlijenti) {
        return vratiListuId("SELECT idLjubimac FROM udomljavanje WHERE idKlijenti = ?", idKlijenti);
    }
    /**
     * Pomoćna metoda koja izvršava SQL upit za dohvat liste ID-jeva na osnovu datog parametra. Ova metoda je generička i koristi se za dohvat bilo koje liste ID-jeva, bilo da se radi o ID-jevima klijenata ili ljubimaca, ovisno o SQL upitu koji se prosljeđuje. Metoda koristi PreparedStatement za sigurno postavljanje parametra i izvršavanje upita, te vraća listu cijelih brojeva koji su rezultat tog upita.
     * @param sql SQL upit koji se izvršava. Očekuje se da ovaj upit sadrži jedan parametar (oznaka '?') koji će biti zamijenjen vrijednošću 'id' prilikom izvršavanja. Upit bi trebao vratiti jednu kolonu sa ID-jevima.
     * @param id ID koji se koristi za filtriranje rezultata.
     * @return Lista ID-jeva koja su rezultat izvršavanja SQL upita sa datim parametrom. Ako nema rezultata, vraća se prazna lista.
     */
    private ArrayList<Integer> vratiListuId(String sql, int id) {
        ArrayList<Integer> lista = new ArrayList<>();
        try (Connection kon = getKone(); 
             PreparedStatement pstmt = kon.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) lista.add(rs.getInt(1));
            }
        } catch (SQLException e) {
            System.err.println("Greška kod dohvata ID-ova: " + e.getMessage());
        }
        return lista;
    }
    
    /**
     * Provjerava da li klijent ima makar jednu aktivnu rezervaciju.
     * 
     * @param idKlijenti ID klijenta
     * @return craća <code>true</code> ako ima barem jednu aktivnu rezervaciju, inače <code>false</code>.
     * @throws SQLException U slučaju greške pri radu sa bazom podataka.
     */
    public boolean imaLiKlijentAktivnuRezervaciju(int idKlijenti) throws SQLException {
        String sql = "SELECT COUNT(*) FROM udomljavanje " +
                     "WHERE idKlijenti = ? AND status = ?";

        try (Connection kon = getKone(); 
             PreparedStatement pstmt = kon.prepareStatement(sql)) {

            pstmt.setInt(1, idKlijenti);
            pstmt.setString(2, StanjeLjubimca.REZERVISAN.toString());

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }
    /**
     * Automatski pronalazi i poništava rezervacije starije od 3 dana.
     * Proces se odvija u dva koraka:
     * <br>
     * 1. Identifikacija isteklih rezervacija<br>
     * 2. Grupno ažuriranje statusa unutar transakcije
     * <br>
     * 
     * Ova metoda je ključna za održavanje integriteta podataka i osiguravanje da se ljubimci koji su rezervisani ali nisu finalizirani udomljavanjem, a čije su rezervacije istekle, ponovo postavljaju na status "SLOBODAN" kako bi bili dostupni drugim potencijalnim klijentima. Korištenje transakcija osigurava da se svi povezani upiti izvrše atomarno, čime se sprječavaju nedosljednosti u slučaju grešaka tijekom procesa poništavanja rezervacija.
     * Napomene:
     * - Ova metoda se može pozivati periodično (npr. putem schedulera) kako bi se automatski održavao sistem bez potrebe za ručnim intervencijama.
     * - Broj poništenih rezervacija se vraća kao rezultat metode, što može biti korisno za logiranje ili praćenje aktivnosti sistema.
     * - U slučaju greške pri radu sa bazom podataka, metoda će baciti SQLException, što omogućava pozivatelju da adekvatno reagira na takve situacije (npr. logiranje greške, obavještavanje administratora, itd.).
     * Primjer upotrebe:
     * <pre>
     *     UdomljavanjeCRUD crud = new UdomljavanjeCRUD();
     *     int brojPonistenih = crud.ponistiIstekleRezervacije();
     * </pre>
     * 
     * @return Broj poništenih (isteklih) rezervacija
     * @throws SQLException U slučaju greške pri radu sa bazom
     */
    public int ponistiIstekleRezervacije() throws SQLException {
        // 1. KORAK: Pronađi sve istekle rezervacije
        List<int[]> zaPonistiti = new ArrayList<>();

        String sqlRezervacije = "SELECT idKlijenti, idLjubimac, datumUdomljavanja " +
                                "FROM udomljavanje " +
                                "WHERE status = ?";

        try (Connection kon = getKone();
             PreparedStatement pstmt = kon.prepareStatement(sqlRezervacije)) {

            pstmt.setString(1, StanjeLjubimca.REZERVISAN.toString());

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    try {
                        java.util.Date datumRez = dateFormat.parse(rs.getString("datumUdomljavanja"));
                        if (jeRezervacijaIstekla(datumRez)) {
                            zaPonistiti.add(new int[]{
                                rs.getInt("idKlijenti"), 
                                rs.getInt("idLjubimac")
                            });
                        }
                    } catch (ParseException e) {
                        System.err.println("Greška pri parsiranju datuma: " + e.getMessage());
                    }
                }
            }
        }

        // 2. KORAK: Poništi sve istekle rezervacije u transakciji
        if (zaPonistiti.isEmpty()) {
            return 0;
        }

        int brojPonistenih = 0;

        try (Connection kon = getKone()) {
            kon.setAutoCommit(false);

            try {
                for (int[] par : zaPonistiti) {
                    if (izvrsiPonistavanjeRezervacije(kon, par[0], par[1])) {
                        brojPonistenih++;
                    }
                }
                kon.commit();
                System.out.println("Poništeno " + brojPonistenih + " isteklih rezervacija.");
            } catch (SQLException e) {
                kon.rollback();
                throw e;
            } finally {
                kon.setAutoCommit(true);
            }
        }

        return brojPonistenih;
    }

    /**
     * Pomoćna metoda za poništavanje jedne istekle rezervacije unutar transakcije.
     * Ova metoda ažurira status rezervacije na "ISTEKLO" samo ako trenutno ima status "REZERVISAN", a zatim ažurira status ljubimca na "SLOBODAN". Ova metoda je dizajnirana da se koristi unutar transakcije, gdje se očekuje da će biti pozvana za svaki par (idKlijenti, idLjubimac) koji predstavlja isteklu rezervaciju. Ako je rezervacija uspješno poništena, metoda vraća true, inače false (ako nije bilo promjene, npr. ako rezervacija već nije bila aktivna).
     * Napomene:
     * - Ova metoda ne upravlja transakcijom sama po sebi, već se očekuje da će biti pozvana unutar već započete transakcije u metodi ponistiIstekleRezervacije.
     * - Ova metoda je ključna za osiguravanje da se svi povezani upiti (ažuriranje rezervacije i ažuriranje ljubimca) izvrše atomarno, čime se sprječavaju nedosljednosti u slučaju grešaka tijekom procesa poništavanja rezervacija.
     * - U slučaju greške pri radu sa bazom podataka, metoda će baciti SQLException, što omogućava pozivatelju da adekvatno reagira na takve situacije (npr. logiranje greške, obavještavanje administratora, itd.).
     *      
     * @param kon Aktivna SQL konekcija
     * @param idKlijenti ID klijenta
     * @param idLjubimac ID ljubimca
     * @return vraća <code>true</code> ako je rezervacija poništena, <code>false</code> ako nije bilo promjene
     * @throws SQLException greška pri radu sa bazom podataka
     */
    private boolean izvrsiPonistavanjeRezervacije(Connection kon, int idKlijenti, int idLjubimac) 
            throws SQLException {

        // 1. Ažuriraj status rezervacije u ISTEKLO
        String sqlRezervacija = "UPDATE udomljavanje SET status = ? " +
                                "WHERE idKlijenti = ? AND idLjubimac = ? " +
                                "AND status = ?";

        try (PreparedStatement pstmt1 = kon.prepareStatement(sqlRezervacija)) {
            pstmt1.setString(1, StanjeLjubimca.ISTEKLO.toString());
            pstmt1.setInt(2, idKlijenti);
            pstmt1.setInt(3, idLjubimac);
            pstmt1.setString(4, StanjeLjubimca.REZERVISAN.toString());

            int redova = pstmt1.executeUpdate();

            if (redova > 0) {
                // 2. Oslobodi ljubimca (vrati ga u status SLOBODAN)
                String sqlLjubimac = "UPDATE ljubimac SET status = ? WHERE id = ?";

                try (PreparedStatement pstmt2 = kon.prepareStatement(sqlLjubimac)) {
                    pstmt2.setString(1, StanjeLjubimca.SLOBODAN.toString());
                    pstmt2.setInt(2, idLjubimac);
                    pstmt2.executeUpdate();
                }
                return true;
            }
        }

        return false;
    }

    /**
     * Poništava sve istekle rezervacije za određenog klijenta.
     * Ova metoda funkcioniše slično kao ponistiIstekleRezervacije, ali je ograničena samo na rezervacije koje pripadaju određenom klijentu. Prvo pronalazi sve rezervacije datog klijenta koje su istekle (stare više od 3 dana), a zatim ih grupno poništava unutar transakcije. Broj poništenih rezervacija se vraća kao rezultat metode, što može biti korisno za logiranje ili praćenje aktivnosti sistema.
     * Napomene:
     * - Ova metoda se može koristiti kao dio procesa održavanja sistema, gdje se periodično provjerava da li klijenti imaju istekle rezervacije i automatski ih poništavaju kako bi se ljubimci ponovo postavljali na status "SLOBODAN".
     * - U slučaju greške pri radu sa bazom podataka, metoda će baciti SQLException, što omogućava pozivatelju da adekvatno reagira na takve situacije (npr. logiranje greške, obavještavanje administratora, itd.).
     * 
     * 
     * @param idKlijenti ID klijenta za kojeg se poništavaju istekle rezervacije
     * @return Broj poništenih rezervacija za datog klijenta
     * @throws SQLException Ako dođe do greške u radu sa bazom podataka
     */
    public int ponistiIstekleRezervacijeZaKlijenta(int idKlijenti) throws SQLException {
        List<int[]> zaPonistiti = new ArrayList<>();

        String sql = "SELECT idLjubimac, datumUdomljavanja FROM udomljavanje " +
                     "WHERE idKlijenti = ? AND status = ?";

        try (Connection kon = getKone();
             PreparedStatement pstmt = kon.prepareStatement(sql)) {

            pstmt.setInt(1, idKlijenti);
            pstmt.setString(2, StanjeLjubimca.REZERVISAN.toString());

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    try {
                        java.util.Date datumRez = dateFormat.parse(rs.getString("datumUdomljavanja"));
                        if (jeRezervacijaIstekla(datumRez)) {
                            zaPonistiti.add(new int[]{idKlijenti, rs.getInt("idLjubimac")});
                        }
                    } catch (ParseException e) {
                        System.err.println("Greška pri parsiranju datuma: " + e.getMessage());
                    }
                }
            }
        }

        if (zaPonistiti.isEmpty()) {
            return 0;
        }

        int brojPonistenih = 0;

        try (Connection kon = getKone()) {
            kon.setAutoCommit(false);

            try {
                for (int[] par : zaPonistiti) {
                    if (izvrsiPonistavanjeRezervacije(kon, par[0], par[1])) {
                        brojPonistenih++;
                    }
                }
                kon.commit();
            } catch (SQLException e) {
                kon.rollback();
                throw e;
            } finally {
                kon.setAutoCommit(true);
            }
        }

        return brojPonistenih;
    }

    /**
     * Poništava jednu specifičnu rezervaciju (bez obzira na datum).
     * Ova metoda je dizajnirana da poništi aktivnu rezervaciju između datog klijenta i ljubimca, bez obzira na to koliko je stara ta rezervacija. Metoda koristi transakciju kako bi osigurala da oba ažuriranja (ažuriranje rezervacije i ažuriranje ljubimca) budu izvršena atomarno. Ako postoji aktivna rezervacija između datog klijenta i ljubimca, metoda će ažurirati status rezervacije na "ISTEKLO" i status ljubimca na "SLOBODAN", te vratiti true. Ako ne postoji aktivna rezervacija, metoda će vratiti false.
     * Napomene:
     * - Ova metoda se može koristiti u scenarijima gdje klijent želi ručno otkazati rezervaciju, bez obzira na to koliko je stara ta rezervacija. Na primjer, ako klijent shvati da više nije zainteresovan za određenog ljubimca ili ako želi promijeniti rezervaciju, može koristiti ovu metodu za poništavanje postojeće rezervacije.
     * - U slučaju greške pri radu sa bazom podataka, metoda će baciti SQLException, što omogućava pozivatelju da adekvatno reagira na takve situacije (npr. logiranje greške, obavještavanje administratora, itd.).
     * 
     * @param idKlijenti ID klijenta koji želi poništiti rezervaciju
     * @param idLjubimac ID ljubimca kojem se poništava rezervacija
     * @return vraća <code>true</code> ako je rezervacija poništena, inače <code>false</code>
     * @throws SQLException Ako dođe do greške u radu sa bazom podataka
     */
    public boolean ponistiRezervaciju(int idKlijenti, int idLjubimac) throws SQLException {
        try (Connection kon = getKone()) {
            kon.setAutoCommit(false);

            try {
                boolean uspjeh = izvrsiPonistavanjeRezervacije(kon, idKlijenti, idLjubimac);
                kon.commit();
                return uspjeh;
            } catch (SQLException e) {
                kon.rollback();
                throw e;
            } finally {
                kon.setAutoCommit(true);
            }
        }
    }
}