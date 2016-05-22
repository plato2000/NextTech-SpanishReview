package com.nexttech.spanishreview.utils;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.appengine.datastore.AppEngineDataStoreFactory;
import com.google.api.client.extensions.appengine.http.UrlFetchTransport;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.googleapis.auth.oauth2.*;
import com.google.api.client.googleapis.extensions.appengine.auth.oauth2.AppIdentityCredential;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.Preconditions;
import com.google.api.services.classroom.Classroom;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.tools.cloudstorage.*;
import com.google.gson.JsonObject;


import com.google.api.services.classroom.ClassroomScopes;
import com.google.api.services.calendar.CalendarScopes;

import java.io.*;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.simple.*;
import org.json.simple.parser.JSONParser;

import javax.servlet.http.HttpServletRequest;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.charset.Charset;
//import java.nio.file.Files;
//import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.util.*;

/**
 * Created by plato2000 on 4/23/16.
 */
public class Utils {

    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final HttpTransport HTTP_TRANSPORT = new UrlFetchTransport();
    private static final AppEngineDataStoreFactory DATA_STORE_FACTORY = AppEngineDataStoreFactory.getDefaultInstance();
    private static final String CLIENT_SECRET_FILE = "WEB-INF/private_key.json";
    private static final GcsService gcsService = GcsServiceFactory.createGcsService(new RetryParams.Builder()
            .initialRetryDelayMillis(10)
            .retryMaxAttempts(10)
            .totalRetryPeriodMillis(15000)
            .build());
    private static Collection<String> studentScopes = Arrays.asList(
            new String[] {ClassroomScopes.CLASSROOM_COURSES_READONLY});
    private static Collection<String> teacherScopes = Arrays.asList(
            new String[] {ClassroomScopes.CLASSROOM_ROSTERS_READONLY, "profile", "email"});


    public static String getRedirectUri(HttpServletRequest req) {
        GenericUrl url = new GenericUrl(req.getRequestURL().toString());
        url.setRawPath("/oauth2callback");
        return url.build();
    }
    private static GoogleClientSecrets clientSecrets = null;

    public static GoogleClientSecrets getClientCredential() throws IOException {
//        System.out.println(System.getProperty("user.dir"));
        if (clientSecrets == null) {
            clientSecrets = GoogleClientSecrets.load(
                    JacksonFactory.getDefaultInstance(), new FileReader(CLIENT_SECRET_FILE));
        }
        return clientSecrets;
    }
    public static AppIdentityCredential authorize(boolean student) throws IOException {
        // Load client secrets.
//        InputStream in =
//                Utils.class.getResourceAsStream(CLIENT_SECRET_FILE);
////        errorPath("");
////        errorPath("WEB-INF");
//        System.out.println(in);
        GoogleClientSecrets clientSecrets = getClientCredential();
//        System.out.println(in);
        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = newFlow();
        AppIdentityCredential credential = new AppIdentityCredential(student ? studentScopes : teacherScopes);
        return credential;
    }

    public static GoogleAuthorizationCodeFlow newFlow() throws IOException {
//        List<String> scope = new ArrayList<String>();
//        scope.add(ClassroomScopes.CLASSROOM_COURSES_READONLY);
//        scope.add("profile");
//        scope.add("email");
//        System.out.println(ClassroomScopes.CLASSROOM_COURSES);
        return new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY,
                getClientCredential(),
                studentScopes)
                .setDataStoreFactory(
                DATA_STORE_FACTORY).setAccessType("online").build();
    }

    public static GoogleAuthorizationCodeFlow teacherFlow() throws IOException {
//        ArrayList<String> teacherScopes = new ArrayList<>();
//        teacherScopes.add(ClassroomScopes.CLASSROOM_ROSTERS_READONLY);
        return new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY,
                getClientCredential(), teacherScopes).setDataStoreFactory(
                DATA_STORE_FACTORY).setAccessType("offline").build();
    }


    public static Classroom getClassroomService(boolean student) throws IOException {
        AppIdentityCredential credential = authorize(student);
        return new Classroom.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName("SpanishReview")
                .build();
    }

    public static String array2DToJson(String name, Object[][] array) {
        StringBuilder s = new StringBuilder();
        s.append("{ \"" + name + "\": [\n");
        for(int i = 0; i < array.length; i++) {
            if(i != 0) {
                s.append(",\n");
            }
            s.append("\t[");
            for(int j = 0; j < array[i].length; j++) {
                if(j != 0) {
                   s.append(", ");
                }
                s.append("\"" + array[i][j].toString() + "\"");
            }
            s.append("]");
        }
        s.append("\n\t]\n}");
        return s.toString();
    }

    public static void errorPath(String path) {
        File folder = new File(path);
        File[] listOfFiles = folder.listFiles();

        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                System.err.println("File " + listOfFiles[i].getName());
            } else if (listOfFiles[i].isDirectory()) {
                System.err.println("Directory " + listOfFiles[i].getName());
            }
        }
    }

}
