package com.votingsystem.database;

import java.sql.Connection;

public class DatabaseTest {
    public static void main(String[] args) {
        try (Connection connection = DatabaseUtil.getConnection()) {
            System.out.println("Database connection successful!");
        } catch (Exception e) {
            System.out.println("Database connection failed: " + e.getMessage());
        }
    }
}