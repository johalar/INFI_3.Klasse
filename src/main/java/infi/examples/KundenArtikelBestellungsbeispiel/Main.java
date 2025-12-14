package infi.examples.KundenArtikelBestellungsbeispiel;

import infi.examples.DatabaseConfig;

import java.sql.DriverManager;
import java.util.Scanner;
import java.sql.Connection;
import java.sql.SQLException;

public class Main {
    static DatabaseConfig config = new DatabaseConfig("databaseKAB.properties");
    static String jdbcUrl = config.getDbUrl();
    public static void main(String[] args) {
        try (Connection c = DriverManager.getConnection(jdbcUrl)) {
            //initializeDatabase(c);
            anwendungsAnzeige(c);
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    public static void anwendungsAnzeige(Connection c) throws SQLException {
        try (Scanner input = new Scanner(System.in);) {
            boolean schleife = true;
            while (schleife) {
                ausgabe();
                switch (input.nextInt()) {
                    case 1:
                        System.out.print("Namen des Kunden (keine Leerzeichen!): ");
                        String name = input.next();
                        System.out.print("Email des Kunden: ");
                        String email = input.next();
                        Kundenbereich.insertData(c, name, email);
                        break;
                    case 2:
                        System.out.print("Namen des Artikels: ");
                        String artikelName = input.next();
                        System.out.print("Preis des Artikels (Cent): ");
                        double preis = input.nextDouble();
                        Artikelbereich.insertData(c, artikelName, preis);
                        break;
                    case 3:
                        System.out.print("KundenID: ");
                        int kundenID = input.nextInt();
                        System.out.print("ArtikelID: ");
                        int artikelID = input.nextInt();
                        System.out.print("Anzahl: ");
                        int anzahl = input.nextInt();
                        Bestellbereich.insertData(c, kundenID, artikelID, anzahl);
                        break;
                    case 4:
                        Kundenbereich.select(c);
                        break;
                    case 5:
                        Artikelbereich.select(c);
                        break;
                    case 6:
                        Bestellbereich.select(c);
                        break;
                    case 7:
                        System.out.print("ID des Kunden: ");
                        int kundenID1 = input.nextInt();
                        Bestellbereich.selectBestellungenVonKunden(c, kundenID1);
                        break;
                    case 8:
                        System.out.print("ID des Kunden: ");
                        int kundenID2 = input.nextInt();
                        System.out.print("Neuer Name: ");
                        String neuerName = input.next();
                        System.out.print("Neue Email: ");
                        String neueEmail = input.next();
                        Kundenbereich.update(c, neuerName, neueEmail, kundenID2);
                        break;
                    case 9:
                        System.out.print("ID des Artikels: ");
                        int artikelID1 = input.nextInt();
                        System.out.print("Neue Bezeichnung: ");
                        String neueBezeichnung = input.next();
                        System.out.print("Neuer Preis: ");
                        int neuerPreis = input.nextInt();
                        Artikelbereich.update(c, neueBezeichnung, neuerPreis, artikelID1);
                        break;
                    case 10:
                        System.out.print("ID der Bestellung: ");
                        int bestellungsID = input.nextInt();
                        System.out.print("ID des Kunden: ");
                        int neueKundenID = input.nextInt();
                        System.out.print("ID des Artikels: ");
                        int neueArtikelID = input.nextInt();
                        System.out.print("Anzahl: ");
                        int neueAnzahl = input.nextInt();
                        Bestellbereich.update(c, neueKundenID, neueArtikelID, neueAnzahl, bestellungsID);
                        break;
                    case 11:
                        System.out.print("ID des Kunden: ");
                        int kundenID3 = input.nextInt();
                        Kundenbereich.delete(c, kundenID3);
                        break;
                    case 12:
                        System.out.print("ID des Artikels: ");
                        int artikelID2 = input.nextInt();
                        Artikelbereich.delete(c, artikelID2);
                        break;
                    case 13:
                        System.out.print("ID der Bestellung: ");
                        int bestellungsID1 = input.nextInt();
                        Bestellbereich.delete(c, bestellungsID1);
                        break;
                    case 14:
                        schleife = false;
                        break;
                    default:
                        System.err.println("Falsche Eingabe nur zahlen von 1-8!!");
                }
            }
        }
    }

    public static void initializeDatabase(Connection c) throws SQLException {
        try {
            Kundenbereich.create(c);
            Artikelbereich.create(c);
            Bestellbereich.create(c);
            insertTestData(c);

        } catch (SQLException e) {
            throw new DataAccessException("Datenbank-Initialisierung fehlgeschlagen.", e);
        }
    }

    public static void insertTestData(Connection c) throws SQLException {
        // --- 20 Neue Kunden (Startet voraussichtlich bei ID 1) ---
        Kundenbereich.insertData(c, "Anja Huber", "anja.huber@firma.de");
        Kundenbereich.insertData(c, "Bernd Keller", "bernd.keller@mail.at");
        Kundenbereich.insertData(c, "Clara Vogt", "clara.vogt@gmx.ch");
        Kundenbereich.insertData(c, "David Maier", "d.maier@web.de");
        Kundenbereich.insertData(c, "Eva Scholz", "eva.scholz@example.com");
        Kundenbereich.insertData(c, "Franz Lindner", "franz.l@post.de");
        Kundenbereich.insertData(c, "Gabi Neumann", "gabi_n@email.com");
        Kundenbereich.insertData(c, "Hans Berger", "h.berger@company.net");
        Kundenbereich.insertData(c, "Ina Walter", "ina.walter@xyz.org");
        Kundenbereich.insertData(c, "Jens Becker", "jens.becker@swissmail.ch");
        Kundenbereich.insertData(c, "Katrin Wolf", "k.wolf@business.com");
        Kundenbereich.insertData(c, "Lukas Zeller", "lukas.zeller@gmx.de");
        Kundenbereich.insertData(c, "Maria Otto", "maria.otto@private.ch");
        Kundenbereich.insertData(c, "Nico Herrmann", "nico.herr@webmail.at");
        Kundenbereich.insertData(c, "Olga Petry", "olga.petry@mail.net");
        Kundenbereich.insertData(c, "Peter Funk", "peter.funk@online.de");
        Kundenbereich.insertData(c, "Quinn Riese", "quinn.riese@int.com"); // Fiktiver Name
        Kundenbereich.insertData(c, "Rieke Steiner", "rieke.steiner@postfach.ch");
        Kundenbereich.insertData(c, "Stefan Tiedemann", "stefan.t@mail.de");
        Kundenbereich.insertData(c, "Tina Volkmann", "tina.v@web.at");

        // --- 20 Neue Artikel (Startet voraussichtlich bei ID 1) ---
        Artikelbereich.insertData(c, "Drahtlose Ladestation", 39.99);
        Artikelbereich.insertData(c, "4K Action-Kamera", 199.50);
        Artikelbereich.insertData(c, "Bluetooth-Lautsprecher", 79.00);
        Artikelbereich.insertData(c, "E-Book-Reader", 119.99);
        Artikelbereich.insertData(c, "Tragbarer Beamer", 450.00);
        Artikelbereich.insertData(c, "Gaming-Stuhl (Leder)", 289.00);
        Artikelbereich.insertData(c, "Smart-Home Thermostat", 125.50);
        Artikelbereich.insertData(c, "Robotersauger", 350.00);
        Artikelbereich.insertData(c, "Mesh WLAN System", 189.99);
        Artikelbereich.insertData(c, "Tablet 10 Zoll", 420.00);
        Artikelbereich.insertData(c, "Grafiktablett", 89.90);
        Artikelbereich.insertData(c, "Drohne mit Kamera", 599.00);
        Artikelbereich.insertData(c, "Netzteil 650W", 79.50);
        Artikelbereich.insertData(c, "HDMI Kabel 2m", 15.99);
        Artikelbereich.insertData(c, "Software: Virenschutz", 49.00);
        Artikelbereich.insertData(c, "Gaming Headset", 95.00);
        Artikelbereich.insertData(c, "Kaffee-Maschine (Kapsel)", 69.90);
        Artikelbereich.insertData(c, "Externe SSD 1TB", 149.99);
        Artikelbereich.insertData(c, "Temperatursensor Smart", 25.00);
        Artikelbereich.insertData(c, "Powerbank 20000mAh", 35.50);

        // --- 20 neue Bestellungen (Kundennummern 1 bis 20, Artikelnummern 1 bis 20) ---
        // diverse Bestellungen, um alle Kunden und Artikel abzudecken
        Bestellbereich.insertData(c, 1, 4, 1);    // Anja kauft E-Book-Reader
        Bestellbereich.insertData(c, 2, 1, 2);    // Bernd kauft 2 Ladestationen
        Bestellbereich.insertData(c, 3, 10, 1);   // Clara kauft Tablet
        Bestellbereich.insertData(c, 4, 20, 3);   // David kauft 3 Powerbanks
        Bestellbereich.insertData(c, 5, 17, 1);   // Eva kauft Kaffeemaschine
        Bestellbereich.insertData(c, 6, 6, 1);    // Franz kauft Gaming-Stuhl
        Bestellbereich.insertData(c, 7, 14, 5);   // Gabi kauft 5 HDMI Kabel
        Bestellbereich.insertData(c, 8, 8, 1);    // Hans kauft Robotersauger
        Bestellbereich.insertData(c, 9, 3, 2);    // Ina kauft 2 Bluetooth-Lautsprecher
        Bestellbereich.insertData(c, 10, 15, 1);  // Jens kauft Virenschutz
        Bestellbereich.insertData(c, 11, 12, 1);  // Katrin kauft Drohne
        Bestellbereich.insertData(c, 12, 11, 1);  // Lukas kauft Grafiktablett
        Bestellbereich.insertData(c, 13, 18, 1);  // Maria kauft Externe SSD
        Bestellbereich.insertData(c, 14, 5, 1);   // Nico kauft Beamer
        Bestellbereich.insertData(c, 15, 9, 1);   // Olga kauft Mesh WLAN
        Bestellbereich.insertData(c, 16, 13, 1);  // Peter kauft Netzteil
        Bestellbereich.insertData(c, 17, 7, 2);   // Quinn kauft 2 Thermostate
        Bestellbereich.insertData(c, 18, 16, 1);  // Rieke kauft Gaming Headset
        Bestellbereich.insertData(c, 19, 2, 1);   // Stefan kauft Action-Kamera
        Bestellbereich.insertData(c, 20, 19, 4);  // Tina kauft 4 Sensoren
    }

    public static void ausgabe() {
        System.out.println("\n====== KUNDEN-ARTIKEL-BESTELLUNGEN ======");
        System.out.println("(1) Kunde anlegen");
        System.out.println("(2) Artikel anlegen");
        System.out.println("(3) Bestellung anlegen");
        System.out.println("(4) Alle Kunden anzeigen");
        System.out.println("(5) Alle Artikel anzeigen");
        System.out.println("(6) Alle Bestellungen anzeigen");
        System.out.println("(7) Bestellungen eines Kunden anzeigen");
        System.out.println("(8) Kunden aktualisieren");
        System.out.println("(9) Artikel aktualisieren");
        System.out.println("(10) Bestellung aktualisieren");
        System.out.println("(11) Kunde löschen");
        System.out.println("(12) Artikel löschen");
        System.out.println("(13) Bestellung löschen");
        System.out.println("(14) Beenden");
        System.out.print("Geben sie eine Zahl ein: ");
    }
}
