package com.org;

import java.sql.Connection;
import com.org.dao.DBConnection;

public class TestDB {

    public static void main(String[] args) {

        try {
            Connection con = DBConnection.getConnection();

            if (con != null) {
                System.out.println("Database Connected Successfully!");
                con.close();
            } else {
                System.out.println("Connection is NULL");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}