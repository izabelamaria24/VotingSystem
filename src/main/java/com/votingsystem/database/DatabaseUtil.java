package com.votingsystem.database;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class DatabaseUtil {
    private static final String URL;
    private static final String USER;
    private static final String PASSWORD;

    static {
        try (InputStream input = DatabaseUtil.class.getClassLoader().getResourceAsStream("db.properties")) {
            Properties props = new Properties();
            props.load(input);
            URL = props.getProperty("db.url");
            USER = props.getProperty("db.user");
            PASSWORD = props.getProperty("db.password");
        } catch (Exception e) {
            throw new RuntimeException("Failed to load database properties", e);
        }
    }

    public static Connection getConnection() throws Exception {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}