package com.org.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.org.User;
import com.org.dao.UserDAO;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    private UserDAO userDAO = new UserDAO();

    protected void doPost(HttpServletRequest req,
                          HttpServletResponse resp)
            throws ServletException, IOException {

        User user = new User();
        
        user.setFullName(req.getParameter("fullName"));
        user.setUsername(req.getParameter("username"));
        user.setEmail(req.getParameter("email"));
        user.setPassword(req.getParameter("password"));
        user.setMobile(req.getParameter("mobile"));
        user.setDob(req.getParameter("dob"));
        user.setGender(req.getParameter("gender"));
        user.setAddress(req.getParameter("address"));
        user.setRole("user");

        if(userDAO.register(user)) {
            resp.sendRedirect("user-login.html");
        } else {
            resp.getWriter().println("Registration Failed");
        }
        
    }
}

