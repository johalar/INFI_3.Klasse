package infi.examples.KundenArtikelBestellungsbeispiel;

import java.sql.DriverManager;
import java.util.Scanner;
import java.sql.Connection;
import java.sql.SQLException;

public class Main {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/kundenartikelbestellungen?user=root&useSSL=false&serverTimezone=UTC";

    public static void main(String[] args){
        try (Scanner input = new Scanner(System.in); Connection c = DriverManager.getConnection(DB_URL)) {
            initializeDatabase(c);
            boolean schleife = true;
            while(schleife){
                ausgabe();
                switch (input.nextInt()) {
                    case 1:
                        System.out.print("Namen des Kunden: ");
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
                    case 5:
                        Artikelbereich.selectArtikel(c);
                    case 6:
                        Bestellbereich.selectBestellungen(c);
                    case 7:
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
            Kundenbereich.insertIntoKunden(c, "Artikel 1", "Artikel 2");
            Artikelbereich.insertIntoArtikel(c, "Bezeichnung 1", 20000);
            Bestellbereich.insertIntoBestellungen(c, 1,1,20);
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public static void ausgabe(){
        System.out.println("\n====== KUNDEN-ARTIKEL-BESTELLUNGEN ======");
        System.out.println("(1) Kunde anlegen");
        System.out.println("(2) Artikel anlegen");
        System.out.println("(3) Bestellung anlegen");
        System.out.println("(4) Kunden anzeigen");
        System.out.println("(5) Artikel anzeigen");
        System.out.println("(6) Bestellungen anzeigen");
        System.out.println("(7) Beenden");
        System.out.print("Geben sie eine Zahl ein: ");
    }
}
