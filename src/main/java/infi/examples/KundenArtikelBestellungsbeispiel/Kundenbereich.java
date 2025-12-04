package infi.examples.KundenArtikelBestellungsbeispiel;

import java.sql.*;

public class Kundenbereich {
    public static void createKunden(Connection c) throws SQLException{
        try (Statement stmt = c.createStatement()){
            String sql = """
                    CREATE TABLE IF NOT EXISTS KUNDEN(
                        id INTEGER PRIMARY KEY AUTO_INCREMENT,
                        name TEXT,
                        email TEXT
                    )
                    """;
            stmt.execute(sql);
        } catch (SQLException e){
            throw new SQLException("Fehler beim erstellen der Kunden-Tabelle",e);
        }
    }

    public static void insertIntoKunden(Connection c, String name, String email) throws SQLException{
        String sql = "INSERT INTO KUNDEN (name, email) VALUES (?, ?)";
        try (PreparedStatement pstmt = c.prepareStatement(sql)){
            pstmt.setString(1, name);
            pstmt.setString(2, email);
            pstmt.executeUpdate();
        } catch (SQLException e){
            throw new SQLException("Fehler beim einfügen der Kundendaten", e);
        }
    }

    public static void selectKunden(Connection c){
        String sql = "SELECT * FROM KUNDEN";
        try (ResultSet rs = c.createStatement().executeQuery(sql)){
            while (rs.next()){
                String name = rs.getString("name");
                String email = rs.getString("email");
                System.out.println("Name: " + name);
                System.out.println("Email:" + email);
            }
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }
}
