package com.nexttech.spanishreview.testing;

import com.nexttech.spanishreview.worksheet.WorksheetGenerator;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class TestServlet extends HttpServlet {
    /**
     * Servlet just used for testing purposes - to do prints, etc when navigated to. This was used to test
     * worksheet generation before frontend was ready, and it was used to test the Users API.
     * @param req the HTTP request to get to this page
     * @param resp the HTTP response to return
     * @throws IOException
     */
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        PrintWriter out = resp.getWriter();
//        WorksheetGrader.getCorrectKingWorksheet();
        try {
            WorksheetGenerator generator = new WorksheetGenerator();

//            out.println(Utils.array2DToJson("ws", generator.getBlankWorksheet().getWorksheet()));
        } catch(Exception e) {
            e.printStackTrace();
        }
        out.println("Things are happening :D");
    }
}