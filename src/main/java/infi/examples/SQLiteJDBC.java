package infi.examples;

import java.sql.*;

public class SQLiteJDBC {
    private static final String DB_URL = "jdbc:sqlite:test.db";
    private static final String TABLE_NAME = "COMPANY";
    private static final String COL_ID = "ID";
    private static final String COL_NAME = "NAME";
    private static final String COL_ADDRESS = "ADDRESS";
    private static final String COL_AGE = "AGE";

    public static void main(String[] args) {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.err.println("SQLite JDBC Treiber nicht gefunden: " + e.getMessage());
            return; // Programm beenden, wenn Treiber fehlt
        }

        try (Connection c = DriverManager.getConnection(DB_URL);) {
            System.out.println("Datenbank erfolgreich geöffnet");

            createTable(c);

            insertData(c);

            insertNewData("7", "Finn", "16", "Koessen", "2400.00", c);

            selectLike(TABLE_NAME, COL_AGE, c);

            update(TABLE_NAME, c);

            selectAnd(TABLE_NAME, COL_AGE, c);

            delete(TABLE_NAME, c);

            dropTable(TABLE_NAME, c);

        } catch (SQLException e) {
            System.err.println("FEHLER: " + e.getMessage());
            if (e.getCause() != null) {
                System.err.println("Ursache: " + e.getCause().getMessage());
            }
//            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            System.err.println("Ungültige Parameter: " + e.getMessage());
        }
    }

    private static void validateIdentifier(String identifier) {
        if (identifier == null || !identifier.matches("[a-zA-Z_][a-zA-Z0-9_]*")) {
            throw new IllegalArgumentException("Ungültiger Tabellenname: " + identifier);
        }
    }

    public static void createTable(Connection c) throws SQLException {
        try (Statement stmt = c.createStatement()) {
            String sql = "CREATE TABLE IF NOT EXISTS COMPANY "
                    + "(ID INT PRIMARY KEY NOT NULL,"
                    + " NAME TEXT NOT NULL, "
                    + " AGE INT NOT NULL, "
                    + " ADDRESS CHAR(50), "
                    + " SALARY REAL)";
            stmt.executeUpdate(sql);

        } catch (SQLException e) {
            throw new SQLException("Fehler beim Erstellen des Tables: ", e);
        }
    }

    public static void dropTable(String name, Connection c) throws SQLException {
        validateIdentifier(name);

        try (Statement stmt = c.createStatement()) {
            String sql = "DROP TABLE " + name;
            stmt.executeUpdate(sql);

        } catch (SQLException e) {
            throw new SQLException("Fehler beim Dropen des Tables: ", e);
        }
    }

    public static void insertData(Connection c) throws SQLException {
        try /*(Statement stmt = c.createStatement())*/ {
            String[][] data = {
                    {"1", "Paul", "32", "California", "20000.00"},
                    {"2", "Allen", "25", "Texas", "15000.00"},
                    {"3", "Teddy", "23", "Norway", "20000.00"},
                    {"4", "Mark", "25", "Rich-Mond", "65000.00"},
                    {"5", "David", "27", "Texas", "85000.00"},
                    {"6", "Kim", "22", "South-Hall", "45000.00"}
            };

            for (String[] row : data) {
                insertNewData(row[0], row[1], row[2], row[3], row[4], c);
            }

        } catch (SQLException e) {
            throw new SQLException("Fehler beim Einfügen der Beispieldaten", e);
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
        } catch (SQLException e) {
            throw new SQLException("Fehler beim Einfügen der Daten: ", e);
        }
    }

    public static void update(String table, Connection c) throws SQLException {
        validateIdentifier(table);
        try (Statement stmt = c.createStatement()) {
            String sql = "UPDATE " + table + " SET ADDRESS = 'Innsbruck' WHERE ID = 4";
            stmt.executeUpdate(sql);

        } catch (SQLException e) {
            throw new SQLException("Fehler beim Updaten der Daten: ", e);
        }
    }

    public static void delete(String table, Connection c) throws SQLException {
        validateIdentifier(table);

        try (Statement stmt = c.createStatement()) {
            String sql = "DELETE FROM " + table + " WHERE ID = 6";
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            throw new SQLException("Fehler beim Löschen der Tabelle: ", e);
        }
    }

    public static void selectAnd(String table, String column, Connection c) throws SQLException {
        validateIdentifier(table);
        validateIdentifier(column);

        String sql = "SELECT * FROM " + table + " WHERE NAME LIKE 'M%' AND " + column + " LIKE '2%';";

        try (Statement pstmt = c.createStatement(); ResultSet rs = pstmt.executeQuery(sql)) {
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

        } catch (SQLException e) {
            throw new SQLException("Fehler beim SelectAnd: ", e);
        }
    }

    public static void selectLike(String table, String column, Connection c) throws SQLException {
        validateIdentifier(table);
        validateIdentifier(column);

        String sql = "SELECT NAME FROM " + table + " WHERE " + column + " LIKE '2%';";

        try (Statement pstmt = c.createStatement(); ResultSet rs = pstmt.executeQuery(sql)) {
            while (rs.next()) {
                String name = rs.getString("name");
                System.out.println("NAME = " + name);
                System.out.println();
            }

        } catch (SQLException e) {
            throw new SQLException("Fehler beim SelectLike: ", e);
        }
    }
}
