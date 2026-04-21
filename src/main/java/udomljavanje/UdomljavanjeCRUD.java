package udomljavanje;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import ljubimac.LjubimacCRUD;
import klijent.KlijentCRUD;

/**
 * Kontroler klasa koja upravlja veznom tabelom 'udomljavanje'.
 * Realizuje M:N relaciju, prati statuse udomljenja/rezervacija i 
 * upravlja historijom vraćanja ljubimaca.
 * 
 * @author Amel Džanić
 * @version 1.2
 */
public class UdomljavanjeCRUD extends korisni.Kontroler {
    /** Servis za rad sa klijentima. */
    private final KlijentCRUD kk = new KlijentCRUD();
    /** Servis za rad sa ljubimcima. */
    private final LjubimacCRUD lk = new LjubimacCRUD();
    /** Standardni format za spašavanje datuma u SQLite bazu. */
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");   
    /**
     * Konstruktor koji osigurava postojanje tabele udomljavanje.
     * 
     * @throws SQLException U slučaju greške pri inicijalizaciji baze.
     */
    public UdomljavanjeCRUD() throws SQLException {
        createTable();       
    }

    /**
     * Mapira red iz baze u objekat klase {@link Udomljen}.
     * Vrši parsiranje datuma iz String formata u {@link java.util.Date}.
     * 
     * @param rs ResultSet pozicioniran na odgovarajući red.
     * @return {@link Udomljen} model.
     * @throws SQLException Ako format datuma nije ispravan ili kolone nedostaju.
     */
    private Udomljen mapirajUdomljavanje(ResultSet rs) throws SQLException {
        try {
            Udomljen u = new Udomljen(
                rs.getInt("idKlijenti"),
                rs.getInt("idLjubimac"),
                dateFormat.parse(rs.getString("datumUdomljavanja"))
            );
            // Opcionalno: u.setStatus(rs.getString("status"));
            return u;
        } catch (ParseException e) {
            throw new SQLException("Greška u formatu datuma baze: " + e.getMessage());
        }
    }
    /**
     * Dodaje novi zapis o udomljavanju ili rezervaciji u bazu.
     * 
     * @param u Objekat koji sadrži ID-ove klijenta, ljubimca i datum.
     * @throws SQLException Ako upit ne uspije.
     */
    public void dodajRelaciju(Udomljen u) throws SQLException {
        String sql = "INSERT INTO udomljavanje (idKlijenti, idLjubimac, datumUdomljavanja, status) VALUES (?, ?, ?, ?)";
        try (Connection kon = getKone(); PreparedStatement pstmt = kon.prepareStatement(sql)) {
            pstmt.setInt(1, u.getIdKlijenti());
            pstmt.setInt(2, u.getIdLjubimac());
            pstmt.setString(3, dateFormat.format(u.getDatumUdomljavanja()));
            pstmt.setString(4, u.getStatus());
            pstmt.executeUpdate();
        }
    }
    /**
     * Dobavlja listu svih završenih udomljavanja (isključuje rezervacije).
     * 
     * @return Lista realizovanih udomljenja.
     * @throws SQLException Greška pri čitanju podataka.
     */
    public List<Udomljen> dobaviSvaUdomljavanja() throws SQLException {
        return dobaviListu("SELECT * FROM udomljavanje WHERE status != 'REZERVISAN'");
    }
    /**
     * Dobavlja listu svih aktivnih rezervacija.
     * 
     * @return Lista rezervisala.
     * @throws SQLException Greška u SQL upitu.
     */
    public List<Udomljen> dobaviSveRezervacije() throws SQLException {
        return dobaviListu("SELECT * FROM udomljavanje WHERE status = 'REZERVISAN'");
    }
    /**
     * Pomoćna metoda koja izvršava upit i popunjava listu objektima.
     * Automatski učitava povezane objekte Klijent i Ljubimac.
     * 
     * @param sql SQL upit za filtriranje.
     * @return Popunjena lista udomljenja.
     * @throws SQLException Greška pri mapiranju ili dohvatu.
     */
    private List<Udomljen> dobaviListu(String sql) throws SQLException {
        List<Udomljen> lista = new ArrayList<>();
        try (Connection kon = getKone(); 
             Statement st = kon.createStatement(); 
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Udomljen u = mapirajUdomljavanje(rs);
                // Popunjavanje objekata podacima iz drugih tabela
                u.setKlijent(kk.dobaviKlijentaPoId(u.getIdKlijenti()));
                u.setLjub(lk.dobaviLjubimcaPoId(u.getIdLjubimac()));
                lista.add(u);
            }
        }
        return lista;
    }
    /**
     * Dobavlja specifičnu relaciju na osnovu primarnog ključa (Klijent-Ljubimac).
     * 
     * @param idKlijenti ID udomitelja.
     * @param idLjubimac ID životinje.
     * @return {@link Udomljen} objekat ili {@code null}.
     * @throws SQLException Greška pri dohvatu.
     */
    public Udomljen dobaviRelaciju(int idKlijenti, int idLjubimac) throws SQLException {
        String sql = "SELECT * FROM udomljavanje WHERE idKlijenti = ? AND idLjubimac = ?";
        try (Connection kon = getKone(); PreparedStatement pstmt = kon.prepareStatement(sql)) {
            pstmt.setInt(1, idKlijenti);
            pstmt.setInt(2, idLjubimac);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Udomljen u = mapirajUdomljavanje(rs);
                    u.setKlijent(kk.dobaviKlijentaPoId(u.getIdKlijenti()));
                    u.setLjub(lk.dobaviLjubimcaPoId(u.getIdLjubimac()));
                    return u;
                }
            }
        }
        return null;
    }
    /**
     * Ažurira status ili datum postojećeg udomljavanja.
     * 
     * @param u Objekat sa novim podacima.
     * @throws SQLException Greška pri ažuriranju.
     */
    public void azurirajUdomljavanje(Udomljen u) throws SQLException {
        String sql = "UPDATE udomljavanje SET datumUdomljavanja = ?, status = ? WHERE idKlijenti = ? AND idLjubimac = ?";
        try (Connection kon = getKone(); PreparedStatement pstmt = kon.prepareStatement(sql)) {
            pstmt.setString(1, dateFormat.format(u.getDatumUdomljavanja()));
            pstmt.setString(2, u.getStatus());
            pstmt.setInt(3, u.getIdKlijenti());
            pstmt.setInt(4, u.getIdLjubimac());
            pstmt.executeUpdate();
        }
    }
   
    //Ne koristiti
    public void obrisiUdomljavanje(int idKlijenti, int idLjubimac) throws SQLException {
        String sql = "DELETE FROM udomljavanje WHERE idKlijenti = ? AND idLjubimac = ?";
        
        // Prvo oslobađamo ljubimce (postavljamo status na SLOBODAN/NE)
        ArrayList<Integer> ljubimciIds = vratiIdLjubimaca(idKlijenti);
        for (Integer ljId : ljubimciIds) {
            lk.ukloniUdomljavanje(lk.dobaviLjubimcaPoId(ljId));
        }

        try (Connection kon = getKone(); PreparedStatement pstmt = kon.prepareStatement(sql)) {
            pstmt.setInt(1, idKlijenti);
            pstmt.setInt(2, idLjubimac);
            pstmt.executeUpdate();
        }
    }
    /**
     * Kreira veznu tabelu 'udomljavanje' sa stranim ključevima.
     * 
     * @throws SQLException Greška pri definisanju šeme baze.
     */
    public final void createTable() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS udomljavanje (" +
                     "idKlijenti INTEGER NOT NULL, " +
                     "idLjubimac INTEGER NOT NULL, " +
                     "datumUdomljavanja TEXT NOT NULL, " +
                     "status TEXT, " +
                     "PRIMARY KEY (idKlijenti, idLjubimac), " +
                     "FOREIGN KEY (idKlijenti) REFERENCES klijent(id) ON DELETE CASCADE, " + // Dodano CASCADE
                     "FOREIGN KEY (idLjubimac) REFERENCES ljubimac(id) ON DELETE CASCADE"   + // Dodano CASCADE
                     ")";
        try (Connection kon = getKone(); Statement st = kon.createStatement()) {
            st.execute(sql);
        }
    }

    /** @return Lista ID-ova klijenata povezanih sa ljubimcem. */   
    public ArrayList<Integer> vratiIdKlijenata(int idLjubimac) {
        return vratiListuId("SELECT idKlijenti FROM udomljavanje WHERE idLjubimac = ?", idLjubimac);
    }
    /** @return Lista ID-ova ljubimaca povezanih sa klijentom. */   
    public ArrayList<Integer> vratiIdLjubimaca(int idKlijenti) {
        return vratiListuId("SELECT idLjubimac FROM udomljavanje WHERE idKlijenti = ?", idKlijenti);
    }

    /**
     * Pomoćna metoda za dohvaćanje liste cijelih brojeva (ID-ova) iz baze.
     * 
     * @param sql SQL upit.
     * @param id  ID za filtriranje.
     * @return Lista rezultujućih ID-ova.
     */
    private ArrayList<Integer> vratiListuId(String sql, int id) {
        ArrayList<Integer> lista = new ArrayList<>();
        try (Connection kon = getKone(); PreparedStatement pstmt = kon.prepareStatement(sql)) {
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
     * Vraća kompletnu historiju udomljavanja za određenog ljubimca.
     * Uključuje sve klijente koji su ikada imali interakciju sa ljubimcem (rezervacije, 
     * trenutna udomljenja, vraćene životinje). Rezultati su sortirani po datumu, 
     * od najnovijeg ka starijem.
     * 
     * @param idLjubimca Identifikator ljubimca za kojeg se traži historija.
     * @return Lista {@link Udomljen} objekata sa popunjenim podacima o klijentima.
     * @throws SQLException Ako dođe do greške pri čitanju iz baze.
     */
    public List<Udomljen> dobaviHistorijuLjubimca(int idLjubimca) throws SQLException {
        List<Udomljen> historija = new ArrayList<>();
        String sql = "SELECT * FROM udomljavanje WHERE idLjubimac = ? ORDER BY datumUdomljavanja DESC";

        try (Connection kon = getKone(); 
             PreparedStatement pstmt = kon.prepareStatement(sql)) {

            pstmt.setInt(1, idLjubimca);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Udomljen u = mapirajUdomljavanje(rs);
                    // Punimo podatke o klijentu da znamo IME osobe u historiji
                    u.setKlijent(kk.dobaviKlijentaPoId(u.getIdKlijenti()));
                    historija.add(u);
                }
            }
        }
        return historija;
    }
    /**
     * Vraća kompletnu historiju udomljavanja za određenog klijenta.
     * Uključuje sve ljubimce koji su ikada imali interakciju sa klijentom (rezervacije, 
     * trenutna udomljenja, vraćene životinje). Rezultati su sortirani po datumu, 
     * od najnovijeg ka starijem.
     * 
     * @param idKlijenta Identifikator klijenta za kojeg se traži historija.
     * @return Lista {@link Udomljen} objekata sa popunjenim podacima o ljubimcima.
     * @throws SQLException Ako dođe do greške pri čitanju iz baze.
     */
    public List<Udomljen> dobaviHistorijuKlijenta(int idKlijenta) throws SQLException {
        List<Udomljen> lista = new ArrayList<>();
        String sql = "SELECT * FROM udomljavanje WHERE idKlijenti = ? ORDER BY datumUdomljavanja DESC";

        try (Connection kon = getKone(); PreparedStatement pstmt = kon.prepareStatement(sql)) {
            pstmt.setInt(1, idKlijenta);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Udomljen u = mapirajUdomljavanje(rs);
                    // Punimo podatke o ljubimcu da znamo IME životinje
                    u.setLjub(lk.dobaviLjubimcaPoId(u.getIdLjubimac()));
                    lista.add(u);
                }
            }
        }
        return lista;
    }
    
     /**
     * Proces vraćanja ljubimca u sklonište. 
     * Koristi transakciju kako bi osigurao da se status promijeni u obje tabele istovremeno.
     * 
     * @param idKlijenti ID klijenta koji vraća ljubimca.
     * @param idLjubimac ID ljubimca koji se vraća.
     * @throws SQLException U slučaju greške, transakcija se vraća (rollback).
     */
    
    public void vratiLjubimcaSaUdomljavanja(int idKlijenti, int idLjubimac) throws SQLException {
        // 1. SQL za promjenu statusa u relaciji (udomljavanje postaje historija)
        String sqlUdomljavanje = "UPDATE udomljavanje SET status = 'VRACEN' " +
                                 "WHERE idKlijenti = ? AND idLjubimac = ? AND status != 'VRACEN'";

        try (Connection kon = getKone()) {
            // Isključujemo auto-commit za transakciju (sigurnost)
            kon.setAutoCommit(false);

            try (PreparedStatement pstmt = kon.prepareStatement(sqlUdomljavanje)) {
                pstmt.setInt(1, idKlijenti);
                pstmt.setInt(2, idLjubimac);
                int redova = pstmt.executeUpdate();

                // 2. Ako je status uspješno promijenjen, oslobađamo ljubimca u tabeli ljubimac
                if (redova > 0) {
                    ljubimac.Ljubimac lj = lk.dobaviLjubimcaPoId(idLjubimac);
                    if (lj != null) {
                        lk.ukloniUdomljavanje(lj); // Ova metoda u LjubimacCRUD postavlja status na 'SLOBODAN' ili 'NE'
                    }
                    kon.commit(); // Potvrdi promjene u obje tabele
                } else {
                    kon.rollback(); // Ako ništa nije promijenjeno, vrati na staro
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
     * Automatski pronalazi i poništava rezervacije starije od 3 dana.
     * Proces se odvija u dva koraka: identifikacija isteklih rezervacija i 
     * grupno ažuriranje statusa unutar jedne transakcije.
     * 
     * @throws SQLException U slučaju greške pri radu sa bazom ili prekida transakcije.
     */
    public void ponistiIstekleRezervacije() throws SQLException {
        String sqlRezervacije = "SELECT idKlijenti, idLjubimac, datumUdomljavanja FROM udomljavanje WHERE status = 'REZERVISAN'";
        List<int[]> zaPonistiti = new ArrayList<>();

        long triDanaUMilis = 3L * 24 * 60 * 60 * 1000;
        long trenutno = System.currentTimeMillis();

        // 1. KORAK: Samo čitanje (Zatvaramo RS odmah nakon čitanja)
        try (Connection kon = getKone();
             Statement st = kon.createStatement();
             ResultSet rs = st.executeQuery(sqlRezervacije)) {

            while (rs.next()) {
                try {
                    long datumRez = dateFormat.parse(rs.getString("datumUdomljavanja")).getTime();
                    if (trenutno - datumRez > triDanaUMilis) {
                        zaPonistiti.add(new int[]{rs.getInt("idKlijenti"), rs.getInt("idLjubimac")});
                    }
                } catch (ParseException e) { e.printStackTrace(); }
            }
        }

        // 2. KORAK: Ažuriranje (Nova konekcija nakon što je prva zatvorena)
        if (!zaPonistiti.isEmpty()) {
            try (Connection kon = getKone()) {
                kon.setAutoCommit(false); // Transakcija
                try {
                    for (int[] par : zaPonistiti) {
                        izvrsiOslobadjanje(kon, par[0], par[1]);
                    }
                    kon.commit();
                } catch (SQLException e) {
                    kon.rollback();
                    throw e;
                }
            }
        }
    }
    /**
     * Pomoćna metoda za oslobađanje ljubimca unutar postojeće transakcije.
     * Postavlja status udomljavanja na 'ISTEKLO' i vraća status ljubimca na 'SLOBODAN'.
     * 
     * @param kon Aktivna SQL konekcija.
     * @param idK ID klijenta čija rezervacija ističe.
     * @param idLj ID ljubimca koji se oslobađa.
     * @throws SQLException Ako bilo koji od dva povezana upita ne uspije.
     */
    private void izvrsiOslobadjanje(Connection kon, int idK, int idLj) throws SQLException {
        // Ažuriraj udomljavanje
        String sql1 = "UPDATE udomljavanje SET status = 'ISTEKLO' WHERE idKlijenti = ? AND idLjubimac = ?";
        try (PreparedStatement p1 = kon.prepareStatement(sql1)) {
            p1.setInt(1, idK);
            p1.setInt(2, idLj);
            p1.executeUpdate();
        }

        // Ažuriraj ljubimca (Direktni SQL da izbjegnemo otvaranje nove konekcije u lk)
        String sql2 = "UPDATE ljubimac SET status = 'SLOBODAN' WHERE id = ?";
        try (PreparedStatement p2 = kon.prepareStatement(sql2)) {
            p2.setInt(1, idLj);
            p2.executeUpdate();
        }
    }
}
