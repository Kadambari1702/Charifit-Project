package com.org.servlet;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.org.Volunteer;
import com.org.dao.VolunteerDAO;



@WebServlet("/api/volunteers")
public class VolunteerServlet extends HttpServlet {
    private final VolunteerDAO volunteerDAO = new VolunteerDAO();
    private final Gson gson = new Gson();

    private boolean isAdmin(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        return session != null && session.getAttribute("adminUser") != null;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        if (!isAdmin(req)) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            JsonObject json = new JsonObject();
            json.addProperty("error", "Unauthorized access. Admin login required.");
            resp.getWriter().write(gson.toJson(json));
            return;
        }

        List<Volunteer> list = volunteerDAO.getAllVolunteers();
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.getWriter().write(gson.toJson(list));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        JsonObject jsonResponse = new JsonObject();

        try {
        	Volunteer volunteer = gson.fromJson(req.getReader(), Volunteer.class);

        	System.out.println("Volunteer Name: " + volunteer.getName());
        	System.out.println("Volunteer Email: " + volunteer.getEmail());
        	System.out.println("Volunteer Phone: " + volunteer.getPhone());
        	System.out.println("Volunteer Message: " + volunteer.getMessage());
        	if (volunteer == null || volunteer.getName() == null || volunteer.getName().trim().isEmpty() ||
                volunteer.getEmail() == null || volunteer.getEmail().trim().isEmpty() ||
                volunteer.getPhone() == null || volunteer.getPhone().trim().isEmpty()) {
                
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                jsonResponse.addProperty("success", false);
                jsonResponse.addProperty("message", "Missing required fields (name, email, phone)");
                resp.getWriter().write(gson.toJson(jsonResponse));
                return;
            }
        	System.out.println("Trying to save volunteer...");

            boolean success = volunteerDAO.addVolunteer(volunteer);
            System.out.println("Save result = " + success);
            if (success) {
                resp.setStatus(HttpServletResponse.SC_CREATED);
                jsonResponse.addProperty("success", true);
                jsonResponse.addProperty("message", "Thank you for signing up! We will contact you soon.");
            } else {
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                jsonResponse.addProperty("success", false);
                jsonResponse.addProperty("message", "Database error: Failed to record volunteer details.");
            }
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            jsonResponse.addProperty("success", false);
            jsonResponse.addProperty("message", "Error: " + e.getMessage());
        }

        resp.getWriter().write(gson.toJson(jsonResponse));
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("application/json");
        JsonObject json = new JsonObject();

        if (!isAdmin(req)) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            json.addProperty("success", false);
            json.addProperty("message", "Unauthorized");
            resp.getWriter().write(gson.toJson(json));
            return;
        }

        JsonObject requestData =
                gson.fromJson(req.getReader(), JsonObject.class);

        int id = requestData.get("id").getAsInt();
        String status = requestData.get("status").getAsString();

        boolean success =
                volunteerDAO.updateVolunteerStatus(id, status);

        json.addProperty("success", success);

        resp.getWriter().write(gson.toJson(json));
    }
    
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        JsonObject jsonResponse = new JsonObject();

        if (!isAdmin(req)) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            jsonResponse.addProperty("success", false);
            jsonResponse.addProperty("message", "Unauthorized access. Admin login required.");
            resp.getWriter().write(gson.toJson(jsonResponse));
            return;
        }

        String idParam = req.getParameter("id");
        if (idParam == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            jsonResponse.addProperty("success", false);
            jsonResponse.addProperty("message", "Missing id parameter");
            resp.getWriter().write(gson.toJson(jsonResponse));
            return;
        }

        try {
            int id = Integer.parseInt(idParam);
            boolean success = volunteerDAO.deleteVolunteer(id);
            if (success) {
                resp.setStatus(HttpServletResponse.SC_OK);
                jsonResponse.addProperty("success", true);
                jsonResponse.addProperty("message", "Volunteer record removed successfully");
            } else {
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                jsonResponse.addProperty("success", false);
                jsonResponse.addProperty("message", "Failed to remove record");
            }
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            jsonResponse.addProperty("success", false);
            jsonResponse.addProperty("message", "Invalid ID format");
        }
        
        

        resp.getWriter().write(gson.toJson(jsonResponse));
    }
    
}


