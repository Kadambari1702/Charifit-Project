package com.org.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.org.Cause;

public class CauseDAO {

	// Retrieve all causes
    public List<Cause> getAllCauses() {
        List<Cause> list = new ArrayList<>();
        String sql = "SELECT * FROM causes ORDER BY id DESC";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Cause cause = new Cause();
                cause.setId(rs.getInt("id"));
                cause.setTitle(rs.getString("title"));
                cause.setDescription(rs.getString("description"));
                cause.setImageUrl(rs.getString("image_url"));
                cause.setGoalAmount(rs.getDouble("goal_amount"));
                cause.setRaisedAmount(rs.getDouble("raised_amount"));
                cause.setStatus(rs.getString("status"));
                cause.setCreatedAt(rs.getTimestamp("created_at"));
                list.add(cause);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Retrieve a single cause by ID
    public Cause getCauseById(int id) {
        String sql = "SELECT * FROM causes WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Cause cause = new Cause();
                    cause.setId(rs.getInt("id"));
                    cause.setTitle(rs.getString("title"));
                    cause.setDescription(rs.getString("description"));
                    cause.setImageUrl(rs.getString("image_url"));
                    cause.setGoalAmount(rs.getDouble("goal_amount"));
                    cause.setRaisedAmount(rs.getDouble("raised_amount"));
                    cause.setStatus(rs.getString("status"));
                    cause.setCreatedAt(rs.getTimestamp("created_at"));
                    return cause;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Add a new cause
    public boolean addCause(Cause cause) {
        String sql = "INSERT INTO causes (title, description, image_url, goal_amount, status) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, cause.getTitle());
            ps.setString(2, cause.getDescription());
            ps.setString(3, cause.getImageUrl() != null && !cause.getImageUrl().trim().isEmpty() ? cause.getImageUrl() : "img/cause_placeholder.jpg");
            ps.setDouble(4, cause.getGoalAmount());
            ps.setString(5, cause.getStatus() != null ? cause.getStatus() : "active");
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Update an existing cause
    public boolean updateCause(Cause cause) {
        String sql = "UPDATE causes SET title = ?, description = ?, image_url = ?, goal_amount = ?, status = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, cause.getTitle());
            ps.setString(2, cause.getDescription());
            ps.setString(3, cause.getImageUrl());
            ps.setDouble(4, cause.getGoalAmount());
            ps.setString(5, cause.getStatus());
            ps.setInt(6, cause.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Delete a cause
    public boolean deleteCause(int id) {
        String sql = "DELETE FROM causes WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Update raised amount (called on new donation)
    public boolean incrementRaisedAmount(int causeId, double amount) {
        String sql = "UPDATE causes SET raised_amount = raised_amount + ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, amount);
            ps.setInt(2, causeId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
