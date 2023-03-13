import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Scanner;

public class UI {

    public static void init(DB db){
        if(db != null){
            int option = -1;

            Scanner scanner = new Scanner(System.in);
            scanner.useDelimiter("\n");

            ResultSet result = null;

            while (option != 0) {
                showMenu();
                try {
                    option = scanner.nextInt();

                    switch (option){
                        case 0:
                            break;
                        case 1:
                            //Parodomi visi klientai
                            result = db.selectAllKlientas();
                            if(result != null)
                                displayResultSet(result);
                            else
                                System.out.println("Klientu nera");
                            break;
                        case 2:
                            //parodomi uzsakymai ir uzsakancio klianto id
                            result = db.selectKlientasUzsakymas();
                            if(result != null)
                                displayResultSet(result);
                            else
                                System.out.println("Uzsakymu nera");
                            break;
                        case 3:
                            //kuriamas naujas uzsakymas
                            System.out.println("Iveskite Kliento id kuriam kuriamas uzsakamas. Klientai:");
                            result = db.selectAllKlientas();
                            if(result != null)
                                displayResultSet(result);
                            else
                                System.out.println("Klientu nera");
                            System.out.println("Iveskite kliento id:");
                            int id = scanner.nextInt();
                            db.insertUzsakymas(id);
                            break;
                        case 4:
                            //Pakeiciamas uzsakymo apmokejimo statusas
                            result = db.selectKlientasUzsakymas();
                            if(result != null)
                                displayResultSet(result);
                            else
                                System.out.println("Uzsakymu nera");
                            System.out.println("Iveskite uzsakymo nr, kurio apmokejimo statusa norite keisti:");
                            int nr = scanner.nextInt();
                            System.out.println("Iveskite statusa 0 - neapmoketas 1 - apmoketas");
                            int status = scanner.nextInt();
                            if (status == 0)
                                db.updateUzsakymoStatusas(false, nr);
                            else
                                db.updateUzsakymoStatusas(true, nr);
                            break;
                        case 5:
                            result = db.selectAllPrekes();
                            if(result != null)
                                displayResultSet(result);
                            else
                                System.out.println("Prekiu nera");
                            break;
                        case 6:
                            System.out.println("Iveskite prekes koda:");
                            int kodas = scanner.nextInt();
                            System.out.println("Iveskite Prekes pavadinima");
                            scanner.nextLine();
                            String pavadinimas = scanner.nextLine();
                            System.out.println("Iveskite Prekes tipa");
                            String tipas = scanner.nextLine();
                            System.out.println("Iveskite Prekes kaina");
                            float kaina = scanner.nextFloat();
                            System.out.println("Iveskite Prekes aukstis");
                            int aukstis = scanner.nextInt();
                            System.out.println("Iveskite Prekes plotis");
                            int plotis = scanner.nextInt();
                            System.out.println("Iveskite Prekes skresmuo");
                            int skersmuo = scanner.nextInt();
                            System.out.println("Iveskite Prekes likutis");
                            int likutis = scanner.nextInt();

                            db.insertPreke(kodas, pavadinimas, tipas, kaina, aukstis, plotis, skersmuo, likutis);
                            break;
                        case 7:
                            result = db.selectAllPrekes();
                            if(result != null)
                                displayResultSet(result);
                            else
                                System.out.println("Prekiu nera");
                            System.out.println("Iveskite prekes koda, kurios likuti norite keisti:");
                            int pKodas = scanner.nextInt();
                            System.out.println("Iveskite likuti:");
                            int pLikutis = scanner.nextInt();
                            db.updatePrekesLikutis(pLikutis, pKodas);
                            break;
                        case 8:
                            result = db.selectAllPrekes();
                            if(result != null)
                                displayResultSet(result);
                            else
                                System.out.println("Prekiu nera");
                            System.out.println("Iveskite prekes koda, kuria norite istrinit:");
                            int dKodas = scanner.nextInt();
                            db.deletePreke(dKodas);
                            break;
                        case 9:
                            System.out.println("Iveskite Padangos tipa(vasarine, universali, ziemine)");
                            scanner.nextLine();
                            String type = scanner.nextLine();
                            result = db.selectPrekeTipas(type);
                            if(result != null)
                                displayResultSet(result);
                            else
                                System.out.println("Prekiu nera");
                            break;
                        case 10:
                            result = db.selectKlientasUzsakymas();
                            if(result != null)
                                displayResultSet(result);
                            else
                                System.out.println("Uzsakymu nera");
                            System.out.println("Iveskite uzsakymo numeri kurio krepselio elementus norite pamatyti:");
                            nr = scanner.nextInt();
                            result = db.selectUzsakymoEl(nr);
                            if(result != null)
                                displayResultSet(result);
                            else
                                System.out.println("Krepselis tuscias");
                            break;
                        case 11:
                            result = db.selectKlientasUzsakymas();
                            if(result != null)
                                displayResultSet(result);
                            else
                                System.out.println("Uzsakymu nera");
                            System.out.println("Iveskite uzsakymo numeri i kuri norite iterpti uzsakyma:");
                            int uzsakymoNr = scanner.nextInt();
                            result = db.selectAllPrekes();
                            if(result != null)
                                displayResultSet(result);
                            else
                                System.out.println("Prekiu nera");
                            System.out.println("Iveskite norimos prekes koda:");
                            int prekesKodas = scanner.nextInt();
                            System.out.println("Iveskite norima kieki:");
                            int kiekis = scanner.nextInt();
                            db.insertUzsakymoEl(uzsakymoNr, prekesKodas, kiekis);
                            break;
                        default:
                            System.out.println("Tokio pasirinkimo nera!");
                            break;

                    }

                }catch (SQLException sqle){
                    System.out.println(sqle);
                }
                pause(scanner);
            }
            scanner.close();
        }
    }

    public static void showMenu() {
        System.out.println("0. Baigti darba");
        System.out.println("");
        System.out.println("-------------Klientas-------------------");
        System.out.println("1. Parodyti visus klientus");
        System.out.println("");
        System.out.println("-------------Uzsakymas------------------");
        System.out.println("2. Parodyti Uzsakymus");
        System.out.println("3. Sukurti nauja uzsakyma");
        System.out.println("4. Pakeisti uzsakymo apmokejimo statusa");
        System.out.println("");
        System.out.println("-------------Prekes--------------------");
        System.out.println("5. Parodyti visas prekes");
        System.out.println("6. Iterpti nauja preke");
        System.out.println("7. Koreguoti prekes likuti");
        System.out.println("8. Istrinti Preke");
        System.out.println("9. Parodyti prekes pagal pasirinkta kategorija");
        System.out.println("");
        System.out.println("-------------Uzsakymo Elementai---------------");
        System.out.println("10. Pamatyti krepselio elementus");
        System.out.println("11. Iterpti uzsakymo elementa");


    }

    private static void pause(Scanner sin) {
        sin.nextLine();
        System.out.println("Paspauskite bet koki mygtuka testi darbui...");
        sin.nextLine();
        sin.reset();
    }

    public static void displayResultSet (ResultSet rs) throws SQLException {
        if (rs != null) {
            ResultSetMetaData md = rs.getMetaData ( );
            int ncols = md.getColumnCount ( );
            int nrows = 0;
            int[ ] width = new int[ncols + 1];       // masyvas plociams stulpeliu kaupti
            StringBuilder b = new StringBuilder ( ); // bufferi'is saugantis bar line'a

            // apskaiciuojami plociai stulpeliu
            for (int i = 1; i <= ncols; i++)
            {
                width[i] = md.getColumnDisplaySize (i);
                if (width[i] < md.getColumnName (i).length ( ))
                    width[i] = md.getColumnName (i).length ( );
                // isNullable( ) grazina 1/0, o ne true/false
                if (width[i] < 4 && md.isNullable (i) != 0)
                    width[i] = 4;
            }

            // konstruojam +---+---... linija
            b.append ("+");
            for (int i = 1; i <= ncols; i++)
            {
                for (int j = 0; j < width[i]; j++)
                    b.append ("-");
                b.append ("+");
            }

            // spausdinam bar line'a, stulpelio header'i, bar line'a
            System.out.println (b.toString ( ));
            System.out.print ("|");
            for (int i = 1; i <= ncols; i++)
            {
                System.out.print (md.getColumnName (i));
                for (int j = md.getColumnName (i).length ( ); j < width[i]; j++)
                    System.out.print (" ");
                System.out.print ("|");
            }
            System.out.println ( );
            System.out.println (b.toString ( ));

            // spausdinam result set'o turini

            while (rs.next())
            {
                ++nrows;
                System.out.print ("|");
                for (int i = 1; i <= ncols; i++)
                {
                    String s = rs.getString(i);
                    if (rs.wasNull())
                        s = "Nera";
                    System.out.print (s);
                    for (int j = s.length(); j < width[i]; j++)
                        System.out.print(" ");
                    System.out.print("|");
                }
                System.out.println();
            }

            // spausdinam bar line'a, ir eiluciu skaiciu
            System.out.println (b.toString ( ));
            System.out.println (nrows + " irasai");
        } else {
            throw new SQLException("Tokiu duomenu nera!");
        }
    }
}
