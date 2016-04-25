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

package com.nexttech.spanishreview.requestthings;


import com.google.gson.JsonObject;
import com.nexttech.spanishreview.utils.Utils;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import org.json.simple.JSONObject;


// [START example]
@WebServlet(name = "loginhandler", value = "")
@SuppressWarnings("serial")
public class LoginHandlerServlet extends HttpServlet {

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
//        System.out.println(request.getParameter("idtoken"));
        JSONObject idToken = Utils.verifyID(request.getParameter("idtoken"));
        try {
            if (idToken != null && idToken.get("hd").equals("mcpsmd.net")) {
                out.println("{ \"sign_in\": true, \"name\": \"" + idToken.get("name") + "\"}");
                System.out.println(idToken.get("name") + " signed in.");
            } else {
                out.println("{ \"sign_in\": \"false\" }");
            }
        } catch(NullPointerException e) {
            e.printStackTrace();
            out.println("{ \"sign_in\": \"false\"}");
        }
        out.close();
        System.out.println("Reached end of post thingy");
    }

}
// [END example]
