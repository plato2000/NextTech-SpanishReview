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
    // The RNG for this particular generator - can be specified if you want this to be guessable or something
    private Random random;
    // The Worksheet that is used as a base worksheet to randomize
    private Worksheet baseWorksheet;

    /**
     * No-args constructor - uses a standard Random object and uses baseSpanishWorksheet() to get base worksheet
     * @throws ParseException
     * @throws IOException
     */
    public WorksheetGenerator() throws ParseException, IOException {
        this.random = new Random();
        this.baseWorksheet = new Worksheet(baseSpanishWorksheet());
    }

    /**
     * Uses a given seed for RNG, baseSpanishWorksheet to get base worksheet
     * @param seed The seed used to make the RNG
     * @throws ParseException
     * @throws IOException
     */
    public WorksheetGenerator(long seed) throws ParseException, IOException {
        this.random = new Random(seed);
        this.baseWorksheet = new Worksheet(baseSpanishWorksheet());

    }

    /**
     * Uses a given Random object (directly) as the RNG, and a given 2D array of Strings as the base worksheet
     * @param random The RNG to use to randomize
     * @param baseWorksheet The base worksheet to start at to randomize
     */
    public WorksheetGenerator(Random random, String[][] baseWorksheet) {
        this.random = random;
        this.baseWorksheet = new Worksheet(baseWorksheet);
//        this.baseWorksheet = jsonObjectFromPath(pathToJSON);
//        baseWorksheet.
    }

    /**
     * Gets the starting worksheet used by this Generator
     * @return a Worksheet object for the starting worksheet used by this Generator
     */
    public Worksheet getBaseWorksheet() {
        return new Worksheet(this.baseWorksheet);
    }

    /**
     * Gets a Worksheet with every possible blank blank
     * @return a Worksheet object with fields blank
     */
    public Worksheet getBlankWorksheet() {
        String[][] baseWorksheet = this.baseWorksheet.getWorksheet();
        String[][] blankSheet = new String[baseWorksheet.length][];
        // Copies over header
        blankSheet[0] = Arrays.copyOf(baseWorksheet[0], baseWorksheet[0].length);
        List<String> wordBank = new ArrayList<>();
        // Goes through each row after the header, puts in the leftmost column, and blanks the rest
        for(int i = 1; i < baseWorksheet.length; i++) {
            blankSheet[i] = new String[baseWorksheet[i].length];
            blankSheet[i][0] = baseWorksheet[i][0];
            for(int j = 1; j < baseWorksheet[i].length - 2; j++) {
                blankSheet[i][j] = "";
                // Adds the things that are blanked to wordbank
                wordBank.add(baseWorksheet[i][j]);
            }
        }
        // Shuffles the worksheet
        randomizeSheet(blankSheet);
        // Shuffles the wordbank
        Collections.shuffle(wordBank);
        // Makes a new Worksheet object given the worksheet and wordbank
        return new Worksheet(blankSheet, wordBank);
    }

    /**
     * Gets a Worksheet with every possible item blank except for one random in each row
     * @return a Worksheet object with most fields blank
     */
    public Worksheet getRegularWorksheet() {
        String[][] baseWorksheet = this.baseWorksheet.getWorksheet();
        String[][] regularWorksheet = new String[baseWorksheet.length][];
        // Copies headers
        regularWorksheet[0] = Arrays.copyOf(baseWorksheet[0], baseWorksheet[0].length);
        ArrayList<String> wordBank = new ArrayList<String>();
        // Goes through every row except the last 2
        for(int i = 1; i < baseWorksheet.length - 2; i++) {
            regularWorksheet[i] = new String[baseWorksheet[i].length];
            // Picks a random column to keep filled
            int index = this.random.nextInt(regularWorksheet[i].length);
            regularWorksheet[i][index] = baseWorksheet[i][index];
            // Goes through the rest, blanks, adds to wordBank
            for(int j = 0; j < baseWorksheet[i].length; j++) {
                if(j != index) {
                    regularWorksheet[i][j] = "";
                    wordBank.add(baseWorksheet[i][j]);
                }
            }
        }
        // Goes through last 2 rows, removes {BLANK}s, and adds them to wordBank
        for(int i = baseWorksheet.length - 2; i < baseWorksheet.length; i++) {
            regularWorksheet[i] = Arrays.copyOf(baseWorksheet[i], baseWorksheet[i].length);
            wordBank.addAll(removeBlanksToWordBank(regularWorksheet[i]));
        }
        // Shuffles worksheet
        randomizeSheet(regularWorksheet);
        // Shuffles wordBank
        Collections.shuffle(wordBank);
        // Makes a new Worksheet given the Worksheet array and the wordBank
        return new Worksheet(regularWorksheet, wordBank);
    }

    /**
     * Shuffles the Worksheet given in place, keeping the header row in place and swapping other rows at random
     * @param sheet The 2D array worksheet that is being shuffled in place
     */
    private void randomizeSheet(String[][] sheet) {
        // Goes through each row, bottom up, swapping with another at random
        for (int i = sheet.length - 1; i > 1; i--) {
            int index = this.random.nextInt(i) + 1;
            // Simple swap
            String[] a = sheet[index];
            sheet[index] = sheet[i];
            sheet[i] = a;
        }
    }

    /**
     * Removes instances of the substring {thing} from each element in the row, replaces them with HTML making them drop
     * destinations on the webpage. The "thing" is then added to a word bank ArrayList, which is returned.
     * @param row An array of Strings that may have {BLANK}s in them
     * @return
     */
    private List<String> removeBlanksToWordBank(String[] row) {
        List<String> wordBank = new ArrayList<String>();
        for(int i = 0; i < row.length; i++) {
            // Accounts for multiple blanks in same row
            while(row[i].contains("{") && row[i].contains("}")) {
                // Gets the word between the two brackets, adds to wordBank
                wordBank.add(row[i].substring(row[i].indexOf("{") + 1, row[i].indexOf("}")));
                // Removes the word, replaces it with HTML to make it a drop target
                row[i] = row[i].substring(0, row[i].indexOf("{")) + "<span class='well inline-droppable droppable'></span>" + row[i].substring(row[i].indexOf("}") + 1);
            }
        }
        return wordBank;
    }

    /**
     * Gets the 2D String array of a worksheet by passing the JSON object from a specific path to a method to convert
     * that to a String array
     * @return A 2D array that is a Spanish worksheet
     * @throws ParseException
     * @throws IOException
     */
    private String[][] baseSpanishWorksheet() throws ParseException, IOException {
        return kingJSONToStringArray(jsonObjectFromPath("WEB-INF/kingWorksheet.json"));
    }

    /**
     * Converts a JSON Object for a Ms. King worksheet to a 2D array of Strings - very specific type of JSON worksheet
     * @param json The JSONObject that represents a Ms. King worksheet
     * @return A 2D array - a table - with worksheet stuff in it
     */
    private String[][] kingJSONToStringArray(JSONObject json) {
        String[][] kingWorksheet = new String[10][];
        // Put in headers
        kingWorksheet[0] = new String[] {"Tense", "Translation", "AR", "ER", "IR"};
        // Go through rest of rows
        for(int i = 1; i < kingWorksheet.length; i++) {
            // If it is one of the regular rows:
            if(!(i > 7)) {
                kingWorksheet[i] = new String[5];
                String key;
                // Sets the json key to use given a number - to go through the keys in order
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
                    // Puts in the String representation of the object at [key][j]
                    kingWorksheet[i][j] = (((JSONObject) json.get(key)).get(kingWorksheet[0][j])).toString();
                }
            } else {
                // In special case 1 (i == 8)
                if(i == 8) {
                    kingWorksheet[i] = new String[3];
                    // Specially adds the 3 items in this row
                    kingWorksheet[i][0] = "Command (Imperative)";
                    kingWorksheet[i][1] = (((JSONObject) json.get(kingWorksheet[i][0])).get("affNeg")).toString();
                    kingWorksheet[i][2] = (((JSONObject) json.get(kingWorksheet[i][0])).get("allCommands")).toString();
                // In special case 2 (i == 9)
                } else if(i == 9) {
                    kingWorksheet[i] = new String[2];
                    // Specially adds the 2 items to this row
                    kingWorksheet[i][0] = "Subjunctive formula";
                    kingWorksheet[i][1] = (((JSONObject) json.get(kingWorksheet[i][0])).get("formula")).toString();
                }
            }
        }
        return kingWorksheet;
    }

    /**
     * Gets a JSONObject parsed using SimpleJSON from a given path, using readFileAsString
     * @param path The relative or absolute path of the JSON file given to it
     * @return The JSONObject that represents the contents of the file at path
     * @throws ParseException
     * @throws IOException
     */
    private JSONObject jsonObjectFromPath(String path) throws ParseException, IOException {
//        System.out.println("Current path: " + System.getProperty("user.dir"));
        String content = readFileAsString(path);
        return (JSONObject) new JSONParser().parse(content);
    }

    /**
     * Gets the entire file at filePath as a String
     * @param filePath The relative or absolute path of the file to get as a String
     * @return A String that contains the contents of the file at filePath
     * @throws IOException
     */
    private String readFileAsString(String filePath) throws IOException {
        StringBuffer fileData = new StringBuffer();
        BufferedReader reader = new BufferedReader(
                new FileReader(filePath));
        char[] buf = new char[1024];
        int numRead=0;
        while((numRead=reader.read(buf)) != -1){
            String readData = String.valueOf(buf, 0, numRead);
            fileData.append(readData);
        }
        reader.close();
        return fileData.toString();
    }

}
