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
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();
        try {
            JSONObject jsonObject = (JSONObject) new JSONParser().parse(req.getReader());

            String teacher = (String) jsonObject.get("teacher");
            int total = Integer.parseInt((String) jsonObject.get("total"));
            int blank = Integer.parseInt((String) jsonObject.get("blank"));
            int scoredOver = Integer.parseInt((String) jsonObject.get("scoredOver"));
            long deadline = (Long) jsonObject.get("deadline");
//            MCPSStudent student = ofy().load().type(MCPSStudent.class).id(Long.parseLong(email.substring(0, email.indexOf("@")))).now();
            System.out.println(teacher);


            JSONArray jsonArray = (JSONArray) jsonObject.get("id");
            Iterator iterator = jsonArray.iterator();

            while(iterator.hasNext()) {
                long id = Long.parseLong((String) iterator.next());
                MCPSStudent student = new MCPSStudent(id);
                student.setRequiredTotal(total);
                student.setRequiredBlank(blank);
                student.setRequiredOverScore(scoredOver);
                student.setDeadline(deadline);
                student.setTeacher(teacher);
                ofy().save().entity(student).now();
            }
            StringBuilder json = new StringBuilder("{");
            json.append("\"total\": \"" + total + "\",\n");
            json.append("\"blank\": \"" + blank + "\",\n");
            json.append("\"scoredOver\": \"" + scoredOver + "\",\n");
            json.append("\"deadline\": \"" + deadline + "\"\n");
            json.append("}");
            out.println(json.toString());
            System.out.println(json.toString());
        } catch(Exception e) {
            e.printStackTrace();
            out.println("{ \"success\": \"false\" }");
        }
    }
}
