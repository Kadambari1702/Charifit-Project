package com.org.listener;

import javax.servlet.ServletContextListener;
import javax.servlet.ServletContextEvent;
import com.mysql.cj.jdbc.AbandonedConnectionCleanupThread;

public class AppContextListener implements ServletContextListener {

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        try {
            AbandonedConnectionCleanupThread.checkedShutdown();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        // nothing needed
    }
}