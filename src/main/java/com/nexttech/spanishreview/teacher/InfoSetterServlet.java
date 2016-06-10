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
public class InfoSetterServlet extends HttpServlet {
    /**
     * Servlet used to set data for the teacher. Given a list of students, sets the teacher and requirements for the
     * students. Then it returns what everything was set to.
     * @param req the HTTP request to get to this page
     * @param resp the HTTP response to return
     * @throws IOException
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();
        try {
            // Read request params as JSON object
            JSONObject jsonObject = (JSONObject) new JSONParser().parse(req.getReader());

            // Get requirements and teacher to set for each student
            String teacher = (String) jsonObject.get("teacher");
            int total = Integer.parseInt((String) jsonObject.get("total"));
            int blank = Integer.parseInt((String) jsonObject.get("blank"));
            int scoredOver = Integer.parseInt((String) jsonObject.get("scoredOver"));
            long deadline = (Long) jsonObject.get("deadline");

            System.out.println(teacher);

            // Get list of student IDs to set requirements for
            JSONArray jsonArray = (JSONArray) jsonObject.get("id");
            Iterator iterator = jsonArray.iterator();
            List<Long> ids = new ArrayList<>();
            while(iterator.hasNext()) {
                ids.add(Long.parseLong((String) iterator.next()));
            }

            Map<Long, MCPSStudent> students = ofy().load().type(MCPSStudent.class).ids(ids);

            for(Long id : ids) {
                // Get student with ID or make new one if it doesn't exist
                MCPSStudent student = students.get(id);
                if(student == null) {
                    System.out.println("Making new student " + id);
                    student = new MCPSStudent(id);
                }
                // Set requirements for student based on request params
                student.setRequiredTotal(total);
                student.setRequiredBlank(blank);
                student.setRequiredOverScore(scoredOver);
                student.setDeadline(deadline);
                student.setTeacher(teacher);
                // Save student after requirements were set
                students.put(id, student);
            }
            ofy().save().entities(students.values()).now();
            // Return what everything was set to
            StringBuilder json = new StringBuilder("{");
            json.append("\"total\": \"" + total + "\",\n");
            json.append("\"blank\": \"" + blank + "\",\n");
            json.append("\"scoredOver\": \"" + scoredOver + "\",\n");
            json.append("\"deadline\": \"" + deadline + "\"\n");
            json.append("}");
            // Return JSON string in response
            out.println(json.toString());
            System.out.println(json.toString());
        } catch(Exception e) {
            // In case of something weird happening, success is false
            e.printStackTrace();
            out.println("{ \"success\": \"false\" }");
        }
    }
}
