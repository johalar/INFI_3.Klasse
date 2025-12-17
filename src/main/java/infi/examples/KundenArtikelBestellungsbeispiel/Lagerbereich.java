package infi.examples.KundenArtikelBestellungsbeispiel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Lagerbereich {
    public static void create(Connection c) throws SQLException {
        String sqlLagerstandort = """
            CREATE TABLE IF NOT EXISTS LAGERSTANDORT(
                id INTEGER PRIMARY KEY AUTO_INCREMENT,
                name TEXT NOT NULL
            )
            """;
        String sqlArtikelBestand = """
            CREATE TABLE IF NOT EXISTS ARTIKELBESTAND(
                artikelID INTEGER NOT NULL,
                lagerID INTEGER NOT NULL,
                bestand INTEGER NOT NULL DEFAULT 0,
                PRIMARY KEY (artikelID, lagerID),
                FOREIGN KEY (artikelID) REFERENCES ARTIKEL(id) ON UPDATE CASCADE ON DELETE CASCADE,
                FOREIGN KEY (lagerID) REFERENCES LAGERSTANDORT(id) ON UPDATE CASCADE ON DELETE CASCADE
            )
            """;
        try (Statement stmt = c.createStatement()){
            stmt.execute(sqlLagerstandort);
            stmt.execute(sqlArtikelBestand);
        } catch(SQLException e) {
            throw new DataAccessException("Fehler beim Erstellen der Lager-Tabellen", e);
        }
    }

    public static void insertStandort(Connection c, String name) throws SQLException {
        String sql = "INSERT INTO LAGERSTANDORT (name) VALUES (?)";
        try (PreparedStatement pstmt = c.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Fehler beim Einfügen eines neuen Lagerstandorts", e);
        }
    }

    public static void selectStandorte (Connection c) throws SQLException {
        String sql = "SELECT id, name FROM LAGERSTANDORT ORDER BY id";
        try (Statement stmt = c.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            boolean found = false;
            while (rs.next()) {
                found = true;
                int id = rs.getInt("id");
                String name = rs.getString("name");
                System.out.printf("Lager-ID: %d | Name: %s%n", id, name);
            }
            if (!found) System.err.println("Keine Lagerstandorte in der Datenbank gefunden.");
        } catch (SQLException e) {
            throw new DataAccessException("Fehler beim Abrufen der Lagerstandorte", e);
        }
    }

    public static void updateStandort(Connection c, int id, String neuerName) throws SQLException {
        String sql = "UPDATE LAGERSTANDORT SET name = ? WHERE id = ?";
        try (PreparedStatement pstmt = c.prepareStatement(sql)) {
            pstmt.setString(1, neuerName);
            pstmt.setInt(2, id);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected == 0) {
                System.err.println("Lagerstandort mit ID " + id + " nicht gefunden.");
            } else {
                System.out.println("Lagerstandort erfolgreich aktualisiert.");
            }
        } catch (SQLException e) {
            throw new DataAccessException("Fehler beim Aktualisieren des Lagerstandorts", e);
        }
    }

    public static void deleteStandort(Connection c, int id) throws SQLException {
        String sql = "DELETE FROM LAGERSTANDORT WHERE id = ?";
        try (PreparedStatement pstmt = c.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected == 0) {
                System.err.println("Lagerstandort mit ID " + id + " nicht gefunden.");
            } else {
                System.out.println("Lagerstandort erfolgreich gelöscht. Bestand für diesen Standort wurde ebenfalls entfernt.");
            }
        } catch (SQLException e) {
            throw new DataAccessException("Fehler beim Löschen des Lagerstandorts", e);
        }
    }

    public static void insertArtikelBestand(Connection c, int artikelID, int lagerID, int bestand) throws SQLException{
        String upsertSql = """
            INSERT INTO ARTIKELBESTAND (artikelID, lagerID, bestand)
            VALUES (?, ?, ?)
            ON DUPLICATE KEY UPDATE
                bestand = VALUES(bestand)
            """;
        try (PreparedStatement pstmt = c.prepareStatement(upsertSql)) {
            pstmt.setInt(1, artikelID);
            pstmt.setInt(2, lagerID);
            pstmt.setInt(3, bestand);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Fehler beim Einfügen/Aktualisieren des Artikelbestands", e);
        }
    }

    public static void selectKritischenBestand(Connection c) throws SQLException {
        String sql = """
                SELECT
                    A.bezeichnung AS Artikelname,
                    SUM(AB.bestand) AS Gesamtbestand
                FROM
                    ARTIKEL A
                JOIN
                    ARTIKELBESTAND AB ON A.id = AB.artikelID
                GROUP BY
                    A.id, A.bezeichnung
                HAVING
                    SUM(AB.bestand) < 5  -- Schwellenwert (z.B. < 5)
                ORDER BY
                    Gesamtbestand ASC
                """;
        try (ResultSet rs = c.createStatement().executeQuery(sql)) {
            System.out.println("\n====== ARTIKEL MIT KRITISCHEM GESAMTBESTAND (< 5) ======");
            boolean found = false;
            while (rs.next()) {
                found = true;
                String artikelname = rs.getString("Artikelname");
                int gesamtbestand = rs.getInt("Gesamtbestand");

                System.out.printf("Artikelname: %s |  Gesamtbestand: %d%n", artikelname, gesamtbestand);
            }
            if (!found) System.out.println("Es wurden keine Artikel mit kritischem Gesamtbestand gefunden.");
        } catch (SQLException e) {
            throw new DataAccessException("Fehler beim Abrufen der Artikel mit kritischen Lagerbestand", e);
        }
    }
}