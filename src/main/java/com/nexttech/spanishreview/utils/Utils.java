package com.nexttech.spanishreview.utils;

import com.google.api.client.extensions.appengine.datastore.AppEngineDataStoreFactory;
import com.google.api.client.extensions.appengine.http.UrlFetchTransport;
import com.google.api.client.googleapis.auth.oauth2.*;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.Preconditions;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.tools.cloudstorage.*;
import com.google.gson.JsonObject;


import com.google.api.services.classroom.ClassroomScopes;
import com.google.api.services.calendar.CalendarScopes;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
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

    public static GoogleAuthorizationCodeFlow newFlow() throws IOException {
        List<String> scope = new ArrayList<String>();
        scope.add(ClassroomScopes.CLASSROOM_COURSES_READONLY);
//        System.out.println(ClassroomScopes.CLASSROOM_COURSES);
        return new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY,
                getClientCredential(),
                scope)
                .setDataStoreFactory(
                DATA_STORE_FACTORY).setAccessType("online").build();
    }

    public static GoogleAuthorizationCodeFlow teacherFlow() throws IOException {
        ArrayList<String> teacherScopes = new ArrayList<>();
        teacherScopes.add(ClassroomScopes.CLASSROOM_ROSTERS_READONLY);
        return new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY,
                getClientCredential(), teacherScopes).setDataStoreFactory(
                DATA_STORE_FACTORY).setAccessType("offline").build();
    }


//    public static String readFile(String path, Charset encoding)
//            throws IOException
//    {
//        byte[] encoded = Files.readAllBytes(Paths.get(path));
//        return new String(encoded, encoding);
//    }

    public static String readFromCloudBucket(String name) {
        GcsFilename fileName = getFileName(name);
        GcsInputChannel readChannel = gcsService.openPrefetchingReadChannel(fileName, 0, 2 * 1024 * 1024);
        ByteBuffer buffer = ByteBuffer.allocate(100);
        byte[] array = new byte[100];
        try {
            readChannel.read(buffer);
            buffer.get(array);
            return new String(array, Charset.forName("UTF-8"));
        } catch(IOException e) {
            return "";
        }
    }


    private static GcsFilename getFileName(String url) {
        String[] splits = url.split("/", 4);
        if (!splits[0].equals("") || !splits[1].equals("gcs")) {
            throw new IllegalArgumentException("The URL is not formed as expected. " +
                    "Expecting /gcs/<bucket>/<object>");
        }
        return new GcsFilename(splits[2], splits[3]);
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

}
