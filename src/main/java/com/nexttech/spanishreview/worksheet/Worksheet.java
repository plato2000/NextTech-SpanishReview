package com.nexttech.spanishreview.worksheet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by plato2000 on 5/16/16.
 */
public class Worksheet {
    // The 2D String array that contains all of the values in the Worksheet
    private String[][] worksheet;

    // The List of words in the Word Bank
    private List<String> wordBank;


    /**
     * Create a Worksheet that has no Word Bank - initialized as empty List
     * @param worksheet 2D array containing values in a table from the worksheet
     */
    public Worksheet(String[][] worksheet) {
        this.worksheet = new String[worksheet.length][];
        for(int i = 0; i < this.worksheet.length; i++) {
            this.worksheet[i] = Arrays.copyOf(worksheet[i], worksheet[i].length);
        }
        this.wordBank = new ArrayList<String>();
    }

    /**
     * Create a Worksheet given a 2D array and a Word Bank
     * @param worksheet 2D array containing values in a table from the worksheet
     * @param wordBank A List of Strings that go in blanks in the worksheet
     */
    public Worksheet(String[][] worksheet, List<String> wordBank) {
        this.worksheet = new String[worksheet.length][];
        for(int i = 0; i < this.worksheet.length; i++) {
            this.worksheet[i] = Arrays.copyOf(worksheet[i], worksheet[i].length);
        }
        this.wordBank = new ArrayList<String>();
        this.wordBank.addAll(wordBank);
    }

    /**
     * Copy constructor
     * @param other Worksheet to copy fields from
     */
    public Worksheet(Worksheet other) {
        this.worksheet = new String[other.worksheet.length][];
        for(int i = 0; i < this.worksheet.length; i++) {
            this.worksheet[i] = Arrays.copyOf(other.worksheet[i], other.worksheet[i].length);
        }
        this.wordBank = new ArrayList<String>();
        this.wordBank.addAll(other.wordBank);
    }

    /**
     * Returns a copied 2D array of Strings that represent the Worksheet
     * @return a copied 2D array of Strings that represent the Worksheet
     */
    public String[][] getWorksheet() {
        String[][] copiedWorksheet = new String[this.worksheet.length][];
        for(int i = 0; i < this.worksheet.length; i++) {
            copiedWorksheet[i] = Arrays.copyOf(this.worksheet[i], this.worksheet[i].length);
        }
        return copiedWorksheet;
    }

    /**
     * Returns a copied List of Strings that represent the items that go in the blanks of the Worksheet
     * @return a copied List of Strings that represent the items that go in the blanks of the Worksheet
     */
    public List<String> getWordBank() {
        ArrayList<String> wordBank = new ArrayList<String>();
        wordBank.addAll(this.wordBank);
        return wordBank;
    }

}
