package ljubimac;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import klijent.KlijentCRUD;
import udomljavanje.UdomljavanjeCRUD;

public class LjubimacCRUD extends korisni.Kontroler {
    public static final String[] tip = {"pas", "mačka"};
    private Ljubimac ljubimac = new Ljubimac();
    private final klijent.KlijentCRUD kc = new KlijentCRUD();

    public LjubimacCRUD() throws SQLException {
        createTable();
    }

    // --- DRY: Pomoćna metoda za mapiranje objekta ---
    private Ljubimac mapirajLjubimca(ResultSet rs) throws SQLException {
        return new Ljubimac(
            rs.getInt("id"),
            rs.getString("ime"),
            rs.getString("vrsta"),
            rs.getString("starost"),
            rs.getString("status")
        );
    }

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

    public void obrisiLjubimca(int id) throws SQLException {
        String sql = "DELETE FROM ljubimac WHERE id = ?";
        try (Connection kon = getKone(); PreparedStatement pstmt = kon.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

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

    public void postaviKorisnikeDatogLjubimca(int id) throws SQLException {
        UdomljavanjeCRUD uc = new UdomljavanjeCRUD();
        ArrayList<Integer> korisnikIDs = uc.vratiIdKlijenata(id);
        ljubimac.getKorisnici().clear();
        for (Integer kId : korisnikIDs) {
            ljubimac.getKorisnici().add(kc.dobaviKlijentaPoId(kId));
        }
    }

    public void ukloniUdomljavanje(Ljubimac lj) throws SQLException {
        lj.setStatus("SLOBODAN"); 
        azurirajLjubimca(lj);
    }

    public Ljubimac getLjubimac() { return ljubimac; }
    public void setLjubimac(Ljubimac ljubimac) { this.ljubimac = ljubimac; }
}
