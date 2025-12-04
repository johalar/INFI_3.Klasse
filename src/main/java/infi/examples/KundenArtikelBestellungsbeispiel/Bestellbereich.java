package infi.examples.KundenArtikelBestellungsbeispiel;

import java.sql.*;

public class Bestellbereich {
    public static void createBestellungen(Connection c) throws SQLException {
        try (Statement stmt = c.createStatement()){
            String sql = """
                    CREATE TABLE IF NOT EXISTS BESTELLUNGEN(
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
        } catch (SQLException e){
            throw new SQLException("Fehler beim erstellen der Bestellungen-Tabelle", e);
        }
    }

    public static void insertIntoBestellungen(Connection c, int kundenID, int artikelID, int anzahl) throws SQLException{
        String sql = "INSERT INTO BESTELLUNGEN (kundenID, artikelID, anzahl) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = c.prepareStatement(sql)){
            pstmt.setInt(1, kundenID);
            pstmt.setInt(2, artikelID);
            pstmt.setInt(3, anzahl);
            pstmt.executeUpdate();
        } catch (SQLException e){
            throw new SQLException("Fehler beim einfügen der Bestelldaten", e);
        }
    }

    public static void selectBestellungen(Connection c) throws SQLException{
        String sql = "SELECT * FROM BESTELLUNGEN";
        try (ResultSet rs =  c.createStatement().executeQuery(sql)){
            while (rs.next()){
                int kundenID = rs.getInt("kundenID");
                int artikelID = rs.getInt("artikelID");
                int anzahl = rs.getInt("anzahl");
                System.out.println("KundenID: " + kundenID);
                System.out.println("ArtikelID: " + artikelID);
                System.out.println("Anzahl: " + anzahl);
            }
        }
    }
}
