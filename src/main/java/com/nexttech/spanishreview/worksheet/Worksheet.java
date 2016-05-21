package com.nexttech.spanishreview.worksheet;

import java.util.Arrays;

/**
 * Created by plato2000 on 5/16/16.
 */
public class Worksheet {
    private String[][] worksheet;
    private String[] wordBank;


    public Worksheet(String[][] worksheet) {
        this.worksheet = new String[worksheet.length][];
        for(int i = 0; i < this.worksheet.length; i++) {
            this.worksheet[i] = Arrays.copyOf(worksheet[i], worksheet[i].length);
        }
        this.wordBank = new String[0];
    }

    public Worksheet(String[][] worksheet, String[] wordBank) {
        this.worksheet = new String[worksheet.length][];
        for(int i = 0; i < this.worksheet.length; i++) {
            this.worksheet[i] = Arrays.copyOf(worksheet[i], worksheet[i].length);
        }
        this.wordBank = Arrays.copyOf(wordBank, wordBank.length);
    }

    public String[][] getWorksheet() {
        String[][] copiedWorksheet = new String[this.worksheet.length][];
        for(int i = 0; i < this.worksheet.length; i++) {
            copiedWorksheet[i] = Arrays.copyOf(this.worksheet[i], this.worksheet[i].length);
        }
        return copiedWorksheet;
    }

    public String[] getWordBank() {
        return Arrays.copyOf(this.wordBank, wordBank.length);
    }

}
