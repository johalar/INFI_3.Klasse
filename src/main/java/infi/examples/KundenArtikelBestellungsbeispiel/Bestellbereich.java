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
        c.setAutoCommit(false);
        int aktuellerLagerbestand = -1;
        try {
            String selectSql = "SELECT lagerbestand FROM ARTIKEL WHERE id = ? FOR UPDATE";
            try (PreparedStatement pstmtSelect = c.prepareStatement(selectSql)) {
                pstmtSelect.setInt(1, artikelID);
                try (ResultSet rs = pstmtSelect.executeQuery()) {
                    if (rs.next()) aktuellerLagerbestand = rs.getInt(1);
                    else throw new DataAccessException("Artikel mit ID " + artikelID + " nicht gefunden.", null);
                }
            }

            if (aktuellerLagerbestand < anzahl) {
                c.rollback();
                throw new DataAccessException("Fehler: Nicht genügend Bestand. Nur " + aktuellerLagerbestand + " Stück verfügbar.", null);
            }

            String updateSql = "UPDATE ARTIKEL SET lagerbestand = lagerbestand - ? WHERE id = ?";
            try (PreparedStatement pstmtUpdate = c.prepareStatement(updateSql)) {
                pstmtUpdate.setInt(1, anzahl);
                pstmtUpdate.setInt(2, artikelID);
                pstmtUpdate.executeUpdate();
            }

            String insertSql = "INSERT INTO BESTELLUNGEN (kundenID, artikelID, anzahl) VALUES (?, ?, ?)";
            try (PreparedStatement pstmtInsert = c.prepareStatement(insertSql)) {
                pstmtInsert.setInt(1, kundenID);
                pstmtInsert.setInt(2, artikelID);
                pstmtInsert.setInt(3, anzahl);
                pstmtInsert.executeUpdate();
            }

            c.commit();
        } catch (SQLException e) {
            c.rollback();
            throw new DataAccessException("Fehler beim einfügen der Bestelldaten", e);
        } finally {
            c.setAutoCommit(true);
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
                System.out.printf("Kunden-ID: %d | Artikel-ID: %d | Anzahl: %d%n", kundenID, artikelID, anzahl);
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
                    System.out.printf("Bestell-ID: %d | Kunden-ID: %d | Kunden-Name: %s | Artikel-ID: %d | Artikel-Bezeichnung: %s | Menge: %d%n", id, kundenID, kundenName, artikelID, artikelBezeichnung, menge);
                }
                if (!found) System.err.println("Keine Bestellungen für diese KundenID gefunden.");

            }
        } catch (SQLException e) {
            throw new DataAccessException("Fehler beim Abrufen der Bestellungen des Kunden mit der ID: " + kundenID, e);
        }
    }

    public static void update(Connection c, int neueKundenID, int neueArtikelID, int neueAnzahl, int id) throws SQLException {
        c.setAutoCommit(false);

        try {
            int alteArtikelID = 0;
            int alteAnzahl = 0;
            String sqlSelectOld = "SELECT artikelID, anzahl FROM BESTELLUNGEN WHERE id = ? FOR UPDATE";
            try (PreparedStatement pstmtSelectOld = c.prepareStatement(sqlSelectOld)) {
                pstmtSelectOld.setInt(1, id);
                try (ResultSet rs = pstmtSelectOld.executeQuery()) {
                    if (rs.next()) {
                        alteArtikelID = rs.getInt("artikelID");
                        alteAnzahl = rs.getInt("anzahl");
                    } else throw new DataAccessException("Bestellung mit ID " + id + " nicht gefunden.", null);
                }
            }

            String sqlRevert = "UPDATE ARTIKEL SET lagerbestand = lagerbestand + ? WHERE id = ?";
            try (PreparedStatement pstmtRevert = c.prepareStatement(sqlRevert)) {
                pstmtRevert.setInt(1, alteAnzahl);
                pstmtRevert.setInt(2, alteArtikelID);
                pstmtRevert.executeUpdate();
            }

            String selectSql = "SELECT lagerbestand FROM ARTIKEL WHERE id = ? FOR UPDATE";
            int aktuellerLagerbestand = -1;
            try (PreparedStatement pstmtSelectNew = c.prepareStatement(selectSql)) {
                pstmtSelectNew.setInt(1, neueArtikelID);
                try (ResultSet rs = pstmtSelectNew.executeQuery()) {
                    if (rs.next()) aktuellerLagerbestand = rs.getInt(1);
                    else throw new DataAccessException("Artikel mit ID " + neueArtikelID + " nicht gefunden.", null);
                }
            }

            if (aktuellerLagerbestand < neueAnzahl) {
                c.rollback();
                throw new DataAccessException("Fehler: Nicht genügend Bestand für neue Bestellung. Nur " + aktuellerLagerbestand + " Stück verfügbar.", null);
            }

            String sqlDeduct = "UPDATE ARTIKEL SET lagerbestand = lagerbestand - ? WHERE id = ?";
            try (PreparedStatement pstmtDeduct = c.prepareStatement(sqlDeduct)) {
                pstmtDeduct.setInt(1, neueAnzahl);
                pstmtDeduct.setInt(2, neueArtikelID);
                pstmtDeduct.executeUpdate();
            }

            String sqlUpdate = "UPDATE BESTELLUNGEN SET kundenID = ?, artikelID = ?, anzahl = ? WHERE id = ?";
            try (PreparedStatement pstmtUpdate = c.prepareStatement(sqlUpdate)) {
                pstmtUpdate.setInt(1, neueKundenID);
                pstmtUpdate.setInt(2, neueArtikelID);
                pstmtUpdate.setInt(3, neueAnzahl);
                pstmtUpdate.setInt(4, id);
                pstmtUpdate.executeUpdate();
            }

            c.commit();

        } catch (SQLException e) {
            c.rollback();
            throw new DataAccessException("Fehler beim Aktualisieren der Bestellung oder der Lagerverwaltung", e);
        } finally {
            c.setAutoCommit(true);
        }
    }

    public static void delete(Connection c, int id) throws SQLException {
        try {
            c.setAutoCommit(false);
            int anzahl = 0;
            int artikelID = 0;

            String sqlSelect = "SELECT artikelID, anzahl FROM BESTELLUNGEN WHERE id = ?";
            try (PreparedStatement pstmtSelect = c.prepareStatement(sqlSelect)) {
                pstmtSelect.setInt(1, id);
                try (ResultSet rs = pstmtSelect.executeQuery()) {
                    if (rs.next()) {
                        artikelID = rs.getInt("artikelID");
                        anzahl = rs.getInt("anzahl");
                    } else throw new DataAccessException("Bestellung mit ID " + id + " nicht gefunden.", null);

                }
            }

            String sqlUpdate = "UPDATE ARTIKEL SET lagerbestand = lagerbestand + ? WHERE id = ?";
            try (PreparedStatement pstmtUpdate = c.prepareStatement(sqlUpdate)) {
                pstmtUpdate.setInt(1, anzahl);
                pstmtUpdate.setInt(2, artikelID);
                pstmtUpdate.executeUpdate();
            }

            String sqlDelete = "DELETE FROM BESTELLUNGEN WHERE id = ?";
            try (PreparedStatement pstmt = c.prepareStatement(sqlDelete)) {
                pstmt.setInt(1, id);
                pstmt.executeUpdate();
            }
            c.commit();
        } catch (SQLException e) {
            c.rollback();
            throw new DataAccessException("Fehler beim löschen einer Bestellung", e);
        } finally {
            c.setAutoCommit(true);
        }
    }
}
