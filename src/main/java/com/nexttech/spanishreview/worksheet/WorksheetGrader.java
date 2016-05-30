package com.nexttech.spanishreview.worksheet;

import com.google.common.collect.BiMap;
import com.nexttech.spanishreview.utils.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by plato2000 on 5/23/16.
 */
public class WorksheetGrader {
    // The String[][] that contains the answer key in the form of id numbers
    private static String[][] correctWorksheet = null;

    private static final int THRESHOLD_SCORE = 30;

    /**
     * If correctWorksheet is null, converts a stringWorksheet from WorksheetGenerator to id number format, using
     * the BiMap in Utils. It sets this converted worksheet to correctWorksheet, so that this only has to be done once.
     * @return correctWorksheet
     * @throws IOException
     */
    public static String[][] getCorrectKingWorksheet() throws IOException {
        if(correctWorksheet == null) {
            BiMap<String, Integer> idMap = Utils.getIDMap();
            WorksheetGenerator worksheetGenerator = new WorksheetGenerator();
            String[][] stringWorksheet = worksheetGenerator.getBaseWorksheet().getWorksheet();
            correctWorksheet = new String[stringWorksheet.length][];
            for(int i = 0; i < stringWorksheet.length - 2; i++) {
                correctWorksheet[i] = new String[stringWorksheet[i].length];
                for(int j = 0; j < stringWorksheet[i].length; j++) {
//                    System.out.println((int) stringWorksheet[i][j].charAt(0));
//                    System.out.println((int) idMap.inverse().get(5).charAt(0));
//                    System.out.println(StringUtils.differenceAt(idMap.inverse().get(5), stringWorksheet[i][j]));
//                    System.out.println(idMap.inverse().get(5).equals(stringWorksheet[i][j]));
                    correctWorksheet[i][j] = idMap.get(stringWorksheet[i][j]).toString();
//                    System.out.println(correctWorksheet[i][j]);
//                    System.out.println(idMap.inverse().get(40).equals(stringWorksheet[i][j]));
//                    System.out.print(correctWorksheet[i][j] + " ");
                }
//                System.out.println();
            }
            for(int i = stringWorksheet.length - 2; i < stringWorksheet.length; i++) {
                correctWorksheet[i] = new String[stringWorksheet[i].length];
                correctWorksheet[i][0] = idMap.get(stringWorksheet[i][0]).toString();
//                System.out.print(correctWorksheet[i][0] + " ");
                for(int j = 1; j < stringWorksheet[i].length; j++) {
                    List<String> currentSpot = WorksheetGenerator.removeBlanksToWordBank(stringWorksheet[i]);
                    List<Integer> currentInt = new ArrayList<Integer>();
                    for(String s : currentSpot) {
                        currentInt.add(idMap.get(s));
                    }
                    correctWorksheet[i][j] = Utils.joinList(currentInt);
//                    System.out.print(correctWorksheet[i][j] + " ");
                }
//                System.out.println();
            }
        }
//        Utils.printArray(correctWorksheet[0], System.out);
        return correctWorksheet;
    }

    /**
     * Calculates the score for a worksheet given the original (unfilled) worksheet and the worksheet that is to
     * be graded.
     * @param origWorksheet A String[][] that contains the headers and values that were given to the student to
     *                      fill the worksheet from
     * @param toBeGraded A String[][] that contains everything from origWorksheet and the additional values that the
     *                   student has filled out.
     * @return An integer representation of the score, from 0 to 40. Any signs of cheating give a score of 0.
     */
    public static int getScore(String[][] origWorksheet, String[][] toBeGraded) {
        try {
            String[][] answerKey = getCorrectKingWorksheet();
//            PrettyPrinter pr = new PrettyPrinter(System.out);
//            pr.print(answerKey);
//            pr.print(toBeGraded);
            // The clauses that have return 0 mean there was tampering somewhere.
            if(origWorksheet.length != toBeGraded.length) {
                System.out.println("Wrong size");
                return 0;
            }

            int currScore = 0;

            // Check header - "Tense", "Translation", "AR", "ER", "IR" expected
            for(int i = 0; i < origWorksheet[0].length; i++) {
                if(!Utils.getIDMap().get(origWorksheet[0][i]).toString().equals(toBeGraded[0][i])) {
                    System.out.println("Headers are wrong");
//                    Utils.printArray(origWorksheet[0], System.out);
//                    Utils.printArray(toBeGraded[0], System.out);
                    System.out.println(origWorksheet[0][i] + " " + toBeGraded[0][i]);
                    return 0;
                }
            }

            // Go through rest of worksheet
            for(int i = 1; i < toBeGraded.length; i++) {
                if(origWorksheet[i].length != toBeGraded[i].length) {
                    Utils.printArray(origWorksheet[i], System.out);
                    Utils.printArray(toBeGraded[i], System.out);
                    System.out.println("Wrong size in row " + i + " - " + origWorksheet[i].length + " vs. " + toBeGraded[i].length);
                    return 0;
                }

                // If it is Command form or Subjunctive form row
//                System.out.println(origWorksheet[i][0]);
//                System.out.println(Utils.getIDMap().get(origWorksheet[i][0]));
                if(!origWorksheet[i][0].equals("") && (Utils.getIDMap().get(origWorksheet[i][0]).toString().equals("46")
                        || Utils.getIDMap().get(origWorksheet[i][0]).toString().equals("26"))) {
                    if(!Utils.getIDMap().get(origWorksheet[i][0]).toString().equals(toBeGraded[i][0])) {
                        System.out.println("The first column of subj. or comm. was wrong at " + i);
                        return 0;
                    }

                    // Find row in answer key
                    int graderIndex = 0;
                    for(int p = 0; p < answerKey.length; p++) {
                        if(answerKey[p][0].equals(Utils.getIDMap().get(origWorksheet[i][0]).toString())) {
                            graderIndex = p;
                            break;
                        }
                    }

                    // If the entire thing was right, add four points - since irregular number of blanks, this is best
                    // way to do it
                    if(answerKey[graderIndex][1].equals(toBeGraded[i][1])) {
                        currScore += 4;
                    }
                } else {
                    // Find the index of the item in the row that was given to the student
                    int indexOfGiven = 0;
                    for(int j = 0; j < origWorksheet[i].length; j++) {
                        if(!origWorksheet[i][j].equals("")) {
                            indexOfGiven = j;
                            break;
                        }
                    }

                    if(!Utils.getIDMap().get(origWorksheet[i][indexOfGiven]).toString().equals(toBeGraded[i][indexOfGiven])) {
                        System.out.println("Given point was not in place in row " + i);
                        return 0;
                    }

                    // Find index of answer row for toBeGraded[i]
                    int graderIndex = 0;
                    for(int p = 0; p < answerKey.length; p++) {
                        if(answerKey[p][indexOfGiven].equals(Utils.getIDMap().get(origWorksheet[i][indexOfGiven]).toString())) {
                            graderIndex = p;
                            break;
                        }
                    }

                    // Check answers for the row
                    for(int j = 0; j < origWorksheet[i].length; j++) {
                        if(answerKey[graderIndex][j].equals(toBeGraded[i][j]) && j != indexOfGiven) {
                            System.out.println(toBeGraded[i][j] + " was correct");
                            currScore++;
                        }
                    }
                }
            }
            return currScore;
        } catch(Exception e) {
            // Any error is assumed to be some form of cheating.
            System.out.println("There was an Exception");
            e.printStackTrace();
            return 0;
        }
    }


    /**
     * Checks if the given String[][] worksheet is blank - if the first element in each row is not blank
     * @param worksheet A String[][] that represents the worksheet
     * @return Boolean, true if worksheet is blank template
     */
    public static boolean isBlankSheet(String[][] worksheet) {
        for(String[] row : worksheet) {
            if(row[0].equals("")) {
                return false;
            }
        }
        return true;
    }

    public static boolean hasBlanksInSheet(String[][] worksheet) {
        for(String[] row : worksheet) {
            for(String s : row) {
                if(s.equals("")) {
                    return true;
                }
            }
        }
        return false;
    }


    public static int getThresholdScore() {
        return THRESHOLD_SCORE;
    }
}
