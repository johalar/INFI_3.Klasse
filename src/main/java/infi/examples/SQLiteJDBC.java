package infi.examples;

import java.sql.*;

public class SQLiteJDBC {

    private static final String DB_URL = "jdbc:sqlite:test.db";
    private static final String TABLE_NAME = "COMPANY";

    private static void validateIdentifier(String identifier) {
        if (identifier == null || !identifier.matches("[a-zA-Z_][a-zA-Z0-9_]*")) {
            throw new IllegalArgumentException("Ungültiger Tabellenname: " + identifier);
        }
    }

    public static void main(String[] args) {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.err.println("SQLite JDBC Treiber nicht gefunden: " + e.getMessage());
            return; // Programm beenden, wenn Treiber fehlt
        }

        try (Connection c = DriverManager.getConnection(DB_URL)) {
            System.out.println("Datenbank erfolgreich geöffnet");

            createTable(c);
            System.out.println("Tabelle erstellt");

            insertData(c);
            System.out.println("Daten eingefügt");

            insertNewData("7", "Finn", "16", "Koessen", "2400.00", c);
            System.out.println("Neue Daten eingefügt");

//            selectLike(TABLE_NAME, "age", c);
            update(TABLE_NAME, c);
            delete(TABLE_NAME, c);
//            selectAnd(TABLE_NAME, "age", c);

//            dropTable(TABLE_NAME, c);
//            System.out.println("Tabelle gelöscht");

        } catch (SQLException e) {
            System.err.println("Datenbankfehler: " + e.getMessage());
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            System.err.println("Ungültige Parameter: " + e.getMessage());
        }
    }

    public static void createTable(Connection c) throws SQLException {
        try (Statement stmt = c.createStatement()) {
            String sql = "CREATE TABLE IF NOT EXISTS COMPANY " + "(ID INT PRIMARY KEY NOT NULL,"
                    + " NAME TEXT NOT NULL, " + " AGE INT NOT NULL, "
                    + " ADDRESS CHAR(50), " + " SALARY REAL)";
            stmt.executeUpdate(sql);
        }
    }

    public static void dropTable(String name, Connection c) throws SQLException {
        validateIdentifier(name);

        try (Statement stmt = c.createStatement()) {
            String sql = "DROP TABLE " + name;
            stmt.executeUpdate(sql);
        }
    }

    public static void insertData(Connection c) throws SQLException {

        try (Statement stmt = c.createStatement()) {

            String sql1 = "INSERT OR REPLACE INTO COMPANY (ID,NAME,AGE,ADDRESS,SALARY) " + "VALUES (1, 'Paul', 32, 'California', 20000.00 );";
            String sql2 = "INSERT OR REPLACE INTO COMPANY (ID,NAME,AGE,ADDRESS,SALARY) " + "VALUES (2, 'Allen', 25, 'Texas', 15000.00 );";
            String sql3 = "INSERT OR REPLACE INTO COMPANY (ID,NAME,AGE,ADDRESS,SALARY) " + "VALUES (3, 'Teddy', 23, 'Norway', 20000.00 );";
            String sql4 = "INSERT OR REPLACE INTO COMPANY (ID,NAME,AGE,ADDRESS,SALARY) " + "VALUES (4, 'Mark', 25, 'Rich-Mond', 65000.00 );";
            String sql5 = "INSERT OR REPLACE INTO COMPANY (ID,NAME,AGE,ADDRESS,SALARY) " + "VALUES (5, 'David', 27, 'Texas', 85000.00 );";
            String sql6 = "INSERT OR REPLACE INTO COMPANY (ID,NAME,AGE,ADDRESS,SALARY) " + "VALUES (6, 'Kim', 22, 'South-Hall', 45000.00 );";

            stmt.executeUpdate(sql1);
            stmt.executeUpdate(sql2);
            stmt.executeUpdate(sql3);
            stmt.executeUpdate(sql4);
            stmt.executeUpdate(sql5);
            stmt.executeUpdate(sql6);
        }
    }

    public static void insertNewData(String id, String name, String age, String address, String salary, Connection c) throws SQLException {
       String sql = "INSERT OR REPLACE INTO COMPANY (ID,NAME,AGE,ADDRESS,SALARY) VALUES(?,?,?,?,?)";

        try (PreparedStatement pstmt = c.prepareStatement(sql)) {
            pstmt.setInt(1, Integer.parseInt(id));
            pstmt.setString(2, name);
            pstmt.setInt(3, Integer.parseInt(age));
            pstmt.setString(4, address);
            pstmt.setDouble(5, Double.parseDouble(salary));

            pstmt.executeUpdate();
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Ungültige Zahlenwerte: " + e.getMessage(), e);
        }
    }

    public static void update(String table, Connection c) throws SQLException {
        validateIdentifier(table);
        try (Statement stmt = c.createStatement()){

            String sql = "UPDATE " + table + " SET ADDRESS = 'Innsbruck' WHERE ID = 4";
            stmt.executeUpdate(sql);
        }
    }

    public static void delete(String table, Connection c) throws SQLException {
        validateIdentifier(table);
        try (Statement stmt = c.createStatement();) {
            String sql = "DELETE FROM " + table + " WHERE ID = 6";
            stmt.executeUpdate(sql);
        }
    }

    public static void selectAnd(String table, String column, Connection c) throws SQLException {
        try (Statement pstmt = c.createStatement()) {
            String sql = "SELECT * FROM" + table + " WHERE NAME LIKE 'M%' AND " + column + " LIKE '2%';";
            ResultSet rs = pstmt.executeQuery(sql);

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                int age = rs.getInt("age");
                String address = rs.getString("address");
                float salary = rs.getFloat("salary");

                System.out.println("ID = " + id);
                System.out.println("NAME = " + name);
                System.out.println("AGE = " + age);
                System.out.println("ADDRESS = " + address);
                System.out.println("SALARY = " + salary);
                System.out.println();
            }
            rs.close();

        } catch (NullPointerException e) {
            System.err.println("Fehler: Null-Wert: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Datenbankfehler: " + e.getMessage());
        }
    }

    public static void selectLike(String table, String column, Connection c) throws SQLException {
        PreparedStatement pstmt = null;
        try {

            String sql = "SELECT NAME FROM ? WHERE ? LIKE '2%';";
            pstmt = c.prepareStatement(sql);

            pstmt.setString(1, table);
            pstmt.setString(2, column);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {

                String name = rs.getString("name");
                int age = rs.getInt("age");

                System.out.println("NAME = " + name);
                System.out.println("AGE = " + age);
                System.out.println();
            }
            rs.close();

        } catch (NullPointerException e) {
            System.err.println("Fehler: Null-Wert: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Datenbankfehler: " + e.getMessage());
        } finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (SQLException e) {
                System.err.println("Fehler beim Schließen: " + e.getMessage());
            }
        }

    }
}
