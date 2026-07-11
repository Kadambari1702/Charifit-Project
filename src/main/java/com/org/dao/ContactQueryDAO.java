package com.org.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.org.ContactQuery;

public class ContactQueryDAO {

	// Retrieve all contact queries
    public List<ContactQuery> getAllQueries() {
        List<ContactQuery> list = new ArrayList<>();
        String sql = "SELECT * FROM contact_queries ORDER BY id DESC";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                ContactQuery query = new ContactQuery();
                query.setId(rs.getInt("id"));
                query.setName(rs.getString("name"));
                query.setEmail(rs.getString("email"));
                query.setSubject(rs.getString("subject"));
                query.setMessage(rs.getString("message"));
                query.setDateSubmitted(rs.getTimestamp("date_submitted"));
                list.add(query);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Add a contact query
    public boolean addQuery(ContactQuery query) {
        String sql = "INSERT INTO contact_queries (name, email, subject, message) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, query.getName());
            ps.setString(2, query.getEmail());
            ps.setString(3, query.getSubject());
            ps.setString(4, query.getMessage());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Delete a query
    public boolean deleteQuery(int id) {
        String sql = "DELETE FROM contact_queries WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

}
