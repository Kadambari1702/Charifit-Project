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
import com.org.ContactQuery;
import com.org.dao.ContactQueryDAO;

@WebServlet("/api/contacts")
public class ContactServlet extends HttpServlet {
    private final ContactQueryDAO contactDAO = new ContactQueryDAO();
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

        List<ContactQuery> list = contactDAO.getAllQueries();
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.getWriter().write(gson.toJson(list));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        JsonObject jsonResponse = new JsonObject();

        try {
            ContactQuery query = gson.fromJson(req.getReader(), ContactQuery.class);
            if (query == null || query.getName() == null || query.getName().trim().isEmpty() ||
                query.getEmail() == null || query.getEmail().trim().isEmpty() ||
                query.getSubject() == null || query.getSubject().trim().isEmpty() ||
                query.getMessage() == null || query.getMessage().trim().isEmpty()) {
                
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                jsonResponse.addProperty("success", false);
                jsonResponse.addProperty("message", "Missing required fields");
                resp.getWriter().write(gson.toJson(jsonResponse));
                return;
            }

            boolean success = contactDAO.addQuery(query);
            if (success) {
                resp.setStatus(HttpServletResponse.SC_CREATED);
                jsonResponse.addProperty("success", true);
                jsonResponse.addProperty("message", "Thank you! Your message has been sent successfully.");
            } else {
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                jsonResponse.addProperty("success", false);
                jsonResponse.addProperty("message", "Database error: Failed to record message.");
            }
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            jsonResponse.addProperty("success", false);
            jsonResponse.addProperty("message", "Error: " + e.getMessage());
        }

        resp.getWriter().write(gson.toJson(jsonResponse));
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
            boolean success = contactDAO.deleteQuery(id);
            if (success) {
                resp.setStatus(HttpServletResponse.SC_OK);
                jsonResponse.addProperty("success", true);
                jsonResponse.addProperty("message", "Contact query deleted successfully");
            } else {
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                jsonResponse.addProperty("success", false);
                jsonResponse.addProperty("message", "Failed to delete record");
            }
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            jsonResponse.addProperty("success", false);
            jsonResponse.addProperty("message", "Invalid ID format");
        }

        resp.getWriter().write(gson.toJson(jsonResponse));
    }
}



