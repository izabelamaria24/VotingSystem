package main.java.com.votingsystem.security;

import java.util.List;
import java.util.ArrayList;

public class AuditLog {
    private static List<String> logs = new ArrayList<>();

    public static void log(String action) {
        String logEntry = java.time.LocalDateTime.now() + ": " + action;
        logs.add(logEntry);
        System.out.println(logEntry);
    }

    public static List<String> getLogs() {
        return logs;
    }
}
