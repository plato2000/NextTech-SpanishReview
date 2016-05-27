package com.nexttech.spanishreview.worksheet;

import com.nexttech.spanishreview.database.MCPSStudent;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;

import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * Created by plato2000 on 5/25/16.
 */
public class GraderServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();
        try {
            JSONObject jsonObject = (JSONObject) new JSONParser().parse(req.getReader());
            ArrayList<ArrayList> toBeGradedList = new ArrayList<>();

            String email = (String) jsonObject.get("email");
            MCPSStudent student = ofy().load().type(MCPSStudent.class).id(Long.parseLong(email.substring(0, email.indexOf("@")))).now();
            String[][] lastWorksheet = student.getLastWorksheet();

            if(student == null) {
                throw new IOException("Ye crusty new student didn't sign up first -_-");
            }
            JSONArray jsonArray = (JSONArray) jsonObject.get("ws");
            Iterator iterator = jsonArray.iterator();
            while(iterator.hasNext()) {
                ArrayList<String> currentRow = new ArrayList<>();
                JSONArray jsonRow = (JSONArray) iterator.next();
                Iterator rowIterator = jsonRow.iterator();
                while(rowIterator.hasNext()) {
                    String s = (String) rowIterator.next();
//                    System.out.println(s);
                    currentRow.add(s.replaceAll("\\p{C}", ""));
                }
                toBeGradedList.add(currentRow);
            }
//            System.out.println(toBeGradedList);
            String[][] toBeGraded = new String[toBeGradedList.size()][];
            for(int i = 0; i < toBeGradedList.size(); i++) {
                toBeGraded[i] = new String[toBeGradedList.get(i).size()];
                for(int j = 0; j < toBeGradedList.get(i).size(); j++) {
                    toBeGraded[i][j] = toBeGradedList.get(i).get(j).toString().replaceAll("\\p{C}", "");
                }
            }

            int score = WorksheetGrader.getScore(student.getLastWorksheet(), toBeGraded);
            if(score > WorksheetGrader.getThresholdScore()) {
                student.completedOverScore();
            }
            if(WorksheetGrader.isBlankSheet(student.getLastWorksheet())) {
                student.completedBlank();
            }
            student.completedWorksheet();
            ofy().save().entity(student);
            out.println("{ \"success\": \"true\", \"score\": \"" + score + "\" }");
            System.out.println(score);
        } catch(Exception e) {
            e.printStackTrace();
            out.println("{ \"success\": \"false\" }");
        }
    }
}
