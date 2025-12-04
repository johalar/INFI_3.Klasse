package infi.examples.KundenArtikelBestellungsbeispiel;

import java.sql.*;

public class Artikelbereich {
    public static void createArtikel(Connection c) throws SQLException {
        try (Statement stmt = c.createStatement()){
            String sql = """
                    CREATE TABLE IF NOT EXISTS ARTIKEL(
                        id INTEGER PRIMARY KEY AUTO_INCREMENT,
                        bezeichnung TEXT,
                        preis INTEGER
                    )
                    """;
            stmt.execute(sql);
        } catch (SQLException e){
            throw new SQLException("Fehler beim erstellen der Artikel-Tabelle", e);
        }
    }

    public static void insertIntoArtikel(Connection c, String bezeichnung, int preis) throws SQLException{
        String sql = "INSERT INTO ARTIKEL (bezeichnung, preis) VALUES (?, ?)";
        try (PreparedStatement pstmt = c.prepareStatement(sql)){
            pstmt.setString(1, bezeichnung);
            pstmt.setInt(2, preis);
            pstmt.executeUpdate();
        } catch (SQLException e){
            throw new SQLException("Fehler beim einfügen der Artikeldaten", e);
        }
    }

    public static void selectArtikel(Connection c) throws SQLException{
        String sql = "SELECT * FROM ARTIKEL";
        try (ResultSet rs = c.createStatement().executeQuery(sql)){
            while (rs.next()) {
                String bezeichnung = rs.getString(1);
                int preis = rs.getInt(2);
                System.out.println("Bezeichnung: " + bezeichnung);
                System.out.println("Preis: " + preis);
            }
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }
}
