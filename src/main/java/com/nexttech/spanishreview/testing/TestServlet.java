package com.nexttech.spanishreview.testing;

import com.nexttech.spanishreview.worksheet.WorksheetGrader;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class TestServlet extends HttpServlet {
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        PrintWriter out = resp.getWriter();
        WorksheetGrader.getCorrectKingWorksheet();
        out.println("Things are happening :D");
    }
}