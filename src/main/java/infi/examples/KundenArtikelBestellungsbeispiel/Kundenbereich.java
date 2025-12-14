package infi.examples.KundenArtikelBestellungsbeispiel;

import java.sql.*;

public class Kundenbereich {
    public static void create(Connection c) throws SQLException {
        try (Statement stmt = c.createStatement()) {
            String sql = """
                    CREATE TABLE IF NOT EXISTS KUNDEN(
                        id INTEGER PRIMARY KEY AUTO_INCREMENT,
                        name TEXT,
                        email TEXT
                    )
                    """;
            stmt.execute(sql);
        } catch (SQLException e) {
            throw new DataAccessException("Fehler beim erstellen der Kunden-Tabelle", e);
        }
    }

    public static void insertData(Connection c, String name, String email) throws SQLException {
        String sql = "INSERT INTO KUNDEN (name, email) VALUES (?, ?)";
        try (PreparedStatement pstmt = c.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, email);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Fehler beim einfügen der Kundendaten", e);
        }
    }

    public static void select(Connection c) throws SQLException {
        String sql = "SELECT id, name, email FROM KUNDEN";
        try (Statement stmt = c.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            boolean found = false;
            while (rs.next()) {
                found = true;
                int kundenID = rs.getInt("id");
                String name = rs.getString("name");
                String email = rs.getString("email");
                System.out.printf("Kunden-ID: %d | Name: %s | Email: %s%n",
                        kundenID, name, email);
            }
            if (!found) System.err.println("Keine Kunden in der Datenbank gefunden.");

        } catch (SQLException e) {
            throw new DataAccessException("Fehler beim Abrufen der Kundendaten", e);
        }
    }

    public static void update(Connection c, String name, String email, int id) throws SQLException {
        String sql = "UPDATE FROM KUNDEN SET name = ?, email = ? WHERE id = ?";
        try (PreparedStatement pstmt = c.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, email);
            pstmt.setInt(3, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Fehler beim aktualisieren der Kundendaten", e);
        }
    }

    public static void delete(Connection c, int id) throws SQLException {
        try (PreparedStatement pstmt = c.prepareStatement("DELETE FROM KUNDEN WHERE id = ?")) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Fehler beim löschen eines Kunden", e);
        }
    }
}
