package com.nexttech.spanishreview.worksheet;

import com.nexttech.spanishreview.utils.PrettyPrinter;
import com.nexttech.spanishreview.utils.Utils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Created by plato2000 on 4/27/16.
 */
public class WorksheetGenerator {
    private Random random;
    private Worksheet baseWorksheet;

    public WorksheetGenerator() {
        this.random = new Random();
        this.baseWorksheet = new Worksheet(baseSpanishWorksheet());
    }

    public WorksheetGenerator(long seed) {
        this.random = new Random(seed);
        this.baseWorksheet = new Worksheet(baseSpanishWorksheet());

    }

    public WorksheetGenerator(Random random, String[][] baseWorksheet) {
        this.random = random;
        this.baseWorksheet = new Worksheet(baseWorksheet);
//        this.baseWorksheet = jsonObjectFromPath(pathToJSON);
//        baseWorksheet.
    }

    private String[][] baseSpanishWorksheet() {
        return kingJSONToStringArray(jsonObjectFromPath("WEB-INF/kingWorksheet.json"));
//        try (CSVReader reader = new CSVReader(new BufferedReader(
//                new FileReader("kingWorksheet.csv")));) {
//
//            List<String[]> lines = reader.readAll();
//            return lines.toArray(new String[lines.size()][]);
//        } catch(IOException e) {
//            e.printStackTrace();
//        }
//        return new String[][] {
//                {"a", "b", "c", "d", "e"},
//                {"j", "i", "h", "g", "f"},
//                {"k", "l", "m", "n", "o"},
//                {"t", "s", "r", "q", "p"},
//                {"u", "v", "w", "x", "y"},
//
//        };
    }

    public Worksheet getBlankWorksheet() {
        String[][] baseWorksheet = this.baseWorksheet.getWorksheet();
        String[][] blankSheet = new String[baseWorksheet.length][];
        blankSheet[0] = Arrays.copyOf(baseWorksheet[0], baseWorksheet[0].length);
        for(int i = 1; i < baseWorksheet.length; i++) {
            blankSheet[i] = new String[baseWorksheet[i].length];
            blankSheet[i][0] = baseWorksheet[i][0];
            for(int j = 1; j < baseWorksheet[i].length - 2; j++) {
                blankSheet[i][j] = "";
            }
        }
        randomizeSheet(blankSheet);
        return new Worksheet(blankSheet);
    }

    public Worksheet getRegularWorksheet() {
        String[][] baseWorksheet = this.baseWorksheet.getWorksheet();
        String[][] regularWorksheet = new String[baseWorksheet.length][];
        regularWorksheet[0] = Arrays.copyOf(baseWorksheet[0], baseWorksheet[0].length);
//        System.out.println(baseWorksheet.length);
//        System.out.println(regularWorksheet.length);
        ArrayList<String> wordBank = new ArrayList<String>();
        for(int i = 1; i < baseWorksheet.length - 2; i++) {
            System.out.println(baseWorksheet[i]);
            regularWorksheet[i] = new String[baseWorksheet[i].length];
            System.out.println(i);
//            regularWorksheet[i][0] = this.baseWorksheet[i][0];
            int index = this.random.nextInt(regularWorksheet[i].length);
            regularWorksheet[i][index] = baseWorksheet[i][index];
            for(int j = 0; j < baseWorksheet[i].length; j++) {
                if(j != index) {
                    regularWorksheet[i][j] = "";
                    wordBank.add(baseWorksheet[i][j]);
                }
            }
        }
        for(int i = baseWorksheet.length - 3; i < baseWorksheet.length; i++) {
            regularWorksheet[i] = Arrays.copyOf(baseWorksheet[i], baseWorksheet[i].length);
            wordBank.addAll(removeBlanksToWordBank(regularWorksheet[i]));
        }
        return new Worksheet(regularWorksheet);
    }


    private void randomizeSheet(String[][] sheet) {
        for (int i = sheet.length - 1; i > 1; i--) {
            int index = this.random.nextInt(i) + 1;
            // Simple swap
            String[] a = sheet[index];
            sheet[index] = sheet[i];
            sheet[i] = a;
        }
    }

    private ArrayList<String> removeBlanksToWordBank(String[] row) {
        ArrayList<String> wordBank = new ArrayList<String>();
        for(int i = 0; i < row.length; i++) {
            if(row[i].contains("{") && row[i].contains("}")) {
                wordBank.add(row[i].substring(row[i].indexOf("{"), row[i].indexOf("}")));
                row[i] = row[i].substring(0, row[i].indexOf("{") + 1) + "BLANK" + row[i].substring(row[i].indexOf("}"));
            }
        }
        return wordBank;
    }

    private String[][] kingJSONToStringArray(JSONObject json) {
        String[][] kingWorksheet = new String[10][];
        kingWorksheet[0] = new String[] {"Tense", "Translation", "AR", "ER", "IR"};
        for(int i = 1; i < kingWorksheet.length; i++) {
            if(!(i > 7)) {
                kingWorksheet[i] = new String[5];
                String key;
                switch(i) {
                    case 0:
                        key = "Present";
                        break;
                    case 1:
                        key = "Preterite";
                        break;
                    case 2:
                        key = "Imperfect";
                        break;
                    case 3:
                        key = "Future";
                        break;
                    case 4:
                        key = "Conditional";
                        break;
                    case 5:
                        key = "Present Progressive";
                        break;
                    case 6:
                        key = "Present Perfect";
                        break;
                    case 7:
                        key = "Present Subjunctive";
                        break;
                    default:
                        key = "";
                        break;
                }
                kingWorksheet[i][0] = key;
                for(int j = 1; j < kingWorksheet[0].length; j++) {
                    if(i == 6) {
                        System.out.println("key: " + key + " other key: " + kingWorksheet[0][j]);
                        System.out.println(((JSONObject) json.get(key)).get(kingWorksheet[0][j]));
                    }
                    kingWorksheet[i][j] = (((JSONObject) json.get(key)).get(kingWorksheet[0][j])).toString();
                }
            } else {
                if(i == 8) {
                    kingWorksheet[i] = new String[3];
                    kingWorksheet[i][0] = "Command (Imperative)";
                    kingWorksheet[i][1] = (((JSONObject) json.get(kingWorksheet[i][0])).get("affNeg")).toString();
                    kingWorksheet[i][2] = (((JSONObject) json.get(kingWorksheet[i][0])).get("allCommands")).toString();
                } else if(i == 9) {
                    kingWorksheet[i] = new String[2];
                    kingWorksheet[i][0] = "Subjunctive formula";
                    kingWorksheet[i][1] = (((JSONObject) json.get(kingWorksheet[i][0])).get("formula")).toString();
                }
            }
        }
        return kingWorksheet;
    }

    private JSONObject jsonObjectFromPath(String path) {
        try {
            System.out.println("Current path: " + System.getProperty("user.dir"));
            String content = Utils.readFromCloudBucket("/gcs/phs-spanishreview/kingWorksheet.json");
            return (JSONObject) new JSONParser().parse(content);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Worksheet getBaseWorksheet() {
        return this.baseWorksheet;
    }

    // TESTING purposes only
    public static void main(String[] args) {
        PrettyPrinter p = new PrettyPrinter(System.out);
        WorksheetGenerator g = new WorksheetGenerator();
//        String[][] baseWorksheet = g.getBaseWorksheet();
//        System.out.println("length: " + baseWorksheet.length);
//        p.print(g.getBaseWorksheet());
        System.out.println("Got base worksheet");
        p.print(g.getRegularWorksheet().getWorksheet());
        System.out.println(Utils.array2DToJson("ws", g.getRegularWorksheet().getWorksheet()));
    }

}
