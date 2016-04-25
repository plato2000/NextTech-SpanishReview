package com.nexttech.spanishreview.utils;

import com.google.api.client.extensions.appengine.http.UrlFetchTransport;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.simple.*;
import org.json.simple.parser.JSONParser;

import java.security.GeneralSecurityException;
import java.util.Arrays;

/**
 * Created by plato2000 on 4/23/16.
 */
public class Utils {

    public static GsonFactory jsonFactory = new GsonFactory();
    public static HttpTransport transport = new UrlFetchTransport();


    public static String getHTML(String urlToRead) throws Exception {
        StringBuilder result = new StringBuilder();
        URL url = new URL(urlToRead);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String line;
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }
        rd.close();
        return result.toString();
    }

    public static JSONObject verifyID(String idTokenString) {
//        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(transport, jsonFactory)
//                .setAudience(Arrays.asList("1081294254756-cs889poi4i8qr3bqtc7gv42fhmsccn8g.apps.googleusercontent.com"))
//                // If you retrieved the token on Android using the Play Services 8.3 API or newer, set
//                // the issuer to "https://accounts.google.com". Otherwise, set the issuer to
//                // "accounts.google.com". If you need to verify tokens from multiple sources, build
//                // a GoogleIdTokenVerifier for each issuer and try them both.
//                .setIssuer("accounts.google.com")
//                .build();
//        String returnedStringFromVerification = getHTML("https://www.googleapis.com/oauth2/v3/tokeninfo?id_token=" + idTokenString);

// (Receive idTokenString by HTTPS POST)
        try {
            String returnedStringFromVerification = getHTML("https://www.googleapis.com/oauth2/v3/tokeninfo?id_token=" + idTokenString);
            System.out.println(returnedStringFromVerification);
            JSONObject json = (JSONObject)new JSONParser().parse(returnedStringFromVerification);
            if(json.get("aud").equals("1081294254756-cs889poi4i8qr3bqtc7gv42fhmsccn8g.apps.googleusercontent.com")) {
                if(json.get("iss").equals("accounts.google.com") || json.get("iss").equals("https://accounts.google.com")) {
//                    System.out.println("exp: " + Long.getLong("12"));
                    if(Long.parseLong((String) json.get("exp")) >= (System.currentTimeMillis() % 1000)) {
                        return json;
                    }
                }
            }
            return null;
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
