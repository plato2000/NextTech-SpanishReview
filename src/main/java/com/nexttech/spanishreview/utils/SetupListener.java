package com.nexttech.spanishreview.utils;

import com.googlecode.objectify.ObjectifyService;
import com.nexttech.spanishreview.database.MCPSStudent;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Created by plato2000 on 5/27/16.
 */
public class SetupListener implements ServletContextListener{
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        ObjectifyService.register(MCPSStudent.class);
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }
}
