package infi.examples.KundenArtikelBestellungsbeispiel;

import java.sql.*;

public class Artikelbereich {
    public static void create(Connection c) throws SQLException {
        try (Statement stmt = c.createStatement()) {
            String sql = """
                    CREATE TABLE IF NOT EXISTS ARTIKEL(
                        id INTEGER PRIMARY KEY AUTO_INCREMENT,
                        bezeichnung TEXT,
                        preis DECIMAL(10, 2),
                        lagerbestand INTEGER DEFAULT 0
                    )
                    """;
            stmt.execute(sql);
        } catch (SQLException e) {
            throw new DataAccessException("Fehler beim erstellen der Artikel-Tabelle", e);
        }
    }

    public static void insertData(Connection c, String bezeichnung, double preis, int lagerbestand) throws SQLException {
        String sql = "INSERT INTO ARTIKEL (bezeichnung, preis, lagerbestand) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = c.prepareStatement(sql)) {
            pstmt.setString(1, bezeichnung);
            pstmt.setDouble(2, preis);
            pstmt.setInt(3, lagerbestand);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Fehler beim einfügen der Artikeldaten", e);
        }
    }

    public static void select(Connection c) throws SQLException {
        String sql = "SELECT id, bezeichnung, preis FROM ARTIKEL";
        try (Statement stmt = c.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            boolean found = false;
            while (rs.next()) {
                found = true;
                int artikelID = rs.getInt("id");
                String bezeichnung = rs.getString("Bezeichnung");
                double preis = rs.getDouble("Preis");
                int lagerbestand = rs.getInt("lagerbestand");

                System.out.printf("Artikel-ID: %d | Bezeichnung: %s | Preis: %.2f € | Lagerbestand %d%n",
                        artikelID, bezeichnung, preis, lagerbestand);
            }
            if (!found) System.err.println("Keine Artikel in der Datenbank gefunden.");
        } catch (SQLException e) {
            throw new DataAccessException("Fehler beim Abrufen der Artikeldaten", e);
        }
    }

    public static void update(Connection c, String bezeichnung, double preis, int lagerbestand, int id) throws SQLException {
        String sql = "UPDATE ARTIKEL SET bezeichnung = ?, preis = ?, lagerbestand = ? WHERE id = ?";
        try (PreparedStatement pstmt = c.prepareStatement(sql)) {
            pstmt.setString(1, bezeichnung);
            pstmt.setDouble(2, preis);
            pstmt.setInt(3, lagerbestand);
            pstmt.setInt(4, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Fehler beim Daten aktualisieren der Artikeldaten", e);
        }
    }

    public static void delete(Connection c, int artikelID) throws SQLException {
        try (PreparedStatement pstmt = c.prepareStatement("DELETE FROM ARTIKEL WHERE id = ?")) {
            pstmt.setInt(1, artikelID);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Fehler beim löschen eines Artikel", e);
        }
    }

    public static void selectKritischenBestand(Connection c) throws SQLException {
        String sql = """
                SELECT
                    bezeichnung,
                    lagerbestand
                FROM
                    ARTIKEL
                WHERE
                    lagerbestand < 5
                ORDER BY
                    lagerbestand ASC;
                """;
        try (ResultSet rs = c.createStatement().executeQuery(sql)) {
            boolean found = false;
            while (rs.next()) {
                found = true;
                String bezeichnung = rs.getString("bezeichnung");
                int lagerbestand = rs.getInt("lagerbestand");

                System.out.printf("Bezeichnung: %s |  Lagerbestand: %d%n", bezeichnung, lagerbestand);
            }
            if (!found) System.err.println("Keine Artikel in der Datenbank gefunden.");
        } catch (SQLException e) {
            throw new DataAccessException("Fehler beim Abrufen der Artikel mit kritischen Lagerbestand", e);
        }
    }
}
