package infi.examples.KundenArtikelBestellungsbeispiel;

import java.sql.*;

public class Artikelbereich {

    public static void create(Connection c) throws SQLException {
        try (Statement stmt = c.createStatement()) {
            String sql = """
                    CREATE TABLE IF NOT EXISTS ARTIKEL(
                        id INTEGER PRIMARY KEY AUTO_INCREMENT,
                        bezeichnung TEXT,
                        preis DECIMAL(10, 2)
                        -- Die Spalte 'lagerbestand' wurde entfernt und in die Tabelle ARTIKELBESTAND ausgelagert
                    )
                    """;
            stmt.execute(sql);
        } catch (SQLException e) {
            throw new DataAccessException("Fehler beim erstellen der Artikel-Tabelle", e);
        }
    }

    public static void insertData(Connection c, String bezeichnung, double preis) throws SQLException {
        String sql = "INSERT INTO ARTIKEL (bezeichnung, preis) VALUES (?, ?)";
        try (PreparedStatement pstmt = c.prepareStatement(sql)) {
            pstmt.setString(1, bezeichnung);
            pstmt.setDouble(2, preis);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Fehler beim einfügen der Artikeldaten", e);
        }
    }

    public static void select(Connection c) throws SQLException {
        String sql = "SELECT id, bezeichnung, preis FROM ARTIKEL";
        try (Statement stmt = c.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            System.out.println("\n====== ARTIKELDATEN ======");
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
            throw new DataAccessException("Fehler beim Abrufen der Artikeldaten", e);
        }
    }

    public static void update(Connection c, String bezeichnung, double preis, int id) throws SQLException {
        String sql = "UPDATE ARTIKEL SET bezeichnung = ?, preis = ? WHERE id = ?";
        try (PreparedStatement pstmt = c.prepareStatement(sql)) {
            pstmt.setString(1, bezeichnung);
            pstmt.setDouble(2, preis);
            pstmt.setInt(3, id);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected == 0) {
                System.err.println("Artikel mit ID " + id + " nicht gefunden.");
            } else {
                System.out.println("Artikel erfolgreich aktualisiert.");
            }
        } catch (SQLException e) {
            throw new DataAccessException("Fehler beim Daten aktualisieren der Artikeldaten", e);
        }
    }

    public static void delete(Connection c, int artikelID) throws SQLException {
        try  (PreparedStatement pstmt = c.prepareStatement("DELETE FROM ARTIKEL WHERE id = ?")) {
            pstmt.setInt(1, artikelID);
            pstmt.executeUpdate();
            System.out.println("Artikel ID " + artikelID + " erfolgreich gelöscht.");
        } catch (SQLException e) {
            throw new DataAccessException("Fehler beim löschen eines Artikel",e);
        }
    }
}