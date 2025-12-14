package infi.examples;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class DatabaseConfig {
    private final Properties properties = new Properties();
    private final String propertiesFileName;

    public DatabaseConfig(String propertiesFileName) {
        this.propertiesFileName = propertiesFileName;
        loadProperties();
    }

    private void loadProperties() {
        try (InputStream input = DatabaseConfig.class.getClassLoader()
                .getResourceAsStream(propertiesFileName)) {
            if (input == null) {
                throw new RuntimeException(
                        propertiesFileName + " nicht gefunden!");
            }
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException(
                    "Fehler beim Laden der Konfiguration: " + e.getMessage(), e);
        }
    }

    public String getDbUrl() {
        String host = getProperty("db.host");
        String port = getProperty("db.port");
        String name = getProperty("db.name");
        String user = getProperty("db.user");
        String password = getProperty("db.password");
        String useSSL = getProperty("db.useSSL");
        String timezone = getProperty("db.serverTimezone");
        String allowPublicKeyRetrieval = getProperty("db.allowPublicKeyRetrieval");

        return String.format(
                "jdbc:mysql://%s:%s/%s?user=%s&password=%s&useSSL=%s&serverTimezone=%s&allowPublicKeyRetrieval=%s",
                host, port, name, user, password, useSSL, timezone, allowPublicKeyRetrieval
        );
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }
}