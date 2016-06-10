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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * Created by plato2000 on 5/30/16.
 */
public class InfoGetterServlet extends HttpServlet {
    /**
     * Servlet used to get data for the teacher. Receives an array of student IDs to get information for, and returns
     * whether the students have completed the assignment or not. Also gets the requirements set on the students.
     * @param req the HTTP request to get to this page
     * @param resp the HTTP response to return
     * @throws IOException
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Respond to the request in JSON format
        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();
        // If an exception is thrown, don't do anything, just return success as false
        try {
            // Reads HTTP request parameters as JSON
            JSONObject jsonObject = (JSONObject) new JSONParser().parse(req.getReader());
            System.out.println(jsonObject);
            // StringBuilder for the output JSON
            StringBuilder json = new StringBuilder();
            json.append("{\"success\": \"true\", \n");
            json.append("\"results\": {");

            // Get the teacher from the JSONObject to set for each student
            String teacher = (String) jsonObject.get("teacher");
            System.out.println(teacher);

            // Get the array of IDs to get info for
            JSONArray jsonArray = (JSONArray) jsonObject.get("id");
            List<Long> ids = new ArrayList<>();
            Iterator iterator = jsonArray.iterator();

            while(iterator.hasNext()) {
                ids.add(Long.parseLong((String) iterator.next()));
            }

            Map<Long, MCPSStudent> students = ofy().load().type(MCPSStudent.class).ids(ids);

            // Load in MCPSStudent for that id
            MCPSStudent student = students.get(ids.get(0));
            if(student == null) {
                student = new MCPSStudent(ids.get(0));
            }
            // Set teacher to teacher name from the request
            student.setTeacher(teacher);
            // Save student after setting teacher
            students.put(ids.get(0), student);

            System.out.println(student);

            // Get requirements for students
            int total = student.getRequiredTotal();
            int blank = student.getRequiredBlank();
            int scoredOver = student.getRequiredOverScore();
            long deadline = student.getDeadline();
            // Write whether the student has met requirements to the JSON to return
            json.append("\"" + ids.get(0) + "\": ");
            if(student.metRequirements()) {
                json.append("\"Completed\"");
            } else if(student.started()) {
                json.append("\"Incomplete\"");
            } else {
                json.append("\"Not started\"");
            }
            // Repeat for all the rest of the students
            for(Long id : ids) {
                student = students.get(id);
                if(student == null) {
                    student = new MCPSStudent(id);
                }
                System.out.println(student);
                // Set the requirements to whatever they were for the first student to make sure all of the students
                // have the same requirements
                student.setRequiredTotal(total);
                student.setRequiredBlank(blank);
                student.setRequiredOverScore(scoredOver);
                student.setDeadline(deadline);
                student.setTeacher(teacher);
                students.put(id, student);
                json.append(",\n");
                json.append("\"" + id + "\": ");
                if(student.metRequirements()) {
                    json.append("\"Completed\"");
                } else if(student.started()) {
                    json.append("\"Incomplete\"");
                } else {
                    json.append("\"Not started\"");
                }
            }
            ofy().save().entities(students.values()).now();
            // Append requirements to the JSON so the teacher can see and change them
            json.append("},\n");
            json.append("\"total\": \"" + total + "\",\n");
            json.append("\"blank\": \"" + blank + "\",\n");
            json.append("\"scoredOver\": \"" + scoredOver + "\",\n");
            json.append("\"deadline\": \"" + deadline + "\"\n");
            json.append("}");
            out.println(json.toString());
            System.out.println(json.toString());
        } catch(Exception e) {
            // If there was an exception, return false
            e.printStackTrace();
            out.println("{ \"success\": \"false\" }");
        }
    }
}
