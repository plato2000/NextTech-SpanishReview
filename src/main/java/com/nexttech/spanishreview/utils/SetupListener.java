package com.nexttech.spanishreview.utils;

import com.googlecode.objectify.ObjectifyService;
import com.nexttech.spanishreview.database.MCPSStudent;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Created by plato2000 on 5/27/16.
 */
public class SetupListener implements ServletContextListener{
    /**
     * Called when any servlet is started. This is where setup things go.
     * @param servletContextEvent The context for the servlet starting up
     */
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        // Registers the MCPSStudent class with DataStore to teach it how to store these types of objects
        ObjectifyService.register(MCPSStudent.class);
    }

    /**
     * Called when any servlet is stopped. This is where cleanup things go.
     * @param servletContextEvent The context for the servlet stopping
     */
    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        // Nothing, yet
        // Apparently this doesn't work with App Engine anyway
    }
}
