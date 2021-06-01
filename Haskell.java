import java.util.*;

import java.util.stream.Collectors;

import java.io.*;

public class Haskell {

    // ================================== konfiguracja ==================================
    public static String adresPliku = "C:\\Users\\AM IT Support\\OneDrive - AM IT Support Ltd\\PJA\\TAK\\Projects\\example_input.txt";

    static Map<String, String> hmPolecenia = new HashMap<>();

    public static int krok = 0;

    // ================================== wczytywanie pliku ==================================

    //  przerobic na wczytywanie z konsoli!!! (. ͠o ⍨ ͡O.)   

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

    // Wykonanie programu

    public static void main(String[] args) throws FileNotFoundException, IOException {

        ArrayList<String> listLiniePliku =
                //wczytajPolecenia();
                wczytajPlik(adresPliku);

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
        String[] sArr = s.trim().replace("[", "").replace("]", "").split(" ");
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

    public static ArrayList<String> lambdaHoldery(String str) {

        ArrayList<String> aList = new ArrayList<String>();

        if (str.indexOf("{arg") == -1)
            return aList;

        HashSet<String> argNumbers = new HashSet<>();
        while (str.indexOf("{arg") > -1) {
            argNumbers.add(str.substring(str.indexOf("{arg"), str.indexOf("}")));
            str = str.substring(str.indexOf("}") + 1);
        }

        for (int i = 0; i < argNumbers.size(); i++)
            aList.add("{arg" + i + "}");

        return aList;
    }

    public static String[] slowaPolecenia(String str) {
        String[] slowaPolecenia = (indeksyNawiasowKwad(str)[0] == -1) ? str.split(" ")
                : addArrays(
                        addArrays(
                                (str.substring(0, indeksyNawiasowKwad(str)[0]).length() > 0)
                                        ? str.substring(0, indeksyNawiasowKwad(str)[0]).trim().split(" ")
                                        : null,
                                new String[] {
                                        str.substring(indeksyNawiasowKwad(str)[0], indeksyNawiasowKwad(str)[1] + 1) }),
                        (str.substring(indeksyNawiasowKwad(str)[1] + 1, str.length()).length() > 0)
                                ? str.substring(indeksyNawiasowKwad(str)[1] + 1, str.length()).trim().split(" ")
                                : null);
        return slowaPolecenia;
    }

    public static String parsuj(String str) {
        return parsuj(str, new String[0]);
    }

    public static String parsuj(String str, String[] strArr) {

        str = str.trim();
        System.out.println("krok [\t" + ++krok + "\t] parsuje \t" + str);
        int krokCache = krok;
        System.out.println(Arrays.toString(strArr));
        int[] indeksyNawiasow = indeksyNawiasow(str);

        ArrayList<String> lambdaHoldery = lambdaHoldery(str);

        if (indeksyNawiasow[0] > -1 && lambdaHoldery.size() == 0) {
            String subStr = str.substring(indeksyNawiasow[0] + 1, indeksyNawiasow[1]);
            str = parsuj(str.substring(0, indeksyNawiasow[0]) + parsuj(subStr, strArr)
                    + str.substring(indeksyNawiasow[1] + 1), strArr);
        }

        if (hmPolecenia.get(str) != null) {
            // troche nieeleganckie wylapywanie overloadow ale przy naszych zalozeniach powinno dzialac...
            str = str.replace(str, parsuj(hmPolecenia.get(str), strArr));
        }

        String[] slowaPolecenia = slowaPolecenia(str);
        /*
        if (lambdaHoldery.size() > 0) {
            for (int i = 0; i < slowaPolecenia.length; i++) {
                if (lambdaHoldery(slowaPolecenia[i]).size() > 0 && i + 1 < slowaPolecenia.length) {
                    slowaPolecenia[i] = zamienArgumenty(slowaPolecenia[i], lambdaHoldery(slowaPolecenia[i]).get(0),
                            slowaPolecenia[i + 1]);
                }
            }
        }
        /*
        if (lambdaHoldery.size() > 0) {
        
            for (int j = 0; j < lambdaHoldery.size(); j++)
                try {
                    zamienArgumenty(str, lambdaHoldery.get(j), slowaPolecenia[i + j + 1]);
                } catch (ArrayIndexOutOfBoundsException e) {
                }
        
        }
        */
        if (str.indexOf("[") != 0) {
            str = str.replace("[", "(").replace("]", ")");
            for (int i = 0; i < slowaPolecenia.length; i++) {
                slowaPolecenia[i] = slowaPolecenia[i].replace("[", "(").replace("]", ")");
            }
        }
        for (int i = 0; i < slowaPolecenia.length; i++) {
            String s = slowaPolecenia[i].toString();

            System.out.println("\t\t\t\t\t\t" + s);

            String[] newStrArr = new String[slowaPolecenia.length - (i + 1)];
            System.arraycopy(slowaPolecenia, i + 1, newStrArr, 0, slowaPolecenia.length - (i + 1));
            strArr = addArrays(strArr, newStrArr);

            if (czyLambda(s)) { //lambda
                if (hmPolecenia.get(s) != null) {
                    String[] wartosciArg = new String[lambdaIleArg(hmPolecenia.get(s))];
                    String target = s;
                    for (int j = 0, k = 0; j < wartosciArg.length; j++) {
                        try {
                            wartosciArg[j] = slowaPolecenia[i + j + 1];
                        } catch (ArrayIndexOutOfBoundsException e) {
                            if (strArr.length - 1 < k)
                                wartosciArg[j] = "{arg}";
                            else {
                                wartosciArg[j] = strArr[k];
                                newStrArr = new String[strArr.length - 1];
                                System.arraycopy(strArr, k + 1, newStrArr, 0, newStrArr.length);
                                strArr = newStrArr;
                                System.out.println(Arrays.toString(strArr));
                                k++;
                            }
                        }
                        target += " " + wartosciArg[j];

                    }
                    i += wartosciArg.length - 1;
                    str = str.replace(target, parsuj(lambdaParsuj(hmPolecenia.get(s), wartosciArg), strArr));
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

                        for (int j = 0, k = 0; j < wartosciArg.length; j++) {
                            try {
                                wartosciArg[j] = slowaPolecenia[i + j + iloscArgumentow];
                            } catch (ArrayIndexOutOfBoundsException e) {
                                if (/*strArr.length - 1 < k*/true)
                                    wartosciArg[j] = "{arg}";
                                else {
                                    wartosciArg[j] = strArr[k];
                                    newStrArr = new String[strArr.length - 1];
                                    System.arraycopy(strArr, k + 1, newStrArr, 0, newStrArr.length);
                                    strArr = newStrArr;
                                    System.out.println(Arrays.toString(strArr));
                                    k++;
                                }
                            }
                        }
                        i += iloscArgumentow - 1;
                        if (!jestLiczba)
                            str = str.replace(target, parsuj(lambdaParsuj(value, wartosciArg), strArr));
                    }
                }
            } else if (hmPolecenia.get(s) != null) {
                str = str.replace(s, parsuj(hmPolecenia.get(s), strArr));

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
                    i += iloscArgumentow - 1;
                    if (!jestLiczba)
                        str = parsuj(str.replace(target, value), strArr);

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
            str = parsuj(subStr, strArr) + str.substring(subStr.length());
        }

        System.out.println("[[[\t " + krokCache + "\t]]]");

        if (tylkoLiczbyIInfixy(str)) {

            str = str.replace("[", "").replace("]", "");

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
