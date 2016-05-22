/**
 * Copyright 2015 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nexttech.spanishreview.helloworld;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.services.classroom.Classroom;
import com.google.api.services.classroom.model.Course;
import com.google.api.services.classroom.model.ListCoursesResponse;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.nexttech.spanishreview.utils.Utils;
import com.nexttech.spanishreview.worksheet.WorksheetGenerator;
import com.google.api.client.extensions.appengine.auth.oauth2.AbstractAppEngineAuthorizationCodeServlet;
import org.json.simple.parser.ParseException;


import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class HelloServlet extends AbstractAppEngineAuthorizationCodeServlet {

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        PrintWriter out = resp.getWriter();
        UserService userService = UserServiceFactory.getUserService();
        User user = userService.getCurrentUser();
        out.println(user.getEmail());
        out.println(userService.createLogoutURL(req.getRequestURL().toString()));
        System.err.println("Hello, " + userService.getCurrentUser().getEmail());
//        try {
//            WorksheetGenerator generator = new WorksheetGenerator();
//            out.println(Utils.array2DToJson("ws", generator.getRegularWorksheet().getWorksheet()));
//        } catch(ParseException e) {
//            e.printStackTrace();
//        }
        List<Course> courses = new ArrayList<Course>();
        try {
            Classroom service = Utils.getClassroomService(true);
            ListCoursesResponse response = service.courses().list()
                    .setPageSize(10)
                    .execute();
            courses = response.getCourses();
        } catch(Exception e) {
            e.printStackTrace();
        }
        if (courses == null || courses.size() == 0) {
           out.println("No courses found.");
        } else {
            out.println("Courses:");
            for (Course course : courses) {
                out.printf("%s\n", course.getName());
            }
        }

//        out.println(Classroom.Courses.List.)
    }
    @Override
    protected String getRedirectUri(HttpServletRequest req) throws ServletException, IOException {
        return Utils.getRedirectUri(req);
    }

    @Override
    protected AuthorizationCodeFlow initializeFlow() throws IOException {
        return Utils.newFlow();
    }

}
// [END example]
