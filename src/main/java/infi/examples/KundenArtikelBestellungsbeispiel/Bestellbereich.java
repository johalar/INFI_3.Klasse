package infi.examples.KundenArtikelBestellungsbeispiel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;

public class Bestellbereich {
    public static void create(Connection c) throws SQLException {
        try (Statement stmt = c.createStatement()) {
            String sql = """
                    CREATE TABLE IF NOT EXISTS BESTELLUNGEN(
                        id INTEGER PRIMARY KEY AUTO_INCREMENT,
                        kundenID INTEGER NOT NULL,
                        artikelID INTEGER NOT NULL,
                        lagerID INTEGER NOT NULL,
                        anzahl INTEGER NOT NULL,
                        bestellzeitpunkt DATETIME NOT NULL,
                        FOREIGN KEY (kundenID)
                            REFERENCES KUNDEN(id)
                            ON UPDATE CASCADE
                            ON DELETE CASCADE,
                        FOREIGN KEY (artikelID)
                            REFERENCES ARTIKEL(id)
                            ON UPDATE CASCADE
                            ON DELETE CASCADE,
                        FOREIGN KEY (lagerID)
                            REFERENCES LAGERSTANDORT(id)
                            ON UPDATE CASCADE
                            ON DELETE RESTRICT -- Das Löschen eines Standorts sollte fehlschlagen, wenn es noch Bestellungen dafür gibt
                    )
                    """;
            stmt.execute(sql);
        } catch (SQLException e) {
            throw new DataAccessException("Fehler beim Erstellen der Bestellungen-Tabelle", e);
        }
    }

    public static void insertData(Connection c, int kundenID, int artikelID, int anzahl, int lagerID) throws SQLException {
        c.setAutoCommit(false);
        int aktuellerBestand = -1;

        try {
            String selectSql = "SELECT bestand FROM ARTIKELBESTAND WHERE artikelID = ? AND lagerID = ? FOR UPDATE";
            try (PreparedStatement pstmtSelect = c.prepareStatement(selectSql)) {
                pstmtSelect.setInt(1, artikelID);
                pstmtSelect.setInt(2, lagerID);
                try (ResultSet rs = pstmtSelect.executeQuery()) {
                    if (rs.next()) {
                        aktuellerBestand = rs.getInt(1);
                    } else {
                        c.rollback();
                        throw new DataAccessException("Fehler: Bestand für Artikel " + artikelID + " im Lager " + lagerID + " nicht gefunden.", null);
                    }
                }
            }

            if (aktuellerBestand < anzahl) {
                c.rollback();
                throw new DataAccessException("Fehler: Nicht genügend Bestand im Lager " + lagerID + ". Nur " + aktuellerBestand + " Stück verfügbar.", null);
            }

            String updateBestandSql = "UPDATE ARTIKELBESTAND SET bestand = bestand - ? WHERE artikelID = ? AND lagerID = ?";
            try (PreparedStatement pstmtUpdate = c.prepareStatement(updateBestandSql)) {
                pstmtUpdate.setInt(1, anzahl);
                pstmtUpdate.setInt(2, artikelID);
                pstmtUpdate.setInt(3, lagerID);
                pstmtUpdate.executeUpdate();
            }

            LocalDateTime now =  LocalDateTime.now();

            String insertBestellungSql = "INSERT INTO BESTELLUNGEN (kundenID, artikelID, lagerID, anzahl, bestellzeitpunkt) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement pstmtInsert = c.prepareStatement(insertBestellungSql)) {
                pstmtInsert.setInt(1, kundenID);
                pstmtInsert.setInt(2, artikelID);
                pstmtInsert.setInt(3, lagerID);
                pstmtInsert.setInt(4, anzahl);
                pstmtInsert.setObject(5, now);
                pstmtInsert.executeUpdate();
            }

            c.commit();
        } catch (SQLException e) {
            c.rollback();
            throw new DataAccessException("Fehler beim Einfügen der Bestelldaten und Lagerverwaltung", e);
        } finally {
            c.setAutoCommit(true);
        }
    }

    public static void select(Connection c) throws SQLException {
        String sql = """
            SELECT
                B.id AS BestellID,
                K.name AS Kundenname,
                A.bezeichnung AS Artikelbezeichnung,
                LS.name AS Lagername,
                B.anzahl,
                B.bestellzeitpunkt -- NEU: Spalte auswählen
            FROM
                BESTELLUNGEN B
            JOIN
                KUNDEN K ON B.kundenID = K.id
            JOIN
                ARTIKEL A ON B.artikelID = A.id
            JOIN
                LAGERSTANDORT LS ON B.lagerID = LS.id
            ORDER BY B.id
            """;
        try (Statement stmt = c.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            boolean found = false;
            while (rs.next()) {
                found = true;
                int id = rs.getInt("BestellID");
                String kundenName = rs.getString("Kundenname");
                String artikelBezeichnung = rs.getString("Artikelbezeichnung");
                String lagerName = rs.getString("Lagername");
                int menge = rs.getInt("anzahl");

                LocalDateTime bestellZeitpunkt = rs.getObject("bestellzeitpunkt", LocalDateTime.class);

                System.out.printf("ID: %d | Kunde: %s | Artikel: %s | Lager: %s | Menge: %d | Zeitpunkt: %s%n",
                        id, kundenName, artikelBezeichnung, lagerName, menge, bestellZeitpunkt.toLocalTime());
            }
            if (!found) System.err.println("Keine Bestellungen in der Datenbank gefunden.");
        } catch (SQLException e) {
            throw new DataAccessException("Fehler beim Abrufen aller Bestellungen", e);
        }
    }

    public static void update(Connection c, int neueKundenID, int neueArtikelID, int neueAnzahl, int neueLagerID, int bestellID) throws SQLException {
        c.setAutoCommit(false);
        try {
            int alteArtikelID = 0;
            int alteAnzahl = 0;
            int alteLagerID = 0;

            String sqlSelectOld = "SELECT artikelID, anzahl, lagerID FROM BESTELLUNGEN WHERE id = ? FOR UPDATE";
            try (PreparedStatement pstmtSelectOld = c.prepareStatement(sqlSelectOld)) {
                pstmtSelectOld.setInt(1, bestellID);
                try (ResultSet rs = pstmtSelectOld.executeQuery()) {
                    if (rs.next()) {
                        alteArtikelID = rs.getInt("artikelID");
                        alteAnzahl = rs.getInt("anzahl");
                        alteLagerID = rs.getInt("lagerID");
                    } else {
                        c.rollback();
                        throw new DataAccessException("Bestellung mit ID " + bestellID + " nicht gefunden.", null);
                    }
                }
            }

            String sqlRevert = "UPDATE ARTIKELBESTAND SET bestand = bestand + ? WHERE artikelID = ? AND lagerID = ?";
            try (PreparedStatement pstmtRevert = c.prepareStatement(sqlRevert)) {
                pstmtRevert.setInt(1, alteAnzahl);
                pstmtRevert.setInt(2, alteArtikelID);
                pstmtRevert.setInt(3, alteLagerID);
                pstmtRevert.executeUpdate();
            }

            String selectSqlNew = "SELECT bestand FROM ARTIKELBESTAND WHERE artikelID = ? AND lagerID = ? FOR UPDATE";
            int aktuellerBestandNeu = -1;
            try (PreparedStatement pstmtSelectNew = c.prepareStatement(selectSqlNew)) {
                pstmtSelectNew.setInt(1, neueArtikelID);
                pstmtSelectNew.setInt(2, neueLagerID);
                try (ResultSet rs = pstmtSelectNew.executeQuery()) {
                    if (rs.next()) aktuellerBestandNeu = rs.getInt(1);
                    else
                        throw new DataAccessException("Ziel-Bestand (Artikel " + neueArtikelID + ", Lager " + neueLagerID + ") nicht gefunden.", null);
                }
            }

            if (aktuellerBestandNeu < neueAnzahl) {
                c.rollback();
                throw new DataAccessException("Fehler: Nicht genügend Bestand für neue Bestellung. Nur " + aktuellerBestandNeu + " Stück verfügbar.", null);
            }

            String sqlDeduct = "UPDATE ARTIKELBESTAND SET bestand = bestand - ? WHERE artikelID = ? AND lagerID = ?";
            try (PreparedStatement pstmtDeduct = c.prepareStatement(sqlDeduct)) {
                pstmtDeduct.setInt(1, neueAnzahl);
                pstmtDeduct.setInt(2, neueArtikelID);
                pstmtDeduct.setInt(3, neueLagerID);
                pstmtDeduct.executeUpdate();
            }

            String sqlUpdate = "UPDATE BESTELLUNGEN SET kundenID = ?, artikelID = ?, anzahl = ?, lagerID = ? WHERE id = ?";
            try (PreparedStatement pstmtUpdate = c.prepareStatement(sqlUpdate)) {
                pstmtUpdate.setInt(1, neueKundenID);
                pstmtUpdate.setInt(2, neueArtikelID);
                pstmtUpdate.setInt(3, neueAnzahl);
                pstmtUpdate.setInt(4, neueLagerID);
                pstmtUpdate.setInt(5, bestellID);
                pstmtUpdate.executeUpdate();
            }

            c.commit();
        } catch (SQLException e) {
            c.rollback();
            throw new DataAccessException("Fehler beim Aktualisieren der Bestellung und der Lagerverwaltung", e);
        } finally {
            c.setAutoCommit(true);
        }
    }

    public static void delete(Connection c, int id) throws SQLException {
        try {
            c.setAutoCommit(false);
            int anzahl = 0;
            int artikelID = 0;
            int lagerID = 0;

            String sqlSelect = "SELECT artikelID, anzahl, lagerID FROM BESTELLUNGEN WHERE id = ?";
            try (PreparedStatement pstmtSelect = c.prepareStatement(sqlSelect)) {
                pstmtSelect.setInt(1, id);
                try (ResultSet rs = pstmtSelect.executeQuery()) {
                    if (rs.next()) {
                        artikelID = rs.getInt("artikelID");
                        anzahl = rs.getInt("anzahl");
                        lagerID = rs.getInt("lagerID");
                    } else {
                        c.rollback();
                        throw new DataAccessException("Bestellung mit ID " + id + " nicht gefunden.", null);
                    }
                }
            }

            String sqlUpdate = "UPDATE ARTIKELBESTAND SET bestand = bestand + ? WHERE artikelID = ? AND lagerID = ?";
            try (PreparedStatement pstmtUpdate = c.prepareStatement(sqlUpdate)) {
                pstmtUpdate.setInt(1, anzahl);
                pstmtUpdate.setInt(2, artikelID);
                pstmtUpdate.setInt(3, lagerID);
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
            throw new DataAccessException("Fehler beim Löschen einer Bestellung und Rückbuchen des Bestands", e);
        } finally {
            c.setAutoCommit(true);
        }
    }

    public static void selectBestellungenVonKunden(Connection c, int kundenID) throws SQLException {
        String sql = """
                SELECT
                    B.id AS BestellID,
                    A.bezeichnung AS Artikelbezeichnung,
                    LS.name AS Lagername,
                    B.anzahl
                FROM
                    BESTELLUNGEN B
                JOIN
                    ARTIKEL A ON B.artikelID = A.id
                JOIN
                    LAGERSTANDORT LS ON B.lagerID = LS.id
                WHERE
                    B.kundenID = ?
                ORDER BY B.id
                """;

        try (PreparedStatement pstmt = c.prepareStatement(sql)) {
            pstmt.setInt(1, kundenID);

            try (ResultSet rs = pstmt.executeQuery()) {
                System.out.printf("\n====== BESTELLUNGEN VON KUNDE ID %d ======%n", kundenID);
                boolean found = false;

                while (rs.next()) {
                    found = true;
                    int id = rs.getInt("BestellID");
                    String artikelBezeichnung = rs.getString("Artikelbezeichnung");
                    String lagerName = rs.getString("Lagername");
                    int menge = rs.getInt("anzahl");

                    System.out.printf("Bestell-ID: %d | Artikel: %s | Lager entnommen: %s | Menge: %d%n",
                            id, artikelBezeichnung, lagerName, menge);
                }
                if (!found) System.err.printf("Keine Bestellungen für Kunde ID %d gefunden.%n", kundenID);
            }
        } catch (SQLException e) {
            throw new DataAccessException("Fehler beim Abrufen der Kundenbestellungen", e);
        }
    }

    public static void selectTop10KundenByBestellwert(Connection c) throws SQLException {
        String sql = """
                SELECT
                    K.id AS KundenID,
                    K.name AS Kundenname,
                    SUM(B.anzahl * A.preis) AS GesamtBestellwert
                FROM
                    BESTELLUNGEN B
                JOIN
                    KUNDEN K ON B.kundenID = K.id
                JOIN
                    ARTIKEL A ON B.artikelID = A.id
                GROUP BY
                    K.id, K.name
                ORDER BY
                    GesamtBestellwert DESC
                LIMIT 10
                """;

        try (Statement stmt = c.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("\n====== TOP 10 KUNDEN NACH GESAMTBESTELLWERT ======");
            System.out.println("--------------------------------------------------");
            boolean found = false;
            int rank = 1;

            while (rs.next()) {
                found = true;
                int kundenID = rs.getInt("KundenID");
                String kundenName = rs.getString("Kundenname");
                double gesamtwert = rs.getDouble("GesamtBestellwert");

                System.out.printf("#%d | ID: %d | Kunde: %s | Gesamtwert: %.2f €%n",
                        rank++, kundenID, kundenName, gesamtwert);
            }
            if (!found) System.err.println("Keine Bestellungen in der Datenbank gefunden, um Top-Kunden zu ermitteln.");

        } catch (SQLException e) {
            throw new DataAccessException("Fehler beim Abrufen der Top 10 Kunden nach Bestellwert", e);
        }
    }
}