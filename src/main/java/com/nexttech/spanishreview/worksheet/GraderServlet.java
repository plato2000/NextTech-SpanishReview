package com.nexttech.spanishreview.worksheet;

import com.nexttech.spanishreview.database.MCPSStudent;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * Created by plato2000 on 5/25/16.
 */
public class GraderServlet extends HttpServlet {
    /**
     * Servlet used to receive worksheets to be graded. When an HTTP POST request is made to this Servlet, it will read
     * the request parameters as JSON and grade the worksheet, responding with the score and success of the grading.
     * @param req the HTTP request to get to this page
     * @param resp the HTTP response to return
     * @throws IOException
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();
        try {
            // Reads the request parametsrs as a JSON object
            JSONObject jsonObject = (JSONObject) new JSONParser().parse(req.getReader());
            // A 2D ArrayList to read the worksheet into
            List<List> toBeGradedList = new ArrayList<>();

            // Gets the email address of the student from the JSON object
            String email = (String) jsonObject.get("email");

            // Gets the student object from the database based on the student id
            // There is no failsafe to create a new student in case they don't exist in the database
            // This is because if a worksheet is asked to be graded, the student should have been created on the
            // worksheet page, so if the student isn't in the database, there was some form of tampering
            MCPSStudent student = ofy().load().type(MCPSStudent.class).id(Long.parseLong(email.substring(0, email.indexOf("@")))).now();
            System.out.println(student);

            // Throw exception if tampering
            if(student == null) {
                throw new IOException("Ye crusty new student didn't sign up first -_-");
            }
            // Get the JSONArray of JSONArrays (This should be a 2D array of number strings and comma-separated numbers)
            JSONArray jsonArray = (JSONArray) jsonObject.get("ws");

            // Gets the iterator for the JSONArray, which provides the next element in the array while there is one
            Iterator iterator = jsonArray.iterator();
            while(iterator.hasNext()) {
                // Creates ArrayList for each row
                List<String> currentRow = new ArrayList<>();
                JSONArray jsonRow = (JSONArray) iterator.next();
                // Iterates through second dimension of array
                Iterator rowIterator = jsonRow.iterator();
                while(rowIterator.hasNext()) {
                    // Puts each element in the array into the ArrayList
                    String s = (String) rowIterator.next();
                    // Replaces nonprintable characters from the id numbers in the worksheet
                    currentRow.add(s.replaceAll("\\p{C}", ""));
                }
                // Puts the row into the big List
                toBeGradedList.add(currentRow);
            }

            // Creates a 2D array of strings to convert the 2D list to
            String[][] toBeGraded = new String[toBeGradedList.size()][];
            // Goes through each element in the List and puts it in the array
            for(int i = 0; i < toBeGradedList.size(); i++) {
                toBeGraded[i] = new String[toBeGradedList.get(i).size()];
                for(int j = 0; j < toBeGradedList.get(i).size(); j++) {
                    toBeGraded[i][j] = toBeGradedList.get(i).get(j).toString().replaceAll("\\p{C}", "");
                }
            }

            // Gets a score from the WorksheetGrader based on the worksheet served to the student and the worksheet
            // completed by the student
            int score = WorksheetGrader.getScore(student.getLastWorksheet(), toBeGraded);

            // Checks if the worksheet has blanks in it to throw out tampered worksheets
            boolean hasBlanks = WorksheetGrader.hasBlanksInSheet(toBeGraded);
            if(hasBlanks) {
                throw new Exception("y u incomplete");
            }

            // If worksheet has blanks or deadline is set and it is past that deadline, don't give credit (still report score)
            if(System.currentTimeMillis() <= student.getDeadline() || student.getDeadline() == 0) {
                // If student has scored above threshold score, increment that variable in the student
                if(score > WorksheetGrader.getThresholdScore()) {
                    student.completedOverScore();
                }
                // If student completed a blank worksheet, increment that variable in the student
                if(WorksheetGrader.isBlankSheet(student.getLastWorksheet())) {
                    student.completedBlank();
                }
                // Marks that the student has completed some worksheet (regardless of blankness and score)
                student.completedWorksheet();
                // Sets the last worksheet to null to avoid submission of the same worksheet multiple times to inflate
                // completed worksheet numbers
                student.setLastWorksheet(null);
            }
            // Saves the student after incrementing of the variables, and nulling of the last worksheet
            // Incidentally, IntelliJ recognizes "nulling" as a word but not "failsafe"
            ofy().save().entity(student).now();
            // Return a score and success as true
            out.println("{ \"success\": \"true\", \"score\": \"" + score + "\" }");
            System.out.println(score);
        } catch(Exception e) {
            e.printStackTrace();
            out.println("{ \"success\": \"false\" }");
        }
    }
}
