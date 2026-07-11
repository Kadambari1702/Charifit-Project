package com.org.servlet;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.org.Donation;
import com.org.dao.DonationDAO;

@WebServlet("/api/donations")
public class DonationServlet extends HttpServlet {
    private final DonationDAO donationDAO = new DonationDAO();
    private final Gson gson = new Gson();

    private boolean isAdmin(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        return session != null && session.getAttribute("adminUser") != null;
    }
        

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        String action = req.getParameter("action");

        // Public stats
        if ("stats".equalsIgnoreCase(action)) {

            Map<String, Object> stats = donationDAO.getDonationStats();

            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(gson.toJson(stats));
            return;
        }

        // User donations
        if ("mydonations".equalsIgnoreCase(action)) {

            String email = req.getParameter("email");

            List<Donation> list = donationDAO.getDonationsByEmail(email);

            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(gson.toJson(list));
            return;
        }

        // Admin donations list
        if (!isAdmin(req)) {

            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

            JsonObject json = new JsonObject();
            json.addProperty("error",
                    "Unauthorized access. Admin login required.");

            resp.getWriter().write(gson.toJson(json));
            return;
        }

        List<Donation> list = donationDAO.getAllDonations();

        resp.setStatus(HttpServletResponse.SC_OK);
        resp.getWriter().write(gson.toJson(list));
    }
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        JsonObject jsonResponse = new JsonObject();

        try {
            Donation donation = gson.fromJson(req.getReader(), Donation.class);
            String certificateId =
                    "CF-" +
                    java.time.LocalDate.now().getYear() +
                    "-" +
                    System.currentTimeMillis();

            donation.setCertificateId(certificateId);
            if (donation == null || donation.getCauseId() <= 0 || donation.getDonorName() == null || 
                donation.getDonorName().trim().isEmpty() || donation.getDonorEmail() == null || 
                donation.getDonorEmail().trim().isEmpty() || donation.getAmount() <= 0 || 
                donation.getPaymentMethod() == null || donation.getPaymentMethod().trim().isEmpty()) {
                
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                jsonResponse.addProperty("success", false);
                jsonResponse.addProperty("message", "Missing or invalid donation details");
                resp.getWriter().write(gson.toJson(jsonResponse));
                return;
            }

            boolean success = donationDAO.addDonation(donation);
            if (success) {
                resp.setStatus(HttpServletResponse.SC_CREATED);
                jsonResponse.addProperty("success", true);
                jsonResponse.addProperty("message", "Thank you! Your donation was processed successfully.");
            } else {
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                jsonResponse.addProperty("success", false);
                jsonResponse.addProperty("message", "Database error: Failed to record donation. Please check if the cause exists.");
            }
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            jsonResponse.addProperty("success", false);
            jsonResponse.addProperty("message", "Error processing donation: " + e.getMessage());
        }

        resp.getWriter().write(gson.toJson(jsonResponse));
    }
    
    
}


