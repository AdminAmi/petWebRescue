package ljubimac;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import klijent.KlijentCRUD;
import udomljavanje.UdomljavanjeCRUD;

/**
 * Kontroler klasa zadužena za CRUD operacije nad objektima tipa {@link Ljubimac}.
 * Upravlja komunikacijom sa bazi podataka, kreiranjem tabele i sinhronizacijom veza.
 * Nasljeđuje {@link korisni.Kontroler} radi upravljanja konekcijom.
 * 
 * @author Amel Džanić
 * @version 1.2
 */
public class LjubimacCRUD extends korisni.Kontroler {
    /** Dozvoljene vrste ljubimaca u sistemu. */
    public static final String[] tip = {"pas", "mačka"};
    /** Trenutno aktivni ljubimac u kontekstu kontrolera. */
    private Ljubimac ljubimac = new Ljubimac();
    /** Servis za rad sa podacima klijenata. */
    private final klijent.KlijentCRUD kc = new KlijentCRUD();
     /**
     * Inicijalizuje kontroler i osigurava da tabela u bazi postoji.
     * 
     * @throws SQLException Ako dođe do greške pri radu sa bazom podataka.
     */
    public LjubimacCRUD() throws SQLException {
        createTable();
    }

    /**
     * Pomoćna metoda za mapiranje reda iz ResultSet-a u objekat klase Ljubimac.
     * 
     * @param rs ResultSet otvoren na trenutnom redu.
     * @return {@link Ljubimac} objekat sa podacima iz baze.
     * @throws SQLException Ako kolone nisu pronađene.
     */
    private Ljubimac mapirajLjubimca(ResultSet rs) throws SQLException {
        return new Ljubimac(
            rs.getInt("id"),
            rs.getString("ime"),
            rs.getString("vrsta"),
            rs.getString("starost"),
            rs.getString("status")
        );
    }
    /**
     * Ubacuje novog ljubimca u bazu podataka.
     * 
     * @param lj Objekat ljubimca koji se spašava.
     * @throws SQLException Ako SQL upit ne uspije.
     */
    public void dodajLjubimca(Ljubimac lj) throws SQLException {
        String sql = "INSERT INTO ljubimac (ime, vrsta, starost, status) VALUES (?, ?, ?, ?)";
        try (Connection kon = getKone(); PreparedStatement pstmt = kon.prepareStatement(sql)) {
            pstmt.setString(1, lj.getIme());
            pstmt.setString(2, lj.getVrsta());
            pstmt.setString(3, lj.getStarost());
            pstmt.setString(4, lj.getStatus());
            pstmt.executeUpdate();
        }
    }
    /**
     * Pretražuje ljubimce na osnovu imena, vrste i statusa.
     * 
     * @param opcija  Vrsta ljubimca (0: svi, 1: pas, 2: mačka).
     * @param ime     Početna slova imena za pretragu.
     * @param opcija2 Status ljubimca (1: SLOBODAN, 2: REZERVISAN, 3: UDOMLJEN).
     * @return Lista pronađenih ljubimaca.
     * @throws SQLException U slučaju greške u upitu.
     */
    public List<Ljubimac> dobaviSveLjubimce(int opcija, String ime, int opcija2) throws SQLException {
        List<Ljubimac> lista = new ArrayList<>();
        
        // Izgradnja dinamičkog upita
        StringBuilder sql = new StringBuilder("SELECT * FROM ljubimac WHERE ime LIKE ?");
        if (opcija == 1) sql.append(" AND vrsta = 'pas'");
        if (opcija == 2) sql.append(" AND vrsta = 'mačka'");
        
        String statusFilter = switch (opcija2) {
            case 1 -> "SLOBODAN";
            case 2 -> "REZERVISAN";
            case 3 -> "UDOMLJEN";
            default -> null;
        };
        if (statusFilter != null) sql.append(" AND status = ?");

        try (Connection kon = getKone(); PreparedStatement pstmt = kon.prepareStatement(sql.toString())) {
            pstmt.setString(1, ime + "%");
            if (statusFilter != null) pstmt.setString(2, statusFilter);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) lista.add(mapirajLjubimca(rs));
            }
        }
        return lista;
    }
    /**
     * Pronalazi ljubimca po ID-u i učitava njegove povezane klijente.
     * 
     * @param id Identifikator ljubimca.
     * @return Objekat {@link Ljubimac} ili {@code null} ako nije pronađen.
     * @throws SQLException Greška u komunikaciji sa bazom.
     */
    public Ljubimac dobaviLjubimcaPoId(int id) throws SQLException {
        String sql = "SELECT * FROM ljubimac WHERE id = ?";
        Ljubimac lj = null;
        try (Connection kon = getKone(); PreparedStatement pstmt = kon.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) lj = mapirajLjubimca(rs);
            }
        }
        if (lj != null) postaviKorisnikeDatogLjubimca(id);
        return lj;
    }
     /**
     * Ažurira postojeće podatke ljubimca u bazi.
     * 
     * @param lj Ljubimac sa izmijenjenim podacima.
     * @throws SQLException Ako upit nije validan.
     */
    public void azurirajLjubimca(Ljubimac lj) throws SQLException {
        String sql = "UPDATE ljubimac SET ime = ?, vrsta = ?, starost = ?, status = ? WHERE id = ?";
        try (Connection kon = getKone(); PreparedStatement pstmt = kon.prepareStatement(sql)) {
            pstmt.setString(1, lj.getIme());
            pstmt.setString(2, lj.getVrsta());
            pstmt.setString(3, lj.getStarost());
            pstmt.setString(4, lj.getStatus());
            pstmt.setInt(5, lj.getId());
            pstmt.executeUpdate();
        }
    }
    /**
     * Trajno briše ljubimca iz baze podataka na osnovu ID-a.
     * 
     * @param id Identifikator ljubimca za brisanje.
     * @throws SQLException Ako brisanje ne uspije.
     */
    public void obrisiLjubimca(int id) throws SQLException {
        String sql = "DELETE FROM ljubimac WHERE id = ?";
        try (Connection kon = getKone(); PreparedStatement pstmt = kon.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }
    /**
     * Kreira tabelu 'ljubimac' ako ista ne postoji u bazi podataka.
     * Postavlja integritetna ograničenja na kolone 'vrsta' i 'status'.
     * 
     * @throws SQLException U slučaju greške pri definisanju šeme.
     */
    public final void createTable() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS ljubimac (" +
                     "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                     "ime TEXT NOT NULL, " +
                     "vrsta TEXT CHECK(vrsta IN ('pas', 'mačka')) NOT NULL, " +
                     "starost TEXT, " +
                     "status TEXT DEFAULT 'SLOBODAN')"; 
        try (Connection kon = getKone(); Statement st = kon.createStatement()) {
            st.execute(sql);
        }
    }
    /**
     * Dobavlja sve ljubimce koje je određeni klijent rezervisao.
     * 
     * @param idKorisnik Identifikator klijenta.
     * @return Lista ljubimaca sa statusom REZERVISAN za datog klijenta.
     * @throws SQLException Greška u JOIN upitu.
     */
    public List<Ljubimac> dobaviSveLjubimceKorisnika(int idKorisnik) throws SQLException {
        List<Ljubimac> lista = new ArrayList<>();
        String sql = "SELECT lj.* FROM ljubimac lj " +
                     "JOIN udomljavanje u ON lj.id = u.idLjubimac " +
                     "WHERE u.idKlijenti = ? AND u.status = ?";
        
        try (Connection kon = getKone(); PreparedStatement pstmt = kon.prepareStatement(sql)) {
            pstmt.setInt(1, idKorisnik);
            pstmt.setString(2, "REZERVISAN");
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) lista.add(mapirajLjubimca(rs));
            }
        }
        return lista;
    }
    /**
     * Popunjava listu klijenata za određenog ljubimca koristeći veznu tabelu udomljavanja.
     * 
     * @param id ID ljubimca za kojeg se traže klijenti.
     * @throws SQLException Ako dobavljanje ID-eva ili klijenata ne uspije.
     */
    public void postaviKorisnikeDatogLjubimca(int id) throws SQLException {
        UdomljavanjeCRUD uc = new UdomljavanjeCRUD();
        ArrayList<Integer> korisnikIDs = uc.vratiIdKlijenata(id);
        ljubimac.getKorisnici().clear();
        for (Integer kId : korisnikIDs) {
            ljubimac.getKorisnici().add(kc.dobaviKlijentaPoId(kId));
        }
    }
    /**
     * Poništava status udomljavanja i vraća ljubimca u stanje SLOBODAN.
     * 
     * @param lj Objekat ljubimca koji se oslobađa.
     * @throws SQLException U slučaju greške pri ažuriranju statusa.
     */
    public void ukloniUdomljavanje(Ljubimac lj) throws SQLException {
        lj.setStatus("SLOBODAN"); 
        azurirajLjubimca(lj);
    }
    /** @return Vraća trenutnog ljubimca kontrolera. */
    public Ljubimac getLjubimac() { return ljubimac; }
    /** @param ljubimac Postavlja novog ljubimca u kontroler. */
    public void setLjubimac(Ljubimac ljubimac) { this.ljubimac = ljubimac; }
}
