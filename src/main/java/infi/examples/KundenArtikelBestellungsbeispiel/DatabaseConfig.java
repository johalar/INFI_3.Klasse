package infi.examples.KundenArtikelBestellungsbeispiel;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class DatabaseConfig {
    private static final Properties properties = new Properties();

    static {
        try (InputStream input = DatabaseConfig.class.getClassLoader().getResourceAsStream("database.properties")) {
            if (input == null) throw new RuntimeException("database.properties nicht gefunden!");
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Fehler beim Laden der Konfiguration: " + e.getMessage(), e);
        }
    }

    public static String getDbUrl() {
        String host = properties.getProperty("db.host");
        String port = properties.getProperty("db.port");
        String name = properties.getProperty("db.name");
        String user = properties.getProperty("db.user");
        String password = properties.getProperty("db.password");
        String useSSL = properties.getProperty("db.useSSL");
        String timezone = properties.getProperty("db.serverTimezone");

        return String.format("jdbc:mysql://%s:%s/%s?user=%s&password=%s&useSSL=%s&serverTimezone=%s", host, port, name, user, password, useSSL, timezone);
    }

    public static String getProperty(String key) {
        return properties.getProperty(key);
    }
}
