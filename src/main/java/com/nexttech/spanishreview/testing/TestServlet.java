package com.nexttech.spanishreview.testing;

import com.nexttech.spanishreview.utils.PrettyPrinter;
import com.nexttech.spanishreview.utils.Utils;
import com.nexttech.spanishreview.worksheet.WorksheetGenerator;
import com.nexttech.spanishreview.worksheet.WorksheetGrader;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class TestServlet extends HttpServlet {
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