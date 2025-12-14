package infi.examples.KundenArtikelBestellungsbeispiel;

import java.sql.*;

public class Bestellbereich {
    public static void create(Connection c) throws SQLException {
        try (Statement stmt = c.createStatement()) {
            String sql = """
                    CREATE TABLE IF NOT EXISTS BESTELLUNGEN(
                        id INTEGER PRIMARY KEY AUTO_INCREMENT,
                        kundenID INTEGER NOT NULL,
                        artikelID INTEGER NOT NULL,
                        anzahl INTEGER NOT NULL,
                        FOREIGN KEY (kundenID)
                            REFERENCES KUNDEN(id)
                            ON UPDATE CASCADE
                            ON DELETE CASCADE,
                        FOREIGN KEY (artikelID)
                            REFERENCES ARTIKEL(id)
                            ON UPDATE CASCADE
                            ON DELETE CASCADE
                    )
                    """;
            stmt.execute(sql);
        } catch (SQLException e) {
            throw new DataAccessException("Fehler beim erstellen der Bestellungen-Tabelle", e);
        }
    }

    public static void insertData(Connection c, int kundenID, int artikelID, int anzahl) throws SQLException {
        String sql = "INSERT INTO BESTELLUNGEN (kundenID, artikelID, anzahl) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = c.prepareStatement(sql)) {
            pstmt.setInt(1, kundenID);
            pstmt.setInt(2, artikelID);
            pstmt.setInt(3, anzahl);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Fehler beim einfügen der Bestelldaten", e);
        }
    }

    public static void select(Connection c) throws SQLException {
        String sql = "SELECT * FROM BESTELLUNGEN";
        try (Statement stmt = c.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            boolean found = false;
            while (rs.next()) {
                found = true;
                int kundenID = rs.getInt("kundenID");
                int artikelID = rs.getInt("artikelID");
                int anzahl = rs.getInt("anzahl");
                System.out.printf("Kunden-ID: %d | Artikel-ID: %d | Anzahl: %d%n",
                        kundenID, artikelID, anzahl);
            }
            if (!found) System.err.println("Keine Bestellungen in der Datenbank gefunden.");
        } catch (SQLException e) {
            throw new DataAccessException("Fehler beim Abrufen der Bestelldaten", e);
        }
    }

    public static void selectBestellungenVonKunden(Connection c, int kundenID) throws SQLException {
        String sql = """
                    SELECT B.id, B.kundenID, K.name AS Kundenname, B.artikelId,
                        A.bezeichnung AS Artikelbezeichnung, B.anzahl
                    FROM BESTELLUNGEN B
                    JOIN ARTIKEL A ON B.artikelId = A.id
                    JOIN KUNDEN K ON B.kundenID = K.id
                    WHERE B.kundenID = ?;
                """;
        try (PreparedStatement pstmt = c.prepareStatement(sql)) {
            pstmt.setInt(1, kundenID);
            try (ResultSet rs = pstmt.executeQuery()) {
                boolean found = false;
                while (rs.next()) {
                    found = true;
                    int id = rs.getInt("id");
                    String kundenName = rs.getString("Kundenname");
                    int artikelID = rs.getInt("artikelID");
                    String artikelBezeichnung = rs.getString("Artikelbezeichnung");
                    int menge = rs.getInt("anzahl");
                    System.out.printf("Bestell-ID: %d | Kunden-ID: %d | Kunden-Name: %s | Artikel-ID: %d | Artikel-Bezeichnung: %s | Menge: %d%n",
                            id, kundenID, kundenName, artikelID, artikelBezeichnung, menge);
                }
                if (!found) System.err.println("Keine Bestellungen für diese KundenID gefunden.");

            }
        } catch (SQLException e) {
            throw new DataAccessException("Fehler beim Abrufen der Bestellungen des Kunden mit der ID: " + kundenID, e);
        }
    }

    public static void update(Connection c, int kundenID, int artikelID, int anzahl, int id) throws SQLException {
        String sql = "UPDATE FROM KUNDEN SET name = ?, email = ? WHERE id = ?";
        try (PreparedStatement pstmt = c.prepareStatement(sql)) {
            pstmt.setInt(1, kundenID);
            pstmt.setInt(2, artikelID);
            pstmt.setInt(3, anzahl);
            pstmt.setInt(4, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Fehler beim aktualisieren der Bestellungsdaten", e);
        }
    }

    public static void delete(Connection c, int id) throws SQLException {
        try (PreparedStatement pstmt = c.prepareStatement("DELETE FROM BESTELLUNGEN WHERE id = ?")) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Fehler beim löschen einer Bestellung", e);
        }
    }
}
