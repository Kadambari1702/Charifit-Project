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
import com.org.Cause;
import com.org.dao.CauseDAO;

@WebServlet("/api/causes")
public class CauseServlet extends HttpServlet {
    private final CauseDAO causeDAO = new CauseDAO();
    private final Gson gson = new Gson();

    private boolean isAdmin(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        return session != null && session.getAttribute("adminUser") != null;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        String idParam = req.getParameter("id");
        if (idParam != null) {
            try {
                int id = Integer.parseInt(idParam);
                Cause cause = causeDAO.getCauseById(id);
                if (cause != null) {
                    resp.setStatus(HttpServletResponse.SC_OK);
                    resp.getWriter().write(gson.toJson(cause));
                } else {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    JsonObject json = new JsonObject();
                    json.addProperty("error", "Cause not found");
                    resp.getWriter().write(gson.toJson(json));
                }
            } catch (NumberFormatException e) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                JsonObject json = new JsonObject();
                json.addProperty("error", "Invalid ID format");
                resp.getWriter().write(gson.toJson(json));
            }
        } else {
            List<Cause> list = causeDAO.getAllCauses();
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(gson.toJson(list));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
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

        try {
            Cause cause = gson.fromJson(req.getReader(), Cause.class);
            if (cause == null || cause.getTitle() == null || cause.getTitle().trim().isEmpty() || cause.getGoalAmount() <= 0) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                jsonResponse.addProperty("success", false);
                jsonResponse.addProperty("message", "Missing or invalid required fields (title, goalAmount)");
                resp.getWriter().write(gson.toJson(jsonResponse));
                return;
            }

            boolean success = causeDAO.addCause(cause);
            if (success) {
                resp.setStatus(HttpServletResponse.SC_CREATED);
                jsonResponse.addProperty("success", true);
                jsonResponse.addProperty("message", "Cause created successfully");
            } else {
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                jsonResponse.addProperty("success", false);
                jsonResponse.addProperty("message", "Failed to insert cause into database");
            }
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            jsonResponse.addProperty("success", false);
            jsonResponse.addProperty("message", "Error: " + e.getMessage());
        }

        resp.getWriter().write(gson.toJson(jsonResponse));
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
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

        try {
            Cause cause = gson.fromJson(req.getReader(), Cause.class);
            if (cause == null || cause.getId() <= 0 || cause.getTitle() == null || cause.getTitle().trim().isEmpty() || cause.getGoalAmount() <= 0) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                jsonResponse.addProperty("success", false);
                jsonResponse.addProperty("message", "Missing or invalid required fields");
                resp.getWriter().write(gson.toJson(jsonResponse));
                return;
            }

            boolean success = causeDAO.updateCause(cause);
            if (success) {
                resp.setStatus(HttpServletResponse.SC_OK);
                jsonResponse.addProperty("success", true);
                jsonResponse.addProperty("message", "Cause updated successfully");
            } else {
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                jsonResponse.addProperty("success", false);
                jsonResponse.addProperty("message", "Failed to update cause in database");
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
            boolean success = causeDAO.deleteCause(id);
            if (success) {
                resp.setStatus(HttpServletResponse.SC_OK);
                jsonResponse.addProperty("success", true);
                jsonResponse.addProperty("message", "Cause deleted successfully");
            } else {
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                jsonResponse.addProperty("success", false);
                jsonResponse.addProperty("message", "Failed to delete cause (ID may not exist)");
            }
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            jsonResponse.addProperty("success", false);
            jsonResponse.addProperty("message", "Invalid ID format");
        }

        resp.getWriter().write(gson.toJson(jsonResponse));
    }
}



