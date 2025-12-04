package infi.examples;

import java.sql.*;
import java.util.Scanner;

public class SQLiteRandomStats {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/randomNumberers?user=root&useSSL=false&serverTimezone=UTC";

    public static void main(String[] args) {
        try (Connection c = DriverManager.getConnection(DB_URL);
             Scanner sc = new Scanner(System.in)) {

            System.out.print("Enter the number of random values to generate: ");
            int numberOfValues = sc.nextInt();

            if (hasMoreThanHundretThousandRows(c)) dropRandomNumbersTable(c);

            createRandomNumbersTable(c);
            insertRandomNumbers(c, numberOfValues);

            int oddCount = countOddNumbers(c);
            int evenCount = countEvenNumbers(c);

            if (args.length >= 1) printStatsBasedOnArgument(oddCount, evenCount, args[0]);
            else printAllStats(oddCount, evenCount);

            if (args.length >= 2 && isNumeric(args[1])) deleteFirstNEntries(c, args[1]);

        } catch (SQLException e) {
            System.err.println("ERROR: " + e.getMessage());
            if (e.getCause() != null) System.err.println("Cause: " + e.getCause().getMessage());
        }
    }

    public static void dropRandomNumbersTable(Connection c) throws SQLException {
        try (Statement stmt = c.createStatement()) {
            stmt.executeUpdate("DROP TABLE IF EXISTS RANDOM_NUMBERS");
        } catch (SQLException e) {
            throw new SQLException("Error while dropping table", e);
        }
    }

    public static void createRandomNumbersTable(Connection c) throws SQLException {
        try (Statement stmt = c.createStatement()) {
            String sql = """
                CREATE TABLE IF NOT EXISTS RANDOM_NUMBERS (
                    ID INTEGER PRIMARY KEY AUTO_INCREMENT,
                    VALUE INTEGER NOT NULL,
                    IS_ODD INTEGER NOT NULL
                )
            """;
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            throw new SQLException("Failed to create table", e);
        }
    }

    public static boolean hasMoreThanHundretThousandRows(Connection c) throws SQLException {
        String sql = "SELECT COUNT(ID) AS COUNT FROM RANDOM_NUMBERS";
        try (ResultSet rs = c.createStatement().executeQuery(sql)) {
            return rs.next() && rs.getInt("COUNT") > 100000;
        } catch (SQLException e) {
            throw new SQLException("Error while counting rows", e);
        }
    }

    public static int generateRandomNumber() {
        return (int) (Math.random() * 100000) + 1;
    }

    public static void insertRandomNumbers(Connection c, int count) throws SQLException {
        String sql = "INSERT INTO RANDOM_NUMBERS (VALUE, IS_ODD) VALUES (?, ?)";
        try (PreparedStatement pstmt = c.prepareStatement(sql)) {
            for (int i = 0; i < count; i++) {
                int value = generateRandomNumber();
                pstmt.setInt(1, value);
                pstmt.setInt(2, value % 2);
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            throw new SQLException("Error while inserting random numbers", e);
        }
    }

    public static int countOddNumbers(Connection c) throws SQLException {
        String sql = "SELECT SUM(IS_ODD) AS SUM FROM RANDOM_NUMBERS";
        try (ResultSet rs = c.createStatement().executeQuery(sql)) {
            return rs.next() ? rs.getInt("SUM") : 0;
        } catch (SQLException e) {
            throw new SQLException("Error counting odd numbers", e);
        }
    }

    public static int countEvenNumbers(Connection c) throws SQLException {
        String sql = "SELECT SUM(IS_ODD) AS odd, COUNT(*) AS total FROM RANDOM_NUMBERS";
        try (ResultSet rs = c.createStatement().executeQuery(sql)) {
            // Bedingung ? Wert_wenn_wahr : Wert_wenn_falsch
            return rs.next() ? rs.getInt("total") - rs.getInt("odd") : 0;
        } catch (SQLException e) {
            throw new SQLException("Error counting even numbers", e);
        }
    }

    public static void printStatsBasedOnArgument(int oddCount, int evenCount, String arg) {
        if (arg.equalsIgnoreCase("ODD")) {
            System.out.println("There are " + oddCount + " odd numbers");
        } else if (arg.equalsIgnoreCase("EVEN")) {
            System.out.println("There are " + evenCount + " even numbers");
        } else {
            printAllStats(oddCount, evenCount);
        }
    }

    public static void printAllStats(int oddCount, int evenCount) {
        System.out.println("There are " + evenCount + " even numbers");
        System.out.println("There are " + oddCount + " odd numbers");
    }

    public static void deleteFirstNEntries(Connection c, String arg) throws SQLException {
        String sql = "DELETE FROM RANDOM_NUMBERS WHERE ID <= ?";
        try (PreparedStatement pstmt = c.prepareStatement(sql)) {
            pstmt.setInt(1, Integer.parseInt(arg));
            int deleted = pstmt.executeUpdate();
            System.out.println("Deleted " + deleted + " entries from RANDOM_NUMBERS");
        } catch (SQLException e) {
            throw new SQLException("Error deleting entries", e);
        }
    }

    public static boolean isNumeric(String str) {
        return str != null && str.matches("-?\\d+");
    }
}
