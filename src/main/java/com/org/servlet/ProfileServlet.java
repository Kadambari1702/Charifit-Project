package com.org.servlet;

import java.io.IOException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.org.User;
import com.org.dao.UserDAO;

@WebServlet("/api/profile")
public class ProfileServlet extends HttpServlet {

    private UserDAO userDAO = new UserDAO();

    protected void doGet(HttpServletRequest req,
                         HttpServletResponse resp)
            throws IOException {

        String username = req.getParameter("username");

        User user = userDAO.getUserByUsername(username);

        resp.setContentType("application/json");

        Gson gson = new Gson();
        resp.getWriter().write(gson.toJson(user));
    }
}
