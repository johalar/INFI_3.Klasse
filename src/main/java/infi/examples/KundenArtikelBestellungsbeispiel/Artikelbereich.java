package infi.examples.KundenArtikelBestellungsbeispiel;

import java.sql.*;

public class Artikelbereich {
    public static void createArtikel(Connection c) throws SQLException {
        try (Statement stmt = c.createStatement()) {
            String sql = """
                    CREATE TABLE IF NOT EXISTS ARTIKEL(
                        id INTEGER PRIMARY KEY AUTO_INCREMENT,
                        bezeichnung TEXT,
                        preis DECIMAL(10, 2)
                    )
                    """;
            stmt.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Fehler beim erstellen der Artikel-Tabelle" + e.getMessage(), e);
        }
    }

    public static void insertIntoArtikel(Connection c, String bezeichnung, double preis) throws SQLException {
        String sql = "INSERT INTO ARTIKEL (bezeichnung, preis) VALUES (?, ?)";
        try (PreparedStatement pstmt = c.prepareStatement(sql)) {
            pstmt.setString(1, bezeichnung);
            pstmt.setDouble(2, preis);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Fehler beim einfügen der Artikeldaten" + e.getMessage(), e);
        }
    }

    public static void selectArtikel(Connection c) throws SQLException {
        String sql = "SELECT id, bezeichnung, preis FROM ARTIKEL";
        try (Statement stmt = c.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            boolean found = false;
            while (rs.next()) {
                found = true;
                int artikelID = rs.getInt("id");
                String bezeichnung = rs.getString("Bezeichnung");
                double preis = rs.getDouble("Preis");

                System.out.printf("Artikel-ID: %d | Bezeichnung: %s | Preis: %.2f €%n",
                        artikelID, bezeichnung, preis);
            }
            if (!found) System.err.println("Keine Artikel in der Datenbank gefunden.");
        } catch (SQLException e) {
            throw new RuntimeException("Fehler beim Abrufen der Artikeldaten: " + e.getMessage(), e);
        }
    }
}
