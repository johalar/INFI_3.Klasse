package infi.examples.KundenArtikelBestellungsbeispiel;

import java.sql.DriverManager;
import java.util.Scanner;
import java.sql.Connection;
import java.sql.SQLException;

public class Main {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/kundenartikelbestellungen?user=root&useSSL=false&serverTimezone=UTC";

    public static void main(String[] args) {
        try (Scanner input = new Scanner(System.in); Connection c = DriverManager.getConnection(DB_URL)) {
//            initializeDatabase(c);
            boolean schleife = true;
            while (schleife) {
                ausgabe();
                switch (input.nextInt()) {
                    case 1:
                        System.out.print("Namen des Kunden (keine Leerzeichen!): ");
                        String name = input.next();
                        System.out.print("Email des Kunden: ");
                        String email = input.next();
                        Kundenbereich.insertIntoKunden(c, name, email);
                        break;
                    case 2:
                        System.out.print("Namen des Artikels: ");
                        String artikelName = input.next();
                        System.out.print("Preis des Artikels (Cent): ");
                        int preis = input.nextInt();
                        Artikelbereich.insertIntoArtikel(c, artikelName, preis);
                        break;
                    case 3:
                        System.out.print("KundenID: ");
                        int kundenID = input.nextInt();
                        System.out.print("ArtikelID: ");
                        int artikelID = input.nextInt();
                        System.out.print("Anzahl: ");
                        int anzahl = input.nextInt();
                        Bestellbereich.insertIntoBestellungen(c, kundenID, artikelID, anzahl);
                        break;
                    case 4:
                        Kundenbereich.selectKunden(c);
                        break;
                    case 5:
                        Artikelbereich.selectArtikel(c);
                        break;
                    case 6:
                        Bestellbereich.selectBestellungen(c);
                        break;
                    case 7:
                        System.out.print("ID des Kunden: ");
                        int kundenID1 = input.nextInt();
                        Bestellbereich.selectBestellungenVonKunden(c, kundenID1);
                        break;
                    case 8:
                        schleife = false;
                        break;
                    default:
                        System.err.println("Falsche Eingabe nur zahlen von 1-7!!");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void initializeDatabase(Connection c) throws SQLException {
        try {
            Kundenbereich.createKunden(c);
            Artikelbereich.createArtikel(c);
            Bestellbereich.createBestellungen(c);
            insertTestData(c);

        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public static void insertTestData(Connection c) throws SQLException {
        // --- 20 Neue Kunden (Startet voraussichtlich bei ID 1) ---
        Kundenbereich.insertIntoKunden(c, "Anja Huber", "anja.huber@firma.de");
        Kundenbereich.insertIntoKunden(c, "Bernd Keller", "bernd.keller@mail.at");
        Kundenbereich.insertIntoKunden(c, "Clara Vogt", "clara.vogt@gmx.ch");
        Kundenbereich.insertIntoKunden(c, "David Maier", "d.maier@web.de");
        Kundenbereich.insertIntoKunden(c, "Eva Scholz", "eva.scholz@example.com");
        Kundenbereich.insertIntoKunden(c, "Franz Lindner", "franz.l@post.de");
        Kundenbereich.insertIntoKunden(c, "Gabi Neumann", "gabi_n@email.com");
        Kundenbereich.insertIntoKunden(c, "Hans Berger", "h.berger@company.net");
        Kundenbereich.insertIntoKunden(c, "Ina Walter", "ina.walter@xyz.org");
        Kundenbereich.insertIntoKunden(c, "Jens Becker", "jens.becker@swissmail.ch");
        Kundenbereich.insertIntoKunden(c, "Katrin Wolf", "k.wolf@business.com");
        Kundenbereich.insertIntoKunden(c, "Lukas Zeller", "lukas.zeller@gmx.de");
        Kundenbereich.insertIntoKunden(c, "Maria Otto", "maria.otto@private.ch");
        Kundenbereich.insertIntoKunden(c, "Nico Herrmann", "nico.herr@webmail.at");
        Kundenbereich.insertIntoKunden(c, "Olga Petry", "olga.petry@mail.net");
        Kundenbereich.insertIntoKunden(c, "Peter Funk", "peter.funk@online.de");
        Kundenbereich.insertIntoKunden(c, "Quinn Riese", "quinn.riese@int.com"); // Fiktiver Name
        Kundenbereich.insertIntoKunden(c, "Rieke Steiner", "rieke.steiner@postfach.ch");
        Kundenbereich.insertIntoKunden(c, "Stefan Tiedemann", "stefan.t@mail.de");
        Kundenbereich.insertIntoKunden(c, "Tina Volkmann", "tina.v@web.at");

        // --- 20 Neue Artikel (Startet voraussichtlich bei ID 1) ---
        Artikelbereich.insertIntoArtikel(c, "Drahtlose Ladestation", 39.99);
        Artikelbereich.insertIntoArtikel(c, "4K Action-Kamera", 199.50);
        Artikelbereich.insertIntoArtikel(c, "Bluetooth-Lautsprecher", 79.00);
        Artikelbereich.insertIntoArtikel(c, "E-Book-Reader", 119.99);
        Artikelbereich.insertIntoArtikel(c, "Tragbarer Beamer", 450.00);
        Artikelbereich.insertIntoArtikel(c, "Gaming-Stuhl (Leder)", 289.00);
        Artikelbereich.insertIntoArtikel(c, "Smart-Home Thermostat", 125.50);
        Artikelbereich.insertIntoArtikel(c, "Robotersauger", 350.00);
        Artikelbereich.insertIntoArtikel(c, "Mesh WLAN System", 189.99);
        Artikelbereich.insertIntoArtikel(c, "Tablet 10 Zoll", 420.00);
        Artikelbereich.insertIntoArtikel(c, "Grafiktablett", 89.90);
        Artikelbereich.insertIntoArtikel(c, "Drohne mit Kamera", 599.00);
        Artikelbereich.insertIntoArtikel(c, "Netzteil 650W", 79.50);
        Artikelbereich.insertIntoArtikel(c, "HDMI Kabel 2m", 15.99);
        Artikelbereich.insertIntoArtikel(c, "Software: Virenschutz", 49.00);
        Artikelbereich.insertIntoArtikel(c, "Gaming Headset", 95.00);
        Artikelbereich.insertIntoArtikel(c, "Kaffee-Maschine (Kapsel)", 69.90);
        Artikelbereich.insertIntoArtikel(c, "Externe SSD 1TB", 149.99);
        Artikelbereich.insertIntoArtikel(c, "Temperatursensor Smart", 25.00);
        Artikelbereich.insertIntoArtikel(c, "Powerbank 20000mAh", 35.50);

        // --- 20 Neue Bestellungen (Kundennummern 1 bis 20, Artikelnummern 1 bis 20) ---
        // Diverse Bestellungen, um alle Kunden und Artikel abzudecken
        Bestellbereich.insertIntoBestellungen(c, 1, 4, 1);    // Anja kauft E-Book-Reader
        Bestellbereich.insertIntoBestellungen(c, 2, 1, 2);    // Bernd kauft 2 Ladestationen
        Bestellbereich.insertIntoBestellungen(c, 3, 10, 1);   // Clara kauft Tablet
        Bestellbereich.insertIntoBestellungen(c, 4, 20, 3);   // David kauft 3 Powerbanks
        Bestellbereich.insertIntoBestellungen(c, 5, 17, 1);   // Eva kauft Kaffeemaschine
        Bestellbereich.insertIntoBestellungen(c, 6, 6, 1);    // Franz kauft Gaming-Stuhl
        Bestellbereich.insertIntoBestellungen(c, 7, 14, 5);   // Gabi kauft 5 HDMI Kabel
        Bestellbereich.insertIntoBestellungen(c, 8, 8, 1);    // Hans kauft Robotersauger
        Bestellbereich.insertIntoBestellungen(c, 9, 3, 2);    // Ina kauft 2 Bluetooth-Lautsprecher
        Bestellbereich.insertIntoBestellungen(c, 10, 15, 1);  // Jens kauft Virenschutz
        Bestellbereich.insertIntoBestellungen(c, 11, 12, 1);  // Katrin kauft Drohne
        Bestellbereich.insertIntoBestellungen(c, 12, 11, 1);  // Lukas kauft Grafiktablett
        Bestellbereich.insertIntoBestellungen(c, 13, 18, 1);  // Maria kauft Externe SSD
        Bestellbereich.insertIntoBestellungen(c, 14, 5, 1);   // Nico kauft Beamer
        Bestellbereich.insertIntoBestellungen(c, 15, 9, 1);   // Olga kauft Mesh WLAN
        Bestellbereich.insertIntoBestellungen(c, 16, 13, 1);  // Peter kauft Netzteil
        Bestellbereich.insertIntoBestellungen(c, 17, 7, 2);   // Quinn kauft 2 Thermostate
        Bestellbereich.insertIntoBestellungen(c, 18, 16, 1);  // Rieke kauft Gaming Headset
        Bestellbereich.insertIntoBestellungen(c, 19, 2, 1);   // Stefan kauft Action-Kamera
        Bestellbereich.insertIntoBestellungen(c, 20, 19, 4);  // Tina kauft 4 Sensoren
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
        System.out.println("(8) Beenden");
        System.out.print("Geben sie eine Zahl ein: ");
    }
}
