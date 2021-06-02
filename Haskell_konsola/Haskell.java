package Haskell_konsola;

import java.util.*;

import java.util.stream.Collectors;

import java.io.*;

public class Haskell {

    // ================================== konfiguracja ==================================
    public static String adresPliku = "C:\\Users\\AM IT Support\\OneDrive - AM IT Support Ltd\\PJA\\TAK\\Projects\\example_input.txt";

    static Map<String, String> hmPolecenia = new HashMap<>();

    // ================================== wczytywanie pliku ==================================

    //  przerobic na wczytywanie z konsoli!!! (. ͠o ⍨ ͡O.)   

    public static ArrayList<String> wczytajPolecenia(

    ) throws IOException {
        ArrayList<String> listLiniePliku = new ArrayList<String>();

        BufferedReader brKonsola = new BufferedReader(new InputStreamReader(System.in));

        String linia;

        do {
            linia = brKonsola.readLine();
            listLiniePliku.add(linia);
        } while (!linia.toLowerCase().contains("main"));

        brKonsola.close();

        return listLiniePliku;
    }

    public static ArrayList<String> wczytajPlik(

            String adres

    ) throws FileNotFoundException {
        ArrayList<String> listLiniePliku = new ArrayList<String>();

        Scanner scannerAdresPliku = new Scanner(new File(adres));
        while (scannerAdresPliku.hasNextLine()) {
            listLiniePliku.add(scannerAdresPliku.nextLine());
        }

        scannerAdresPliku.close();

        return listLiniePliku;
    }

    // Wykonanie programu

    public static void main(String[] args) throws FileNotFoundException, IOException {

        ArrayList<String> listLiniePliku = wczytajPlik(adresPliku);

        for (int i = 0; i < listLiniePliku.size(); i++)
            if (listLiniePliku.get(i) != "")
                hmPolecenia.put(listLiniePliku.get(i).split("=")[0].trim(), listLiniePliku.get(i).split("=")[1].trim());

        String a;
        a = hmPolecenia.get("main").replace("print ", "");
        System.out.println(parsuj(a));

    }

    public static int[] indeksyNawiasowKwad(String str) {
        int[] wynik = new int[2];
        wynik[0] = str.indexOf(91);

        int dopasowanieNawiasu = 0, numerIndeksu = 0;
        if (wynik[0] > -1) {
            char[] ch = str.toCharArray();
            for (char c : ch) {
                if (c == 91) {
                    dopasowanieNawiasu++;
                }
                if (c == 93) {
                    dopasowanieNawiasu--;
                    if (dopasowanieNawiasu == 0)
                        break;
                }
                numerIndeksu++;
            }
            wynik[1] = numerIndeksu;
        }

        return wynik;
    }

    public static int[] indeksyNawiasow(String str) {
        int[] wynik = new int[2];
        wynik[0] = str.indexOf(40);

        int dopasowanieNawiasu = 0, numerIndeksu = 0;
        if (wynik[0] > -1) {
            char[] ch = str.toCharArray();
            for (char c : ch) {
                if (c == 40) {
                    dopasowanieNawiasu++;
                }
                if (c == 41) {
                    dopasowanieNawiasu--;
                    if (dopasowanieNawiasu == 0)
                        break;
                }
                numerIndeksu++;
            }
            wynik[1] = numerIndeksu;
        }

        return wynik;
    }

    public static boolean jestLiczba(String s) {
        if (s == null) {
            return false;
        }
        try {
            Float.parseFloat(s);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    public static String zamienArgumenty(String s, String arg, String wartoscArg) {

        String[] patterns = {

                //0
                " " + arg + " ",
                //1
                "(" + arg + " ",
                //2
                " " + arg + ")"

        };

        if (s.substring(0, arg.length() + 1).equals(arg + " "))
            s = s.replaceFirst(arg, wartoscArg);

        if (s.substring(s.length() - (arg.length() + 1), s.length()).equals(" " + arg))
            s = s.replaceFirst("(?s)" + arg + "(?!.*?" + arg + ")", wartoscArg);

        s = s.replace(patterns[0], " " + wartoscArg + " ");

        s = s.replace(patterns[1], "(" + wartoscArg + " ");

        s = s.replace(patterns[2], " " + wartoscArg + ")");

        return s;
    }

    public static int lambdaIleArg(String str) {
        return str.substring(str.indexOf("\\") + 1, str.indexOf("->")).trim().split(" ").length;
    }

    public static String lambdaNumerujArg(String s) {

        HashSet<String> argNumbers = new HashSet<>();
        while (s.indexOf("{arg") > -1) {
            argNumbers.add(s.substring(s.indexOf("{arg"), s.indexOf("}")));
            s = s.substring(s.indexOf("}") + 1);
        }
        return "{arg" + argNumbers.size() + "}";
    }

    public static String lambdaParsuj(String str, String[] wartosciArg) {
        String[] listaArgumentow = str.substring(str.indexOf("\\") + 1, str.indexOf("->")).trim().split(" ");
        str = str.substring(str.indexOf("->") + 2).trim();

        // zamien nazwe funkcji i nastepujace argumenty na operacje
        for (int i = 0; i < listaArgumentow.length; i++) {
            if (wartosciArg[i].equals("{arg}")) {
                wartosciArg[i] = lambdaNumerujArg(str);
            }
            str = "[" + zamienArgumenty(str, listaArgumentow[i], wartosciArg[i]) + "]";
        }

        return str;
    }

    public static boolean tylkoLiczbyIInfixy(String s) {
        String[] sArr = s.trim().split(" ");
        String[] infixy = { "-", "+", "/", "*", "`div`" };
        for (int i = 0; i < sArr.length; i++) {
            if (!(jestLiczba(sArr[i]) || Arrays.stream(infixy).anyMatch(sArr[i]::equals))) {
                return false;
            }
        }
        return true;
    }

    public static boolean czyLambda(String s) {
        boolean[] b = { false };
        if (hmPolecenia.get(s) != null)
            if (hmPolecenia.get(s).contains("\\"))
                b[0] = true;
        hmPolecenia.entrySet().stream().filter(entry -> entry.getKey().startsWith(s)).forEach(e -> {
            if (e.getValue().contains("\\"))
                b[0] = true;
        });
        return b[0];
    }

    public static String[] addArrays(String[] s1, String[] s2) {
        if (s1 == null)
            return s2;
        if (s2 == null)
            return s1;
        String[] s3 = new String[s1.length + s2.length];
        System.arraycopy(s1, 0, s3, 0, s1.length);
        System.arraycopy(s2, 0, s3, s1.length, s2.length);
        return s3;
    }

    public static String parsuj(String str) {

        str = str.trim();

        int[] indeksyNawiasow = indeksyNawiasow(str);

        if (indeksyNawiasow[0] > -1) {
            String subStr = str.substring(indeksyNawiasow[0] + 1, indeksyNawiasow[1]);
            str = parsuj(str.substring(0, indeksyNawiasow[0]) + parsuj(subStr) + str.substring(indeksyNawiasow[1] + 1));
        }

        if (hmPolecenia.get(str) != null) {
            // troche nieeleganckie wylapywanie overloadow ale przy naszych zalozeniach powinno dzialac...
            str = str.replace(str, parsuj(hmPolecenia.get(str)));
        }

        String[] slowaPolecenia = (indeksyNawiasowKwad(str)[0] == -1) ? str.split(" ")
                : addArrays(
                        addArrays(
                                (str.substring(0, indeksyNawiasowKwad(str)[0]).length() > 0)
                                        ? str.substring(0, indeksyNawiasowKwad(str)[0]).split(" ")
                                        : null,
                                new String[] {
                                        str.substring(indeksyNawiasowKwad(str)[0], indeksyNawiasowKwad(str)[1]) }),
                        (str.substring(indeksyNawiasowKwad(str)[1], str.length()).length() > 0)
                                ? str.substring(indeksyNawiasowKwad(str)[1], str.length()).split(" ")
                                : null);
        for (int i = 0; i < slowaPolecenia.length; i++) {
            String s = slowaPolecenia[i].toString();

            if (czyLambda(s)) { //lambda
                if (hmPolecenia.get(s) != null) {
                    String[] wartosciArg = new String[lambdaIleArg(hmPolecenia.get(s))];
                    String target = s;
                    for (int j = 0; j < wartosciArg.length; j++) {
                        try {
                            wartosciArg[j] = slowaPolecenia[i + j + 1];
                        } catch (ArrayIndexOutOfBoundsException e) {
                            wartosciArg[j] = "{arg}";
                        }
                        target += " " + wartosciArg[j];
                    }
                    str = str.replace(target, parsuj(lambdaParsuj(hmPolecenia.get(s), wartosciArg)));
                } else // tu oprocz lambda abstrakcji mamy rowniez "normalne" argumenty funkcji jesli wystepuja overloady moga nastapic dodatkowe komplikacje
                {

                    Map<String, String> funkcje = hmPolecenia.entrySet().stream()
                            .filter(entry -> entry.getKey().startsWith(s))
                            .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));

                    for (Map.Entry<String, String> entry : funkcje.entrySet()) {

                        String[] deklaracjaFunkcji = entry.getKey().split(" ");

                        boolean jestLiczba = true;
                        int iloscArgumentow = deklaracjaFunkcji.length;
                        String value = entry.getValue();

                        String[] wartosciArg = new String[lambdaIleArg(value)];

                        String target = deklaracjaFunkcji[0];
                        for (int j = 1; j < iloscArgumentow; j++) {
                            jestLiczba = jestLiczba(deklaracjaFunkcji[j]);
                            if (!jestLiczba) {
                                value = zamienArgumenty(value, deklaracjaFunkcji[j], slowaPolecenia[j]);
                                target += " " + slowaPolecenia[j];
                            }
                        }

                        for (int j = 0; j < wartosciArg.length; j++) {
                            try {
                                wartosciArg[j] = slowaPolecenia[i + j + iloscArgumentow + 1];
                            } catch (ArrayIndexOutOfBoundsException e) {
                                wartosciArg[j] = "{arg}";
                            }
                        }
                        if (!jestLiczba)
                            str = str.replace(target, parsuj(lambdaParsuj(value, wartosciArg)));
                    }
                }
            } else if (hmPolecenia.get(s) != null) {
                str = str.replace(s, parsuj(hmPolecenia.get(s)));

            } else if (hmPolecenia.entrySet().stream().filter(entry -> entry.getKey().startsWith(s)).count() > 0) {
                Map<String, String> funkcje = hmPolecenia.entrySet().stream()
                        .filter(entry -> entry.getKey().startsWith(s))
                        .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));

                for (Map.Entry<String, String> entry : funkcje.entrySet()) {

                    String[] deklaracjaFunkcji = entry.getKey().split(" ");

                    boolean jestLiczba = true;
                    int iloscArgumentow = deklaracjaFunkcji.length;
                    String value = entry.getValue();
                    String target = deklaracjaFunkcji[0];
                    for (int j = 1; j < iloscArgumentow; j++) {
                        jestLiczba = jestLiczba(deklaracjaFunkcji[j]);
                        if (!jestLiczba) {
                            value = zamienArgumenty(value, deklaracjaFunkcji[j], slowaPolecenia[j]);
                            target += " " + slowaPolecenia[j];
                        }
                    }
                    if (!jestLiczba)
                        str = parsuj(str.replace(target, value));

                }

            }

        }

        //od lewej do prawej, jesli brakuje nawiasow ( ciut niematematycznie ¯\_( ◡́ - ◡̀)_/¯ )
        if (slowaPolecenia.length > 3)

        {
            String subStr = str;
            while (subStr.chars().filter(ch -> ch == ' ').count() > 2) {
                subStr = subStr.substring(0, subStr.lastIndexOf(" "));
            }
            str = parsuj(subStr) + str.substring(subStr.length());
        }

        if (tylkoLiczbyIInfixy(str)) {
            if (str.indexOf("+") > -1) {
                str = Float.toString(
                        Float.parseFloat(str.split("[+]")[0].trim()) + Float.parseFloat(str.split("[+]")[1].trim()));

            }

            if (str.indexOf(" - ") > -1) {
                str = Float.toString(Float.parseFloat(str.split(" [-] ")[0].trim())
                        - Float.parseFloat(str.split(" [-] ")[1].trim()));

            }

            if (str.indexOf("*") > -1) {
                str = Float.toString(
                        Float.parseFloat(str.split("[*]")[0].trim()) * Float.parseFloat(str.split("[*]")[1].trim()));

            }

            if (str.indexOf("/") > -1) {
                str = Float.toString(
                        Float.parseFloat(str.split("[/]")[0].trim()) / Float.parseFloat(str.split("[/]")[1].trim()));

            }

            if (str.indexOf("`div`") > -1) {
                str = Integer.toString(Integer.parseInt(str.split("`div`")[0].trim())
                        / Integer.parseInt(str.split("`div`")[1].trim()));

            }
        }

        if (jestLiczba(str) && Float.parseFloat(str) % 1 == 0) {
            str = str.replace(".0", "");
        }
        return str;
    }

}
