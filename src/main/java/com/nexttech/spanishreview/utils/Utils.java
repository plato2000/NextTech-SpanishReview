package com.nexttech.spanishreview.utils;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.appengine.datastore.AppEngineDataStoreFactory;
import com.google.api.client.extensions.appengine.http.UrlFetchTransport;
import com.google.api.client.googleapis.auth.oauth2.*;
import com.google.api.client.googleapis.extensions.appengine.auth.oauth2.AppIdentityCredential;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.classroom.Classroom;

import com.google.appengine.tools.cloudstorage.*;

import com.google.api.services.classroom.ClassroomScopes;

import java.io.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * Created by plato2000 on 4/23/16.
 */
public class Utils {

    // Handles JSON parsing for Google APIs
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    // Handles network stuff for Google APIs
    private static final HttpTransport HTTP_TRANSPORT = new UrlFetchTransport();

    // Handles Google App Engine Data Store
    private static final AppEngineDataStoreFactory DATA_STORE_FACTORY = AppEngineDataStoreFactory.getDefaultInstance();

    // Handles Google Cloud Storage bucket stuff - unused as of now
    private static final GcsService gcsService = GcsServiceFactory.createGcsService(new RetryParams.Builder()
            .initialRetryDelayMillis(10)
            .retryMaxAttempts(10)
            .totalRetryPeriodMillis(15000)
            .build());

    // The list of OAuth 2.0 scopes that are used for student signin
    private static Collection<String> studentScopes = Arrays.asList(
            new String[] {ClassroomScopes.CLASSROOM_COURSES_READONLY});

    // The list of OAuth 2.0 scopes that are used for teacher signin
    private static Collection<String> teacherScopes = Arrays.asList(
            new String[] {ClassroomScopes.CLASSROOM_ROSTERS_READONLY, "profile", "email"});

    // The location of the client secrets file for this project - can be retrieved from the Cloud Platform Console
    private static final String CLIENT_SECRET_FILE = "WEB-INF/private_key.json";

    // The variable that holds data from the CLIENT_SECRET_FILE
    private static GoogleClientSecrets clientSecrets = null;

    /**
     * Gets the clientSecrets from CLIENT_SECRET_FILE the first time it is called, then just returns clientSecrets
     * @return the Client Secrets (private key, other similar fields) as a GoogleClientSecrets object
     * @throws IOException
     */
    public static GoogleClientSecrets getClientCredential() throws IOException {
//        System.out.println(System.getProperty("user.dir"));
        if (clientSecrets == null) {
            clientSecrets = GoogleClientSecrets.load(
                    JacksonFactory.getDefaultInstance(), new FileReader(CLIENT_SECRET_FILE));
        }
        return clientSecrets;
    }

    /**
     * Gets the authentication verification URL for an OAuth 2.0 login request
     * @param req The HTTP request given to a page, used to get url of page
     * @return URL to redirect to
     */
    public static String getRedirectUri(HttpServletRequest req) {
        GenericUrl url = new GenericUrl(req.getRequestURL().toString());
        url.setRawPath("/oauth2callback");
        return url.build();
    }

    /**
     * Returns an authorization object that is required to make sure the program is allowed to make whatever request
     * it is trying to make
     * @param student Whether to authorize a student or a teacher - true for student
     * @return AppIdentityCredential - an object used in other calls that verifies permissions given to program
     * @throws IOException
     */
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

    /**
     * Generates an object that manages storage of OAuth 2.0 access tokens, refresh tokens, etc.
     * @return GoogleAuthorizationCodeFlow, which manages login credentials backend.
     * @throws IOException
     */
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
                DATA_STORE_FACTORY).setAccessType("offline").build();
    }

    /**
     * Same as newFlow, but asks for scopes in teacherScopes
     * @return GoogleAuthorizationCodeFlow, which manages login credentials backend.
     * @throws IOException
     */
    public static GoogleAuthorizationCodeFlow teacherFlow() throws IOException {
//        ArrayList<String> teacherScopes = new ArrayList<>();
//        teacherScopes.add(ClassroomScopes.CLASSROOM_ROSTERS_READONLY);
        return new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY,
                getClientCredential(), teacherScopes).setDataStoreFactory(
                DATA_STORE_FACTORY).setAccessType("offline").build();
    }


    /**
     * Using a credential, gets Google Classroom object which allows requests to be made from it
     * @param student true if student, false if teacher
     * @return Classroom object with authorization to make requests
     * @throws IOException
     */
    public static Classroom getClassroomService(boolean student) throws IOException {
        AppIdentityCredential credential = authorize(student);
        return new Classroom.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName("SpanishReview")
                .build();
    }

    /**
     * Converts a 2D array of objects to a JSON 2D array
     * @param name the name of the key to put the array under
     * @param array the array object to convert
     * @return JSON String in format "key": array
     */
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

    /**
     * Testing function written to output directory structure to stderr
     * @param path The relative path of which to do a file listing
     */
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
