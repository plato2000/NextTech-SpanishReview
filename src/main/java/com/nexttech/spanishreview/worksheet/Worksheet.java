package com.nexttech.spanishreview.worksheet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by plato2000 on 5/16/16.
 */
public class Worksheet {
    private String[][] worksheet;
    private List<String> wordBank;


    public Worksheet(String[][] worksheet) {
        this.worksheet = new String[worksheet.length][];
        for(int i = 0; i < this.worksheet.length; i++) {
            this.worksheet[i] = Arrays.copyOf(worksheet[i], worksheet[i].length);
        }
        this.wordBank = new ArrayList<String>();
    }

    public Worksheet(String[][] worksheet, List<String> wordBank) {
        this.worksheet = new String[worksheet.length][];
        for(int i = 0; i < this.worksheet.length; i++) {
            this.worksheet[i] = Arrays.copyOf(worksheet[i], worksheet[i].length);
        }
        this.wordBank = new ArrayList<String>();
        this.wordBank.addAll(wordBank);
    }

    public String[][] getWorksheet() {
        String[][] copiedWorksheet = new String[this.worksheet.length][];
        for(int i = 0; i < this.worksheet.length; i++) {
            copiedWorksheet[i] = Arrays.copyOf(this.worksheet[i], this.worksheet[i].length);
        }
        return copiedWorksheet;
    }

    public List<String> getWordBank() {
        ArrayList<String> wordBank = new ArrayList<String>();
        wordBank.addAll(this.wordBank);
        return wordBank;
    }

}
