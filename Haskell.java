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
        if (s.length() > arg.length()) {
            if (s.substring(0, arg.length() + 1).equals(arg + " "))
                s = s.replaceFirst(arg, wartoscArg);

            if (s.substring(s.length() - (arg.length() + 1), s.length()).equals(" " + arg))
                s = s.replaceFirst("(?s)" + arg + "(?!.*?" + arg + ")", wartoscArg);
        } else if (s.equals(arg)) {
            s = wartoscArg;
        }
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

    public static String lambdaParsuj(String polecenie, TreeMap<String, String> args,
            TreeMap<String, String> lambdaArgs) {

        System.out.println(

                "<---\nfunkcja \t" + "\n" + "polecenie \t" + polecenie + "\n" + "args \t" + args.keySet() + " "
                        + args.values() + "\n" + "lambdaArgs \t" + lambdaArgs.keySet() + " " + lambdaArgs.values()
                        + "\n--->"

        );

        if (indeksyNawiasow(polecenie)[0] == 0 && indeksyNawiasow(polecenie)[1] == polecenie.length() - 1)
            polecenie = polecenie.substring(1, polecenie.length() - 1);

        if (args.size() > 0) {
            for (Map.Entry<String, String> arg : args.entrySet()) {

                polecenie = zamienArgumenty(polecenie, arg.getKey(), arg.getValue());
            }
        }

        //zanim to trzeba posprawdzac czy funkcja sie zaczyna od tego!
        polecenie = polecenie.substring(0, polecenie.indexOf("\\"))
                + polecenie.substring(polecenie.indexOf("->") + 2).trim();
        if (polecenie.indexOf("\\") > -1) {
            for (int j = 0; j < lambdaIleArg(polecenie); j++) {
                lambdaArgs.put(
                        polecenie.substring(polecenie.indexOf("\\") + 1, polecenie.indexOf("->")).trim().split(" ")[j],
                        "{arg}");
            }

            polecenie = lambdaParsuj(polecenie, args, lambdaArgs);
        }

        System.out.println(polecenie);

        List<String> polList = new ArrayList<String>(
                Arrays.asList(polecenie.replace("[", "  ").replace("]", "  ").split("  ")));

        for (int l = 0; l < polList.size(); l++) {
            if (!polList.get(l).equals(""))
                if (polList.get(l).charAt(0) == ' ')
                    polList.set(l, polList.get(l).replaceFirst(" ", ":"));
        }
        polecenie = polList.stream().collect(Collectors.joining(" "));
        polList = new ArrayList<String>(Arrays.asList(polecenie.split(":")));

        System.out.println(polList);

        for (int i = polList.size() - 1; i >= 0; i--) {
            ArrayList<String> argsList = lambdaHoldery(polList.get(i));
            for (int j = 0; j < argsList.size(); j++) {
                try {
                    polList.set(i, polList.get(i).replace(argsList.get(j), polList.get(i + j + 1)));
                    polList.remove(i + j + 1);
                } catch (Exception e) {
                    //TODO: handle exception
                }
            }

        }
        polecenie = polList.stream().collect(Collectors.joining(" "));

        for (Map.Entry<String, String> larg : lambdaArgs.entrySet()) {

            String largv = (larg.getValue().equals("{arg}")) ? lambdaNumerujArg(polecenie) : larg.getValue();
            polecenie = zamienArgumenty(polecenie.trim(), larg.getKey(), largv);

        }

        return "[" + polecenie + "]";
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

        if (hmPolecenia.get(str) != null && !czyLambda(str)) {
            // troche nieeleganckie wylapywanie overloadow ale przy naszych zalozeniach powinno dzialac...
            str = str.replace(str, parsuj(hmPolecenia.get(str), strArr));
        }

        String[] slowaPolecenia = slowaPolecenia(str);

        for (int i = 0; i < slowaPolecenia.length; i++) {
            String s = slowaPolecenia[i].toString();

            System.out.println("\t\t\t\t\t\t" + s);

            String[] newStrArr = new String[slowaPolecenia.length - (i + 1)];
            System.arraycopy(slowaPolecenia, i + 1, newStrArr, 0, slowaPolecenia.length - (i + 1));
            strArr = addArrays(strArr, newStrArr);

            if (czyLambda(s)) { //lambda
                // nazwa args[] = \ lambdaArgs[] -> polecenie
                TreeMap<String, String> args = new TreeMap<String, String>();
                TreeMap<String, String> lambdaArgs = new TreeMap<String, String>();
                String polecenie = hmPolecenia.get(str);
                if (polecenie == null)
                    polecenie = hmPolecenia.get(s);
                String target = "";
                if (polecenie == null) {
                    //sa argumenty funkcji
                    Map<String, String> pasujaceFunkcje = hmPolecenia.entrySet().stream()
                            .filter(entry -> entry.getKey().startsWith(s))
                            .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));
                    int k = 0;
                    for (Map.Entry<String, String> entry : pasujaceFunkcje.entrySet()) {

                        String[] deklaracjaFunkcji = entry.getKey().split(" ");

                        boolean jestLiczba = true;
                        int iloscArgumentow = deklaracjaFunkcji.length;
                        target = deklaracjaFunkcji[0];

                        for (int j = 1; j < iloscArgumentow; j++) {
                            jestLiczba = jestLiczba(deklaracjaFunkcji[j]);
                            String wartArg;
                            if (!jestLiczba) {

                                try {
                                    wartArg = slowaPolecenia[i + j];
                                    target += " " + wartArg;
                                } catch (ArrayIndexOutOfBoundsException e) {
                                    wartArg = "{arg}";
                                }
                                args.put(deklaracjaFunkcji[j], wartArg);
                                k++;

                            }

                        }
                        if (!jestLiczba)
                            polecenie = entry.getValue();

                    }
                    for (int j = 0; j < lambdaIleArg(polecenie); j++) {

                        String wartArg;
                        try {
                            wartArg = slowaPolecenia[args.size() + 1 + i + j];
                        } catch (ArrayIndexOutOfBoundsException e) {
                            wartArg = "{arg}";
                        }
                        lambdaArgs.put(polecenie.substring(polecenie.indexOf("\\") + 1, polecenie.indexOf("->")).trim()
                                .split(" ")[j], wartArg);
                    }
                    i += k;
                } else if (polecenie != null) {
                    target = s;
                    //args puste
                    //tylko lambdaArgs
                    int k = i;
                    for (int j = 0; j < lambdaIleArg(polecenie); j++) {

                        String wartArg;
                        try {
                            wartArg = slowaPolecenia[1 + k + j];
                            target += " " + wartArg;
                            i++;
                        } catch (ArrayIndexOutOfBoundsException e) {
                            wartArg = "{arg}";
                        }
                        lambdaArgs.put(polecenie.substring(polecenie.indexOf("\\") + 1, polecenie.indexOf("->")).trim()
                                .split(" ")[j], wartArg);
                    }

                }

                // System.out.println(lambdaParsuj(polecenie, args, lambdaArgs));
                str = str.replace(target, lambdaParsuj(polecenie, args, lambdaArgs));

                //str.substring(str.indexOf("\\") + 1, str.indexOf("->")).trim().split(" ").length;
                /*
                
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
                        if (/*strArr.length - 1 < ktrue)
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
                
                */

            } else if (hmPolecenia.get(s) != null) {
                str = zamienArgumenty(str, s, parsuj(hmPolecenia.get(s)));

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

        if (slowaPolecenia(str).length > 1 && lambdaHoldery(str).size() > 0)
            str = lambdaParsuj(str, new TreeMap<>(), new TreeMap<>());

        if (slowaPolecenia(str).length == 1 && lambdaHoldery(str).size() == 0 && indeksyNawiasowKwad(str)[0] > -1)
            str = parsuj(str.replace("[", "").replace("]", ""));

        if (jestLiczba(str) && Float.parseFloat(str) % 1 == 0) {
            str = str.replace(".0", "");
        }

        return str;
    }

}

class PolecenieLambda {
    public String deklaracja;
    public String polecenie;
    public HashMap<String, String> args = new HashMap<String, String>();
    public HashMap<String, String> lambdaArgs = new HashMap<String, String>();

    PolecenieLambda(String deklaracja, String polecenie, HashMap<String, String> args,
            HashMap<String, String> lambdaArgs) {
        this.deklaracja = deklaracja;
        this.polecenie = polecenie;
        this.args = args;
        this.lambdaArgs = lambdaArgs;
    }
}