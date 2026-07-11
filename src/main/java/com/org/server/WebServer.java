package com.org.server;

import java.io.File;
import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import com.org.servlet.AuthServlet;
import com.org.servlet.CauseServlet;
import com.org.servlet.CertificateServlet;
import com.org.servlet.ContactServlet;
import com.org.servlet.DonationServlet;
import com.org.servlet.ProfileServlet;
import com.org.servlet.RegisterServlet;
import com.org.servlet.VolunteerServlet;

public class WebServer {

    public static void main(String[] args) {

        try {
            Tomcat tomcat = new Tomcat();
            // Cloud hosts (Render, Railway, etc.) assign a port via the PORT
            // env var. Falls back to 8080 for local development.
            String portEnv = System.getenv("PORT");
            int port = (portEnv != null && !portEnv.isEmpty()) ? Integer.parseInt(portEnv) : 8080;
            tomcat.setPort(port);
            tomcat.getConnector(); // Initialize default HTTP connector

            File tempDir = new File("tomcat-temp");
            if (!tempDir.exists()) {
                tempDir.mkdirs();
            }
            tomcat.setBaseDir(tempDir.getAbsolutePath());

            // Set up webapp directory location
            File webappDir = new File("src/main/webapp/");
            
            // Check fallback paths if working directory in Eclipse is the parent workspace folder
            if (!webappDir.exists() || !new File(webappDir, "index.html").exists()) {
                File nestedDir = new File("charifit/src/main/webapp/");
                if (nestedDir.exists() && new File(nestedDir, "index.html").exists()) {
                    webappDir = nestedDir;
                } else {
                    // Try to search up to parent folders (useful for IDE integrations)
                    File parent = new File(".").getAbsoluteFile();
                    for (int i = 0; i < 4; i++) {
                        if (parent == null) break;
                        File checkDir = new File(parent, "src/main/webapp/");
                        if (checkDir.exists() && new File(checkDir, "index.html").exists()) {
                            webappDir = checkDir;
                            break;
                        }
                        File checkNested = new File(parent, "charifit/src/main/webapp/");
                        if (checkNested.exists() && new File(checkNested, "index.html").exists()) {
                            webappDir = checkNested;
                            break;
                        }
                        parent = parent.getParentFile();
                    }
                }
            }

            System.out.println("=========================================================");
            System.out.println("Tomcat Document Base: " + webappDir.getAbsolutePath());
            
            if (!new File(webappDir, "index.html").exists()) {
                System.err.println("[ERROR] 'index.html' not found in document base path!");
                System.err.println("Your IDE's Current Working Directory (CWD) is likely set incorrectly.");
                System.err.println("To fix in Eclipse:");
                System.err.println("  1. Right-click WebServer.java -> Run As -> Run Configurations...");
                System.err.println("  2. Go to 'Arguments' tab.");
                System.err.println("  3. Under 'Working Directory', select 'Other' -> choose your local 'charifit' directory.");
                System.err.println("=========================================================");
            } else {
                System.out.println("Successfully located frontend files (index.html, CSS, JS)!");
                System.out.println("=========================================================");
            }

            Context context = tomcat.addWebapp("", webappDir.getAbsolutePath());

            // Auth Servlet
            Tomcat.addServlet(
                    context,
                    "AuthServlet",
                    AuthServlet.class.getName());

            context.addServletMappingDecoded(
                    "/api/auth/*",
                    "AuthServlet");

            // Register Servlet
            Tomcat.addServlet(
                    context,
                    "RegisterServlet",
                    RegisterServlet.class.getName());

            context.addServletMappingDecoded(
                    "/register",
                    "RegisterServlet");

            // Cause Servlet
            Tomcat.addServlet(
                    context,
                    "CauseServlet",
                    CauseServlet.class.getName());

            context.addServletMappingDecoded(
                    "/api/causes/*",
                    "CauseServlet");

            context.addServletMappingDecoded(
                    "/api/causes",
                    "CauseServlet");

            // Donation Servlet
            Tomcat.addServlet(
                    context,
                    "DonationServlet",
                    DonationServlet.class.getName());

            context.addServletMappingDecoded(
                    "/api/donations/*",
                    "DonationServlet");

            context.addServletMappingDecoded(
                    "/api/donations",
                    "DonationServlet");

            // Volunteer Servlet
            Tomcat.addServlet(
                    context,
                    "VolunteerServlet",
                    VolunteerServlet.class.getName());

            context.addServletMappingDecoded(
                    "/api/volunteers/*",
                    "VolunteerServlet");

            context.addServletMappingDecoded(
                    "/api/volunteers",
                    "VolunteerServlet");

            // Contact Servlet
            Tomcat.addServlet(
                    context,
                    "ContactServlet",
                    ContactServlet.class.getName());

            context.addServletMappingDecoded(
                    "/api/contacts/*",
                    "ContactServlet");

            context.addServletMappingDecoded(
                    "/api/contacts",
                    "ContactServlet");

            // Certificate Servlet
            Tomcat.addServlet(
                    context,
                    "CertificateServlet",
                    CertificateServlet.class.getName());

            context.addServletMappingDecoded(
                    "/certificate",
                    "CertificateServlet");

            // Profile Servlet
            Tomcat.addServlet(
                    context,
                    "ProfileServlet",
                    ProfileServlet.class.getName());

            context.addServletMappingDecoded(
                    "/api/profile",
                    "ProfileServlet");

            System.out.println("==================================");
            System.out.println("Charifit Server Started");
            System.out.println("http://localhost:" + port);
            System.out.println("==================================");

            tomcat.start();
            tomcat.getServer().await();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
