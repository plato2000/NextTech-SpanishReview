package com.nexttech.spanishreview.teacher;

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
import java.util.Iterator;

import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * Created by plato2000 on 5/30/16.
 */
public class ResetUsersServlet extends HttpServlet {
    /**
     * Servlet used to reset student data for given students. Creates a new Student object for each student in the
     * given list, and sets the requirements to whatever was given.
     * @param req the HTTP request to get to this page
     * @param resp the HTTP response to return
     * @throws IOException
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Respond as JSON
        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();
        try {
            // Get JSON object from HTTP request
            JSONObject jsonObject = (JSONObject) new JSONParser().parse(req.getReader());

            // Gets requirements and teacher from jsonObject
            String teacher = (String) jsonObject.get("teacher");
            int total = Integer.parseInt((String) jsonObject.get("total"));
            int blank = Integer.parseInt((String) jsonObject.get("blank"));
            int scoredOver = Integer.parseInt((String) jsonObject.get("scoredOver"));
            long deadline = (Long) jsonObject.get("deadline");
            System.out.println(teacher);


            // Gets the list of IDs to reset from the JSON object
            JSONArray jsonArray = (JSONArray) jsonObject.get("id");
            Iterator iterator = jsonArray.iterator();

            // Goes through each ID
            while(iterator.hasNext()) {
                long id = Long.parseLong((String) iterator.next());
                // Makes new student with given id
                MCPSStudent student = new MCPSStudent(id);
                // Sets requirements
                student.setRequiredTotal(total);
                student.setRequiredBlank(blank);
                student.setRequiredOverScore(scoredOver);
                student.setDeadline(deadline);
                student.setTeacher(teacher);

                // Saves to overwrite what was there (reset)
                ofy().save().entity(student).now();
            }
            // Returns what requirements were set to
            StringBuilder json = new StringBuilder("{");
            json.append("\"total\": \"" + total + "\",\n");
            json.append("\"blank\": \"" + blank + "\",\n");
            json.append("\"scoredOver\": \"" + scoredOver + "\",\n");
            json.append("\"deadline\": \"" + deadline + "\"\n");
            json.append("}");
            out.println(json.toString());
            System.out.println(json.toString());
        } catch(Exception e) {
            // In case of error, success = false
            e.printStackTrace();
            out.println("{ \"success\": \"false\" }");
        }
    }
}
