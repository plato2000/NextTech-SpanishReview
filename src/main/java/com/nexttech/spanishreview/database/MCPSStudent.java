package com.nexttech.spanishreview.database;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.*;

import java.util.Arrays;

/**
 * Created by plato2000 on 5/25/16.
 */

// Datastore entity
@Entity
public class MCPSStudent {
    // MCPS Student ID
    @Id Long id;
    // MCPS email
    String emailAddress;
    // The real name of the student
    String name;

    // The total number of worksheets completed by the user
    int totalWorksheets;
    // The total number of worksheets over the given score completed by the user
    int overScore;
    // The total number of blank worksheets filled out by the user
    int blankWorksheets;

    // The deadline time set by the teacher
    long deadline;
    // The name/email of the teacher
    String teacher;

    // The last served worksheet
    @Serialize String[][] lastWorksheet = null;

    /**
     * The private constructor, never used (Objectify requires a no-args constructor)
     */
    private MCPSStudent() {}

    /**
     * The basic constructor, makes a user with dummy fields except for ID and emailAddress. Used when student logs in
     * before teacher sets stuff up.
     * @param emailAddress The MCPS email address of the student
     */
    public MCPSStudent(String emailAddress) {
        this(emailAddress, 0, "", emailAddress);
    }

    /**
     * The expanded constructor; makes a user given an email address, a deadline, and a teacher.
     * @param emailAddress The MCPS email address of the student
     * @param deadline The deadline time for the student to complete work by
     * @param teacher The name/email of the teacher
     */
    public MCPSStudent(String emailAddress, long deadline, String teacher, String name) {
        this.emailAddress = emailAddress;
        this.deadline = deadline;
        this.teacher = teacher;

        this.totalWorksheets = 0;
        this.overScore = 0;
        this.blankWorksheets = 0;

        // The part of the email address before the @ symbol is the id
        this.id = Long.parseLong(this.emailAddress.substring(0, this.emailAddress.indexOf("@")));

        this.name = name;
    }

    public void completedOverScore() {
        this.overScore++;
    }
    public void completedWorksheet() {
        this.totalWorksheets++;
    }
    public void completedBlank() {
        this.blankWorksheets++;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public int getTotalWorksheets() {
        return totalWorksheets;
    }

    public int getOverScore() {
        return overScore;
    }

    public int getBlankWorksheets() {
        return blankWorksheets;
    }

    public long getDeadline() {
        return deadline;
    }

    public void setDeadline(long deadline) {
        this.deadline = deadline;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String[][] getLastWorksheet() {
        if(this.lastWorksheet == null) {
            return null;
        }
        String[][] copiedWorksheet = new String[this.lastWorksheet.length][];
        for(int i = 0; i < this.lastWorksheet.length; i++) {
            copiedWorksheet[i] = Arrays.copyOf(this.lastWorksheet[i], this.lastWorksheet[i].length);
        }
        return copiedWorksheet;
    }

    public void setLastWorksheet(String[][] worksheet) {
        this.lastWorksheet = new String[worksheet.length][];
        for(int i = 0; i < worksheet.length; i++) {
            this.lastWorksheet[i] = Arrays.copyOf(worksheet[i], worksheet[i].length);
        }
    }

    @Override
    public String toString() {
        return "MCPSStudent{" +
                "id=" + id +
                ", emailAddress='" + emailAddress + '\'' +
                ", name='" + name + '\'' +
                ", totalWorksheets=" + totalWorksheets +
                ", overScore=" + overScore +
                ", blankWorksheets=" + blankWorksheets +
                ", deadline=" + deadline +
                ", teacher='" + teacher + '\'' +
                ", lastWorksheet=" + Arrays.toString(lastWorksheet) +
                '}';
    }
}
