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
 * 
 * @author Amel Džanić
 * @version 2.0
 */

public class UdomljavanjeCRUD extends korisni.Kontroler {
    
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    
    public UdomljavanjeCRUD() throws SQLException {
        createTable();
    }
    
    /**
     * Kreira veznu tabelu sa stranim ključevima.
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
    
    // ==================== MAPIRANJE SA JOIN-OVIMA ====================
    
    /**
     * Mapira kompletan red iz JOIN upita u Udomljen objekat.
     * Očekuje da ResultSet sadrži kolone iz sve tri tabele.
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
    
    // ==================== OPTIMIZOVANE METODE ====================
    
    /**
     * Dohvata sva udomljavanja sa svim podacima (jedan upit!).
     */
    public List<Udomljen> dobaviSvaUdomljavanja() throws SQLException {
        String sql = "SELECT "+aliasi()+" FROM udomljavanje u " +
                     "JOIN korisnik k ON u.idKlijenti = k.id " +
                     "JOIN ljubimac l ON u.idLjubimac = l.id " +
                     "WHERE u.status = '" +StanjeLjubimca.UDOMLJEN.toString()+"' "+
                     "ORDER BY u.datumUdomljavanja DESC";
        
        return izvrsiJoinUpit(sql);
    }
    
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
     */
    public List<Udomljen> dobaviSveRezervacijeZaKorisnika(int idK) throws SQLException {
        String sql = "SELECT " + aliasi() + "FROM udomljavanje u " +
                 "JOIN korisnik k ON u.idKlijenti = k.id " +
                 "JOIN ljubimac l ON u.idLjubimac = l.id " +
                 "WHERE u.status = 'REZERVISAN' AND u.idKlijenti = " + String.valueOf(idK) +
                 " ORDER BY u.datumUdomljavanja DESC";
        return izvrsiJoinUpit(sql);
    }
    
    /**
     * Dohvata specifičnu relaciju sa svim podacima.
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
    
    // ==================== POMOĆNE METODE ====================
    
    /**
     * Izvršava JOIN upit i vraća listu Udomljen objekata.
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
     * Provjerava da li je rezervacija starija od 3 dana.
     */
    private boolean jeRezervacijaIstekla(java.util.Date datumRezervacije) {
        long triDanaUMilis = 3L * 24 * 60 * 60 * 1000;
        long trenutno = System.currentTimeMillis();
        long datumRez = datumRezervacije.getTime();
        return (trenutno - datumRez) > triDanaUMilis;
    }
    
    // ==================== CRUD OPERACIJE ====================
    
    /**
     * Dodaje novu rezervaciju/udomljavanje.
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
     * Dodaje rezervaciju samo ako ne postoji aktivna.
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
     * Otkazuje aktivnu rezervaciju.
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
     * Vraća ljubimca sa udomljavanja.
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
     * Ažurira postojeću relaciju.
     */
    public void azurirajUdomljavanje(Udomljen u) throws SQLException {
        String sql = "UPDATE udomljavanje SET datumUdomljavanja = ?, status = ? " +
                     "WHERE idKlijenti = ? AND idLjubimac = ?";
        
        try (Connection kon = getKone(); 
             PreparedStatement pstmt = kon.prepareStatement(sql)) {
            
            pstmt.setString(1, dateFormat.format(u.getDatumUdomljavanja()));
            pstmt.setString(2, u.getStatus());
            pstmt.setInt(3, u.getIdKlijenti());
            pstmt.setInt(4, u.getIdLjubimac());
            pstmt.executeUpdate();
        }
    }
    
    // ==================== POMOĆNE METODE ZA ID-OVE ====================
    
    public ArrayList<Integer> vratiIdKlijenata(int idLjubimac) {
        return vratiListuId("SELECT idKlijenti FROM udomljavanje WHERE idLjubimac = ?", idLjubimac);
    }
    
    public ArrayList<Integer> vratiIdLjubimaca(int idKlijenti) {
        return vratiListuId("SELECT idLjubimac FROM udomljavanje WHERE idKlijenti = ?", idKlijenti);
    }
    
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
     * @return true ako ima barem jednu aktivnu rezervaciju
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
     * 1. Identifikacija isteklih rezervacija
     * 2. Grupno ažuriranje statusa unutar transakcije
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
     * 
     * @param kon Aktivna SQL konekcija
     * @param idKlijenti ID klijenta
     * @param idLjubimac ID ljubimca
     * @return true ako je rezervacija poništena, false ako nije bilo promjene
     * @throws SQLException Ako upit ne uspije
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
     * 
     * @param idKlijenti ID klijenta
     * @return Broj poništenih rezervacija
     * @throws SQLException Ako dođe do greške
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
     * 
     * @param idKlijenti ID klijenta
     * @param idLjubimac ID ljubimca
     * @return true ako je rezervacija poništena
     * @throws SQLException Ako dođe do greške
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