package klijent;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import ljubimac.LjubimacCRUD;
import udomljavanje.UdomljavanjeCRUD;

public class KlijentCRUD extends korisni.Kontroler {
    private Klijent klijent;

    public KlijentCRUD() throws SQLException {
        createTable();
    }

    // --- POMOĆNA METODA ZA DRY KONCEPT ---
    private Klijent mapirajKlijenta(ResultSet rs) throws SQLException {
        return new Klijent(
            rs.getInt("id"),
            rs.getString("ime_prezime"),
            rs.getString("adresa"),
            rs.getString("broj_telefona")
        );
    }

    public void dodajKlijenta(Klijent k) throws SQLException {
        String sql = "INSERT INTO klijent (ime_prezime, adresa, broj_telefona) VALUES (?, ?, ?)";
        try (Connection kon = getKone(); PreparedStatement pstmt = kon.prepareStatement(sql)) {
            pstmt.setString(1, k.getImePrezime());
            pstmt.setString(2, k.getAdresa());
            pstmt.setString(3, k.getBrojTelefona());
            pstmt.executeUpdate();
        }
    }

    public List<Klijent> dobaviSveKlijente() {
        return dobaviKlijentePoUpitu("SELECT * FROM klijent", null);
    }

    public List<Klijent> dobaviSveKlijenteUvijet(String imePrezime) {
        return dobaviKlijentePoUpitu("SELECT * FROM klijent WHERE ime_prezime LIKE ?", imePrezime + "%");
    }

    // Dodatna DRY metoda za liste
    private List<Klijent> dobaviKlijentePoUpitu(String sql, String parametar) {
        List<Klijent> lista = new ArrayList<>();
        try (Connection kon = getKone(); PreparedStatement pstmt = kon.prepareStatement(sql)) {
            if (parametar != null) pstmt.setString(1, parametar);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) lista.add(mapirajKlijenta(rs));
            }
        } catch (SQLException e) {
            System.err.println("Greška kod dohvata listi: " + e.getMessage());
        }
        return lista;
    }

    public Klijent dobaviKlijentaPoId(int id) {
        String sql = "SELECT * FROM klijent WHERE id = ?";
        try (Connection kon = getKone(); PreparedStatement pstmt = kon.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return mapirajKlijenta(rs);
            }
        } catch (SQLException e) {
            System.err.println("Greška kod dohvata po ID-u: " + e.getMessage());
        }
        return null;
    }

    public Klijent dobaviKlijentaVlasnika(int idLjubimca) {
        String sql = "SELECT k.* FROM klijent k JOIN udomljavanje u ON k.id = u.idKlijenti WHERE u.idLjubimac = ?";
        try (Connection kon = getKone(); PreparedStatement pstmt = kon.prepareStatement(sql)) {
            pstmt.setInt(1, idLjubimca);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return mapirajKlijenta(rs);
            }
        } catch (SQLException e) {
            System.err.println("Greška kod dohvata vlasnika: " + e.getMessage());
        }
        return null;
    }

    public void azurirajKlijenta(Klijent k) throws SQLException {
        String sql = "UPDATE klijent SET ime_prezime = ?, adresa = ?, broj_telefona = ? WHERE id = ?";
        try (Connection kon = getKone(); PreparedStatement pstmt = kon.prepareStatement(sql)) {
            pstmt.setString(1, k.getImePrezime());
            pstmt.setString(2, k.getAdresa());
            pstmt.setString(3, k.getBrojTelefona());
            pstmt.setInt(4, k.getId());
            pstmt.executeUpdate();
        }
    }

    public void obrisiKlijenta(int id) throws SQLException {
        String sql = "DELETE FROM klijent WHERE id = ?";
        try (Connection kon = getKone(); PreparedStatement pstmt = kon.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    public final void createTable() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS klijent (" +
                     "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                     "ime_prezime TEXT NOT NULL UNIQUE, " +
                     "adresa TEXT, " +
                     "broj_telefona TEXT)";
        try (Connection kon = getKone(); Statement st = kon.createStatement()) {
            st.execute(sql);
        }
    }

    public void postaviLjubimceDatogKorisnika(int id) throws SQLException {
        UdomljavanjeCRUD uc = new UdomljavanjeCRUD();
        LjubimacCRUD lc = new LjubimacCRUD();
        if (klijent != null) {
            klijent.getLjubimci().clear();
            for (Integer ljubimacID : uc.vratiIdLjubimaca(id)) {
                klijent.getLjubimci().add(lc.dobaviLjubimcaPoId(ljubimacID));
            }
        }
    }

    // Getteri i Setteri
    public void setKlijent(Klijent klijent) { this.klijent = klijent; }
    public void setKlijent(Object klijent) { this.klijent = (Klijent) klijent; }
    public Klijent getKlijent() { return klijent; }
}
