package com.org.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.org.Volunteer;

public class VolunteerDAO {
	
	public List<Volunteer> getAllVolunteers() {
        List<Volunteer> list = new ArrayList<>();
        String sql = "SELECT * FROM volunteers ORDER BY id DESC";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

        	while (rs.next()) {
        	    Volunteer volunteer = new Volunteer();

        	    volunteer.setId(rs.getInt("id"));
        	    volunteer.setName(rs.getString("name"));
        	    volunteer.setEmail(rs.getString("email"));
        	    volunteer.setPhone(rs.getString("phone"));
        	    volunteer.setMessage(rs.getString("message"));
        	    volunteer.setStatus(rs.getString("status"));
        	    volunteer.setSignupDate(rs.getTimestamp("signup_date"));

        	    list.add(volunteer);
        	}
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Add a new volunteer signup
    public boolean addVolunteer(Volunteer volunteer) {
        String sql = "INSERT INTO volunteers (name, email, phone, message) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, volunteer.getName());
            ps.setString(2, volunteer.getEmail());
            ps.setString(3, volunteer.getPhone());
            ps.setString(4, volunteer.getMessage());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Delete a volunteer record
    public boolean deleteVolunteer(int id) {
        String sql = "DELETE FROM volunteers WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public boolean updateVolunteerStatus(int id, String status) {

        String sql =
            "UPDATE volunteers SET status=? WHERE id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setInt(2, id);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

}
