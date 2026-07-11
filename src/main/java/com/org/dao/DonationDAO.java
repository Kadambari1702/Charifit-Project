package com.org.dao;

import java.sql.*;
import java.util.*;

import com.org.Donation;

public class DonationDAO {

    // ===============================
    // GET ALL DONATIONS (FIXED)
    // ===============================
    public List<Donation> getAllDonations() {

        List<Donation> list = new ArrayList<>();

        String sql =
            "SELECT d.*, c.title AS cause_title " +
            "FROM donations d " +
            "LEFT JOIN causes c ON d.cause_id = c.id " +
            "ORDER BY d.id DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                Donation d = new Donation();

                d.setId(rs.getInt("id"));
                d.setCauseId(rs.getInt("cause_id"));

                // FIX: cause name from join
                d.setCauseTitle(rs.getString("cause_title"));

                // FIX: safe column mapping (prevents your error)
                d.setDonorName(getString(rs, "donor_name", "donorName"));
                d.setPaymentMethod(getString(rs, "payment_method", "paymentMethod"));
                d.setDonorEmail(getString(rs, "donor_email", "donorEmail", "email"));

                d.setAmount(rs.getDouble("amount"));
                d.setDonationDate(getTimestamp(rs, "donation_date", "created_at", "donationDate"));
                d.setMessage(rs.getString("message"));
              
                list.add(d);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }


    // ===============================
    // ADD DONATION (TRANSACTION SAFE)
    // ===============================
    public boolean addDonation(Donation donation) {

    	String insertSql =
    		    "INSERT INTO donations " +
    		    "(cause_id, donorName, donor_email, amount, paymentMethod, message) " +
    		    "VALUES (?, ?, ?, ?, ?, ?)";

        String updateCauseSql =
            "UPDATE causes SET raised_amount = raised_amount + ? WHERE id = ?";

        Connection conn = null;

        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            // 1. Insert donation
            try (PreparedStatement ps = conn.prepareStatement(insertSql)) {

                ps.setInt(1, donation.getCauseId());
                ps.setString(2, donation.getDonorName());
                ps.setString(3, donation.getDonorEmail());
                ps.setDouble(4, donation.getAmount());
                ps.setString(5, donation.getPaymentMethod());
                ps.setString(6, donation.getMessage());

                ps.executeUpdate();
            }

            // 2. Update cause raised amount
            try (PreparedStatement ps2 = conn.prepareStatement(updateCauseSql)) {

                ps2.setDouble(1, donation.getAmount());
                ps2.setInt(2, donation.getCauseId());

                ps2.executeUpdate();
            }

            conn.commit();
            return true;

        } catch (Exception e) {

            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }

            e.printStackTrace();
            return false;

        } finally {
            try {
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    // ===============================
    // DONATION STATS
    // ===============================
    public Map<String, Object> getDonationStats() {

        Map<String, Object> stats = new HashMap<>();

        String sql =
            "SELECT COUNT(*) AS total_count, COALESCE(SUM(amount),0) AS total_raised " +
            "FROM donations";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                stats.put("totalCount", rs.getInt("total_count"));
                stats.put("totalRaised", rs.getDouble("total_raised"));
            }

        } catch (Exception e) {
            e.printStackTrace();
            stats.put("totalCount", 0);
            stats.put("totalRaised", 0.0);
        }

        return stats;
    }


    // ===============================
    // SAFE STRING READER
    // ===============================
    private String getString(ResultSet rs, String... cols) {
        for (String col : cols) {
            try {
                String val = rs.getString(col);
                if (val != null) return val;
            } catch (Exception ignored) {}
        }
        return "-";
    }


    // ===============================
    // SAFE TIMESTAMP READER
    // ===============================
    private Timestamp getTimestamp(ResultSet rs, String... cols) {
        for (String col : cols) {
            try {
                Timestamp ts = rs.getTimestamp(col);
                if (ts != null) return ts;
            } catch (Exception ignored) {}
        }
        return null;
    }
    
    public List<Donation> getDonationsByEmail(String email) {

        List<Donation> list = new ArrayList<>();

        String sql =
            "SELECT d.*, c.title AS cause_title " +
            "FROM donations d " +
            "LEFT JOIN causes c ON d.cause_id = c.id " +
            "WHERE d.donor_email = ? " +
            "ORDER BY d.id DESC";

        try (
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setString(1, email);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                Donation d = new Donation();

                d.setId(rs.getInt("id"));
                d.setCauseId(rs.getInt("cause_id"));
                d.setCauseTitle(rs.getString("cause_title"));
                d.setDonorName(getString(rs, "donor_name", "donorName"));
                d.setPaymentMethod(getString(rs, "payment_method", "paymentMethod"));
                d.setDonorEmail(rs.getString("donor_email"));
                d.setAmount(rs.getDouble("amount"));
                d.setMessage(rs.getString("message"));
                d.setDonationDate(getTimestamp(rs, "donation_date", "created_at", "donationDate"));
                list.add(d);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
}