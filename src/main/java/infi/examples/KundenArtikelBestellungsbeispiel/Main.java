package infi.examples.KundenArtikelBestellungsbeispiel;

import infi.examples.DatabaseConfig;

import java.sql.DriverManager;
import java.util.Scanner;
import java.sql.Connection;
import java.sql.SQLException;

public class Main {
    private static final DatabaseConfig CONFIG = new DatabaseConfig("databaseKAB.properties");
    private static final String JDBC_URL = CONFIG.getDbUrl();

    public static void main(String[] args) {
        try (Connection c = DriverManager.getConnection(JDBC_URL)) {
            initializeDatabase(c);
            anwendungsLogik(c);
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    public static void anwendungsLogik(Connection c) throws SQLException {
        try (Scanner input = new Scanner(System.in)) {
            boolean schleife = true;
            while (schleife) {
                ausgabe();
                int aktion = leseNaechsteZahl(input);

                if (aktion >= 1 && aktion <= 4) {
                    System.out.println("---------------------------------");
                    System.out.println("In welchem Bereich? (1=Kunden, 2=Artikel, 3=Bestellung, 4=Lagerstandort)");
                    int bereich = leseNaechsteZahl(input);
                    verarbeiteAktion(c, input, aktion, bereich);
                } else {
                    verarbeiteAktion(c, input, aktion, 0);
                    if (aktion == 6) schleife = false;
                }
            }
        }
    }

    private static int leseNaechsteZahl(Scanner input) {
        if (input.hasNextInt()) {
            return input.nextInt();
        } else {
            System.err.println("Falsche Eingabe. Bitte geben Sie eine Zahl ein.");
            input.next();
            return -1;
        }
    }

    private static void verarbeiteAktion(Connection c, Scanner input, int aktion, int bereich) throws SQLException {
        try {
            switch (aktion) {
                case 1:
                    switch (bereich) {
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
                            System.out.print("LagerID (Standort der Entnahme): ");
                            int lagerID = input.nextInt();
                            System.out.print("Anzahl: ");
                            int anzahl = input.nextInt();
                            Bestellbereich.insertData(c, kundenID, artikelID, anzahl, lagerID);
                            break;
                        case 4:
                            System.out.print("Namen des neuen Lagerstandorts: ");
                            String lagerName = input.next();
                            Lagerbereich.insertStandort(c, lagerName);
                            break;
                        default:
                            System.err.println("Ungültiger Bereich für Aktion 'Anlegen'.");
                    }
                    break;

                case 2:
                    switch (bereich) {
                        case 1:
                            Kundenbereich.select(c);
                            break;
                        case 2:
                            Artikelbereich.select(c);
                            break;
                        case 3:
                            Bestellbereich.select(c);
                            break;
                        case 4:
                            Lagerbereich.selectStandorte(c);
                            break;
                        default:
                            System.err.println("Ungültiger Bereich für Aktion 'Anzeigen'.");
                    }
                    break;

                case 3:
                    switch (bereich) {
                        case 1:
                            System.out.print("ID des Kunden: ");
                            int kundenID_up = input.nextInt();
                            System.out.print("Neuer Name: ");
                            String neuerName = input.next();
                            System.out.print("Neue Email: ");
                            String neueEmail = input.next();
                            Kundenbereich.update(c, neuerName, neueEmail, kundenID_up);
                            break;
                        case 2:
                            System.out.print("ID des Artikels: ");
                            int artikelID_up = input.nextInt();
                            System.out.print("Neue Bezeichnung: ");
                            String neueBezeichnung = input.next();
                            System.out.print("Neuer Preis: ");
                            double neuerPreis = input.nextDouble();
                            Artikelbereich.update(c, neueBezeichnung, neuerPreis, artikelID_up);
                            break;
                        case 3:
                            System.out.print("ID der Bestellung: ");
                            int bestellungsID_up = input.nextInt();
                            System.out.print("Neue KundenID: ");
                            int neueKundenID = input.nextInt();
                            System.out.print("Neue ArtikelID: ");
                            int neueArtikelID = input.nextInt();
                            System.out.print("Neue LagerID: ");
                            int neueLagerID = input.nextInt();
                            System.out.print("Neue Anzahl: ");
                            int neueAnzahl = input.nextInt();
                            Bestellbereich.update(c, neueKundenID, neueArtikelID, neueAnzahl, neueLagerID, bestellungsID_up);
                            break;
                        case 4:
                            System.out.print("ID des Lagerstandorts: ");
                            int lagerID_up = input.nextInt();
                            System.out.print("Neuer Name des Lagerstandorts: ");
                            String neuerLagerName = input.next();
                            Lagerbereich.updateStandort(c, lagerID_up, neuerLagerName);
                            break;
                        default:
                            System.err.println("Ungültiger Bereich für Aktion 'Aktualisieren'.");
                    }
                    break;

                case 4:
                    switch (bereich) {
                        case 1:
                            System.out.print("ID des Kunden: ");
                            Kundenbereich.delete(c, input.nextInt());
                            break;
                        case 2:
                            System.out.print("ID des Artikels: ");
                            Artikelbereich.delete(c, input.nextInt());
                            break;
                        case 3:
                            System.out.print("ID der Bestellung: ");
                            Bestellbereich.delete(c, input.nextInt());
                            break;
                        case 4:
                            System.out.print("ID des Lagerstandorts: ");
                            Lagerbereich.deleteStandort(c, input.nextInt());
                            break;
                        default:
                            System.err.println("Ungültiger Bereich für Aktion 'Löschen'.");
                    }
                    break;

                case 5:
                    System.out.println("---------------------------------");
                    System.out.println("(1) Bestellungen eines Kunden anzeigen");
                    System.out.println("(2) Artikel mit kritischem Gesamtbestand anzeigen");
                    System.out.println("(3) TOP 10 Kunden nach Bestellwert anzeigen (NEU)");
                    System.out.print("Wahl: ");
                    switch (leseNaechsteZahl(input)) {
                        case 1:
                            System.out.print("ID des Kunden: ");
                            Bestellbereich.selectBestellungenVonKunden(c, input.nextInt());
                            break;
                        case 2:
                            Lagerbereich.selectKritischenBestand(c);
                            break;
                        case 3:
                            Bestellbereich.selectTop10KundenByBestellwert(c);
                            break;
                        default:
                            System.err.println("Ungültige Berichtswahl.");
                    }
                    break;

                case 6:
                    System.out.println("Programm wird beendet.");
                    break;

                default:
                    System.err.println("Falsche Eingabe! Nur Zahlen von 1-6 sind erlaubt.");
            }
        } catch (Exception e) {
            System.err.println("Ein Fehler ist aufgetreten: " + e.getMessage());
        }
    }

    public static void initializeDatabase(Connection c) throws SQLException {
        try {
            Kundenbereich.create(c);
            Artikelbereich.create(c);
            Lagerbereich.create(c);
            Bestellbereich.create(c);

            insertTestData(c);
        } catch (SQLException e) {
            throw new DataAccessException("Datenbank-Initialisierung fehlgeschlagen.", e);
        }
    }

    public static void insertTestData(Connection c) throws SQLException {
        // =========================================================================
        // 1. LAGERSTANDORTE ANLEGEN (20x)
        // IDs 1-20 werden erstellt. Die ersten 3 werden für Bestand/Bestellung genutzt.
        // =========================================================================
        Lagerbereich.insertStandort(c, "Hauptlager Berlin");   // ID 1
        Lagerbereich.insertStandort(c, "Lager Süd");          // ID 2
        Lagerbereich.insertStandort(c, "Lager Ost");           // ID 3
        Lagerbereich.insertStandort(c, "Lager West");          // ID 4
        Lagerbereich.insertStandort(c, "Versandzentrum 1");   // ID 5
        Lagerbereich.insertStandort(c, "Versandzentrum 2");   // ID 6
        Lagerbereich.insertStandort(c, "Lager Leipzig");       // ID 7
        Lagerbereich.insertStandort(c, "Lager Hamburg");       // ID 8
        Lagerbereich.insertStandort(c, "Lager München");       // ID 9
        Lagerbereich.insertStandort(c, "Lager Köln");          // ID 10
        Lagerbereich.insertStandort(c, "Lager Frankfurt");     // ID 11
        Lagerbereich.insertStandort(c, "Lager Stuttgart");     // ID 12
        Lagerbereich.insertStandort(c, "Lager Dortmund");      // ID 13
        Lagerbereich.insertStandort(c, "Lager Essen");         // ID 14
        Lagerbereich.insertStandort(c, "Lager Bremen");        // ID 15
        Lagerbereich.insertStandort(c, "Lager Dresden");       // ID 16
        Lagerbereich.insertStandort(c, "Lager Hannover");      // ID 17
        Lagerbereich.insertStandort(c, "Lager Nürnberg");      // ID 18
        Lagerbereich.insertStandort(c, "Lager Duisburg");      // ID 19
        Lagerbereich.insertStandort(c, "Lager Bochum");        // ID 20

        // =========================================================================
        // 2. KUNDEN ANLEGEN (20x)
        // IDs 1-20 werden erstellt.
        // =========================================================================
        Kundenbereich.insertData(c, "Anna Müller", "anna.m@mail.de");       // ID 1
        Kundenbereich.insertData(c, "Bernd Schmidt", "bernd.s@web.de");     // ID 2
        Kundenbereich.insertData(c, "Carla Weber", "carla.w@gmx.de");       // ID 3
        Kundenbereich.insertData(c, "Dirk Wagner", "dirk.w@firma.com");     // ID 4
        Kundenbereich.insertData(c, "Eva Fischer", "eva.f@online.net");     // ID 5
        Kundenbereich.insertData(c, "Franz Becker", "franz.b@cloud.de");    // ID 6
        Kundenbereich.insertData(c, "Gabi Hoffmann", "gabi.h@x.de");        // ID 7
        Kundenbereich.insertData(c, "Hans Schäfer", "hans.s@provider.at");  // ID 8
        Kundenbereich.insertData(c, "Ina Koch", "ina.k@ch.ch");             // ID 9
        Kundenbereich.insertData(c, "Jens Bauer", "jens.b@post.de");        // ID 10
        Kundenbereich.insertData(c, "Karin Wolf", "karin.w@mail.de");       // ID 11
        Kundenbereich.insertData(c, "Leo Schulz", "leo.s@web.de");          // ID 12
        Kundenbereich.insertData(c, "Mia Horn", "mia.h@gmx.de");            // ID 13
        Kundenbereich.insertData(c, "Nico Jung", "nico.j@firma.com");       // ID 14
        Kundenbereich.insertData(c, "Olga Brandt", "olga.b@online.net");    // ID 15
        Kundenbereich.insertData(c, "Paul Schröder", "paul.s@cloud.de");    // ID 16
        Kundenbereich.insertData(c, "Quinn Keller", "quinn.k@x.de");        // ID 17
        Kundenbereich.insertData(c, "Rosa Meyer", "rosa.m@provider.at");    // ID 18
        Kundenbereich.insertData(c, "Sven Fuchs", "sven.f@ch.ch");          // ID 19
        Kundenbereich.insertData(c, "Tina Vogel", "tina.v@post.de");        // ID 20

        // =========================================================================
        // 3. ARTIKEL ANLEGEN (20x)
        // IDs 1-20 werden erstellt. (KEIN Bestand!)
        // =========================================================================
        Artikelbereich.insertData(c, "Gaming Maus X10", 79.99);            // ID 1
        Artikelbereich.insertData(c, "Mechanische Tastatur Pro", 129.00);  // ID 2
        Artikelbereich.insertData(c, "USB-C Hub 8-in-1", 34.50);           // ID 3
        Artikelbereich.insertData(c, "Noise Cancelling Kopfhörer", 199.99);// ID 4
        Artikelbereich.insertData(c, "Smartwatch S7", 249.00);             // ID 5
        Artikelbereich.insertData(c, "Ersatzakku Power-L", 49.00);         // ID 6
        Artikelbereich.insertData(c, "Webcam 4K", 119.95);                 // ID 7
        Artikelbereich.insertData(c, "Grafiktablett M", 299.00);           // ID 8
        Artikelbereich.insertData(c, "Mini-PC Office", 450.00);            // ID 9
        Artikelbereich.insertData(c, "Monitorständer Ergonomic", 65.00);   // ID 10
        Artikelbereich.insertData(c, "HDMI Kabel Ultra", 15.00);           // ID 11
        Artikelbereich.insertData(c, "Ethernet Kabel 10m", 12.50);         // ID 12
        Artikelbereich.insertData(c, "SSD 1TB M.2", 89.90);                // ID 13
        Artikelbereich.insertData(c, "WLAN Repeater 3000", 55.00);         // ID 14
        Artikelbereich.insertData(c, "VR-Headset Light", 399.00);          // ID 15
        Artikelbereich.insertData(c, "Docking Station Pro", 175.00);       // ID 16
        Artikelbereich.insertData(c, "Beamer 1080p", 599.00);              // ID 17
        Artikelbereich.insertData(c, "Bluetooth Speaker Mini", 29.99);     // ID 18
        Artikelbereich.insertData(c, "Tischmikrofon Studio", 85.00);       // ID 19
        Artikelbereich.insertData(c, "Smart Home Starter Kit", 149.00);    // ID 20

        // =========================================================================
        // 4. ARTIKELBESTAND ANLEGEN (20x)
        // Jeder Artikel erhält mindestens einmal Bestand in einem der Lager (ID 1-3)
        // =========================================================================
        Lagerbereich.insertArtikelBestand(c, 1, 1, 50);  // Art 1 (Maus): Lager 1 (50)
        Lagerbereich.insertArtikelBestand(c, 2, 2, 80);  // Art 2 (Tastatur): Lager 2 (80)
        Lagerbereich.insertArtikelBestand(c, 3, 3, 120); // Art 3 (Hub): Lager 3 (120)
        Lagerbereich.insertArtikelBestand(c, 4, 1, 30);  // Art 4 (Kopfhörer): Lager 1 (30)
        Lagerbereich.insertArtikelBestand(c, 5, 2, 40);  // Art 5 (Smartwatch): Lager 2 (40)
        Lagerbereich.insertArtikelBestand(c, 6, 3, 200); // Art 6 (Akku): Lager 3 (200)
        Lagerbereich.insertArtikelBestand(c, 7, 1, 25);  // Art 7 (Webcam): Lager 1 (25)
        Lagerbereich.insertArtikelBestand(c, 8, 2, 15);  // Art 8 (Tablett): Lager 2 (15)
        Lagerbereich.insertArtikelBestand(c, 9, 3, 10);  // Art 9 (Mini-PC): Lager 3 (10)
        Lagerbereich.insertArtikelBestand(c, 10, 1, 70); // Art 10 (Ständer): Lager 1 (70)
        Lagerbereich.insertArtikelBestand(c, 11, 2, 150); // Art 11 (HDMI): Lager 2 (150)
        Lagerbereich.insertArtikelBestand(c, 12, 3, 250); // Art 12 (Ethernet): Lager 3 (250)
        Lagerbereich.insertArtikelBestand(c, 13, 1, 40); // Art 13 (SSD): Lager 1 (40)
        Lagerbereich.insertArtikelBestand(c, 14, 2, 60); // Art 14 (Repeater): Lager 2 (60)
        Lagerbereich.insertArtikelBestand(c, 15, 3, 5);  // Art 15 (VR-Headset): Lager 3 (5) - KRITISCH
        Lagerbereich.insertArtikelBestand(c, 16, 1, 35); // Art 16 (Docking): Lager 1 (35)
        Lagerbereich.insertArtikelBestand(c, 17, 2, 20); // Art 17 (Beamer): Lager 2 (20)
        Lagerbereich.insertArtikelBestand(c, 18, 3, 90); // Art 18 (Speaker): Lager 3 (90)
        Lagerbereich.insertArtikelBestand(c, 19, 1, 45); // Art 19 (Mikrofon): Lager 1 (45)
        Lagerbereich.insertArtikelBestand(c, 20, 2, 55); // Art 20 (Smart Home): Lager 2 (55)

        // =========================================================================
        // 5. BESTELLUNGEN ANLEGEN (20x)
        // Bestandsentnahme wird geprüft und aus dem jeweiligen Lager abgezogen.
        // =========================================================================
        Bestellbereich.insertData(c, 1, 1, 1, 1);    // Kunde 1 kauft 1x Art 1 (Lager 1)
        Bestellbereich.insertData(c, 2, 2, 2, 2);    // Kunde 2 kauft 2x Art 2 (Lager 2)
        Bestellbereich.insertData(c, 3, 3, 5, 3);    // Kunde 3 kauft 5x Art 3 (Lager 3)
        Bestellbereich.insertData(c, 4, 4, 1, 1);    // Kunde 4 kauft 1x Art 4 (Lager 1)
        Bestellbereich.insertData(c, 5, 5, 1, 2);    // Kunde 5 kauft 1x Art 5 (Lager 2)
        Bestellbereich.insertData(c, 6, 6, 10, 3);   // Kunde 6 kauft 10x Art 6 (Lager 3)
        Bestellbereich.insertData(c, 7, 7, 1, 1);    // Kunde 7 kauft 1x Art 7 (Lager 1)
        Bestellbereich.insertData(c, 8, 8, 2, 2);    // Kunde 8 kauft 2x Art 8 (Lager 2)
        Bestellbereich.insertData(c, 9, 9, 3, 3);    // Kunde 9 kauft 3x Art 9 (Lager 3)
        Bestellbereich.insertData(c, 10, 10, 4, 1);  // Kunde 10 kauft 4x Art 10 (Lager 1)
        Bestellbereich.insertData(c, 11, 11, 10, 2); // Kunde 11 kauft 10x Art 11 (Lager 2)
        Bestellbereich.insertData(c, 12, 12, 20, 3); // Kunde 12 kauft 20x Art 12 (Lager 3)
        Bestellbereich.insertData(c, 13, 13, 5, 1);  // Kunde 13 kauft 5x Art 13 (Lager 1)
        Bestellbereich.insertData(c, 14, 14, 1, 2);  // Kunde 14 kauft 1x Art 14 (Lager 2)
        Bestellbereich.insertData(c, 15, 15, 2, 3);  // Kunde 15 kauft 2x Art 15 (Lager 3)
        Bestellbereich.insertData(c, 16, 16, 1, 1);  // Kunde 16 kauft 1x Art 16 (Lager 1)
        Bestellbereich.insertData(c, 17, 17, 5, 2);  // Kunde 17 kauft 5x Art 17 (Lager 2)
        Bestellbereich.insertData(c, 18, 18, 10, 3); // Kunde 18 kauft 10x Art 18 (Lager 3)
        Bestellbereich.insertData(c, 19, 19, 1, 1);  // Kunde 19 kauft 1x Art 19 (Lager 1)
        Bestellbereich.insertData(c, 20, 20, 1, 2);  // Kunde 20 kauft 1x Art 20 (Lager 2)
    }

    public static void ausgabe() {
        System.out.println("\n====== HAUPTMENÜ ======");
        System.out.println("Aktion wählen:");
        System.out.println("(1) ANLEGEN (Kunde, Artikel, Bestellung, Lagerstandort)");
        System.out.println("(2) ANZEIGEN (Alle Daten)");
        System.out.println("(3) AKTUALISIEREN (Kunde, Artikel, Bestellung, Lagerstandort)");
        System.out.println("(4) LÖSCHEN (Kunde, Artikel, Bestellung, Lagerstandort)");
        System.out.println("(5) BERICHTE/LAGER (Spezialabfragen)");
        System.out.println("(6) BEENDEN");
        System.out.print("Geben Sie eine Zahl ein: ");
    }
}