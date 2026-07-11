package com.org.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.org.User;
import com.org.dao.UserDAO;

@WebServlet("/api/auth/*")
public class AuthServlet extends HttpServlet {

	private final UserDAO userDAO = new UserDAO();
	private final Gson gson = new Gson();

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		resp.setContentType("application/json");
		resp.setCharacterEncoding("UTF-8");

		String pathInfo = req.getPathInfo();
		JsonObject jsonResponse = new JsonObject();

		if ("/login".equals(pathInfo)) {

			JsonObject jsonRequest = gson.fromJson(req.getReader(), JsonObject.class);

			String username = jsonRequest.get("username").getAsString();
			String password = jsonRequest.get("password").getAsString();

			User user = userDAO.authenticate(username, password);

			if (user != null) {

				HttpSession session = req.getSession(true);

				// 🔥 IMPORTANT FIX
				session.setAttribute("adminUser", user);

				jsonResponse.addProperty("success", true);
				jsonResponse.addProperty("message", "Login successful");
				jsonResponse.addProperty("role", user.getRole());
				jsonResponse.addProperty("username", user.getUsername());
				jsonResponse.addProperty("fullName", user.getFullName());
				jsonResponse.addProperty("email", user.getEmail());

			} else {
				resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				jsonResponse.addProperty("success", false);
				jsonResponse.addProperty("message", "Invalid credentials");
			}
		}

		resp.getWriter().write(gson.toJson(jsonResponse));
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

		resp.setContentType("application/json");

		String path = req.getPathInfo();
		JsonObject res = new JsonObject();

		if ("/status".equals(path)) {

			HttpSession session = req.getSession(false);

			// 🔥 MUST MATCH login session key
			if (session != null && session.getAttribute("adminUser") != null) {

				User user = (User) session.getAttribute("adminUser");

				res.addProperty("authenticated", true);

				JsonObject u = new JsonObject();
				u.addProperty("username", user.getUsername());
				u.addProperty("fullName", user.getFullName());
				u.addProperty("role", user.getRole());
				u.addProperty("email",user.getEmail());

				res.add("user", u);

			} else {
				res.addProperty("authenticated", false);
			}
		}

		resp.getWriter().write(gson.toJson(res));
	}
}