package infi.examples;

import java.sql.*;

public class SQLiteForgeienKey {
    private static final String DB_URL = "jdbc:sqlite:customersOrders.db";

    public static void main(String[] args) {
        try (Connection c = DriverManager.getConnection(DB_URL)) {
            initializeDatabase(c);
            runTests(c);
        } catch (SQLException e) {
            System.err.println("ERROR: " + e.getMessage());
            if (e.getCause() != null) System.err.println("Cause: " + e.getCause().getMessage());
        }
    }

    public static void initializeDatabase(Connection c) throws SQLException {
        try {
            System.out.println("Database Initialization Start");

            activateForeignKeys(c);
            System.out.println("Foreign keys activated");

            c.setAutoCommit(false);
            System.out.println("Auto commit deactivated");

            dropTables(c);
            System.out.println("Old tables dropped");

            createTableCustomers(c);
            System.out.println("Customers table created");

            createTableOrders(c);
            System.out.println("Orders table created");

            insertAllValues(c);
            System.out.println("Test data inserted");

            c.commit();
            System.out.println("Changes committed");

            System.out.println("Database Initialization Complete\n");

        } catch (SQLException e) {
            c.rollback();
            throw new SQLException("Database initialization failed - All changes rolled back", e);
        }
    }

    public static void runTests(Connection c) throws SQLException {
        System.out.println("Running Tests");

        try {
            System.out.println("\nTest 1: Display all orders with customers");
            selectOrdersWithCustomers(c);
            System.out.println("Test 1 passed");
        } catch (SQLException e) {
            System.err.println("Test 1 failed: " + e.getMessage());
        }

        try {
            System.out.println("\nTest 2: Foreign Key Restrict Test");
            proofForeignKeyWorks(c);
            System.out.println("\nTest 2 passed");
        } catch (SQLException e) {
            System.err.println("Test 2 failed: " + e.getMessage());
        }

        System.out.println("\nTests Complete");
    }

    public static void activateForeignKeys(Connection c) throws SQLException {
        try (Statement stmt = c.createStatement()) {
            stmt.executeUpdate("PRAGMA foreign_keys = ON;");
        } catch (SQLException e) {
            throw new SQLException("Error while activating foreignKeys", e);
        }
    }

    public static void dropTables(Connection c) throws SQLException {
        try (Statement stmt = c.createStatement()) {
            stmt.executeUpdate("DROP TABLE IF EXISTS orders");
            stmt.executeUpdate("DROP TABLE IF EXISTS customers");
        } catch (SQLException e) {
            c.rollback();
            throw new SQLException("Error while dropping the tables - Transaction rolled back", e);
        }
    }

    public static void createTableCustomers(Connection c) throws SQLException {
        try (Statement stmt = c.createStatement()) {
            String sql = """
                    CREATE TABLE IF NOT EXISTS customers (
                        customer_id INTEGER PRIMARY KEY AUTOINCREMENT,
                        name TEXT NOT NULL,
                        email TEXT NOT NULL,
                        user_name TEXT NOT NULL UNIQUE
                    )
                    """;
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            c.rollback();
            throw new SQLException("Failed to create table customers - Transaction rolled back", e);
        }
    }

    // Dass die Datenbank für uns die Verwaltung macht und die Datenkonsitens sichert

    public static void createTableOrders(Connection c) throws SQLException {
        try (Statement stmt = c.createStatement()) {
            String sql = """
                    CREATE TABLE IF NOT EXISTS orders (
                        order_id INTEGER PRIMARY KEY AUTOINCREMENT,
                        customer_id INTEGER NOT NULL,
                        product TEXT NOT NULL,
                        price REAL NOT NULL,
                        FOREIGN KEY (customer_id)
                            REFERENCES customers (customer_id)
                            ON UPDATE RESTRICT
                            ON DELETE RESTRICT
                    )
                    """;
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            c.rollback();
            throw new SQLException("Failed to create table orders - Transaction rolled back", e);
        }
    }

    public static void insertDataIntoCustomers(Connection c, String name, String email, String user_name) throws SQLException {
        String sql = "INSERT INTO customers (name, email, user_name) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = c.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, email);
            pstmt.setString(3, user_name);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException("Error while inserting the Data for the Customers", e);
        }
    }

    public static void insertDataIntoOrders(Connection c, int customer_id, String product, Double price) throws SQLException {
        String sql = "INSERT INTO orders (customer_id, product, price) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = c.prepareStatement(sql)) {
            pstmt.setInt(1, customer_id);
            pstmt.setString(2, product);
            pstmt.setDouble(3, price);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException("Error while inserting the Data for the Orders", e);
        }
    }

    public static void insertAllValues(Connection c) throws SQLException {
        try {
            insertDataIntoCustomers(c, "Max", "max@tsn.at", "maximax");
            insertDataIntoCustomers(c, "Leo", "leoLar@gmail.com", "leoChill");
            insertDataIntoCustomers(c, "Patti", "pat02@icloud.com", "patR22");
            insertDataIntoOrders(c, 1, "Lenovo Thinkpad", 1249.99);
            insertDataIntoOrders(c, 1, "Samsung Galaxy S23", 314.99);
            insertDataIntoOrders(c, 2, "iPhone 15 Pro", 699.99);
            insertDataIntoOrders(c, 3, "PS5 Slim", 519.90);
            insertDataIntoOrders(c, 3, "iPad Air", 599.99);
        } catch (SQLException e) {
            c.rollback();
            throw new SQLException("Failed while inserting the Data - Transaction rolled back", e);
        }
    }

    public static void selectOrdersWithCustomers(Connection c) throws SQLException {
        String sql = """
                SELECT o.order_id, o.product, o.price, c.name, c.customer_id
                FROM orders o
                JOIN customers c ON o.customer_id = c.customer_id
                ORDER BY c.customer_id, o.order_id
                """;
        try (ResultSet rs = c.createStatement().executeQuery(sql)) {
            while (rs.next()) {
                System.out.println("Customer Name: " + rs.getString("name"));
                System.out.println("Customer ID: " + rs.getInt("customer_id"));
                System.out.println("Order ID: " + rs.getInt("order_id"));
                System.out.println("Product: " + rs.getString("product"));
                System.out.println("Price: " + rs.getDouble("price"));
                System.out.println();
            }
        } catch (SQLException e) {
            throw new SQLException("Error while selecting the orders and customers", e);
        }
    }

    public static void proofForeignKeyWorks(Connection c) throws SQLException {
        try {
            deleteFirstCustomer(c);
            c.commit();
            selectFirstCustomersOrder(c);
        } catch (SQLException e) {
            c.rollback();
            throw new SQLException("Error during foreign key proof - Transaction rolled back", e);
        }
    }

    public static void deleteFirstCustomer(Connection c) throws SQLException {
        try (Statement stmt = c.createStatement()) {
            stmt.executeUpdate("DELETE FROM customers WHERE customer_id = 1;");
        } catch (SQLException e) {
            c.rollback();
            throw new SQLException("Error while deleting first customer - Transaction rolled back", e);
        }
    }

    public static void selectFirstCustomersOrder(Connection c) throws SQLException {
        try (ResultSet rs = c.createStatement().executeQuery("SELECT COUNT(*) as count FROM orders WHERE customer_id = 1;")) {
            while (rs.next()) {
                int count = rs.getInt("count");
                System.out.println("Number of orders from customer ID 1: " + count);
            }
        } catch (SQLException e) {
            throw new SQLException("Error while selecting the orders from the first customer", e);
        }
    }
}