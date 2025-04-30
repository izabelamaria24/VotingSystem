package com.votingsystem.database;

import java.sql.Connection;
import java.sql.Statement;

public class SetupDatabase {
    public static void main(String[] args) {
        try (Connection connection = DatabaseUtil.getConnection();
            Statement statement = connection.createStatement()) {

            statement.executeUpdate("""
                CREATE TABLE IF NOT EXISTS voters (
                    id SERIAL PRIMARY KEY,
                    cnp VARCHAR(50) UNIQUE NOT NULL,
                    public_key TEXT NOT NULL
                );
            """);

            statement.executeUpdate("""
                CREATE TABLE IF NOT EXISTS ballots (
                    id SERIAL PRIMARY KEY,
                    type VARCHAR(50) NOT NULL,
                    options TEXT NOT NULL
                );
            """);

            statement.executeUpdate("""
                CREATE TABLE IF NOT EXISTS votes (
                    id SERIAL PRIMARY KEY,
                    voter_id INT REFERENCES voters(id),
                    ballot_id INT REFERENCES ballots(id),
                    vote_option TEXT NOT NULL,
                    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                );
            """);

            System.out.println("Tables created successfully!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}