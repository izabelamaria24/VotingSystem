package com.votingsystem.database;

import com.votingsystem.ballots.BallotType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BallotDAO {
    private static BallotDAO instance;

    private BallotDAO() {}

    public static BallotDAO getInstance() {
        if (instance == null) {
            instance = new BallotDAO();
        }
        return instance;
    }

    public void saveBallot(BallotType type, List<String> options) throws Exception {
        String sql = "INSERT INTO ballots (type, options) VALUES (?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, type.name());
            stmt.setString(2, String.join(",", options));
            stmt.executeUpdate();
        }
    }

    public int saveBallotAndReturnId(BallotType type, List<String> options) throws Exception {
        String sql = "INSERT INTO ballots (type, options) VALUES (?, ?) RETURNING id";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, type.name());
            stmt.setString(2, String.join(",", options));
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            throw new SQLException("Failed to get generated ballot ID");
        }
    }

    public List<String> getBallotOptions(int ballotId) throws Exception {
        String sql = "SELECT options FROM ballots WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, ballotId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String options = rs.getString("options");
                return List.of(options.split(","));
            }
        }
        return new ArrayList<>();
    }

    public List<String> getMostRecentBallotOptions(BallotType type) throws Exception {
        String sql = "SELECT options FROM ballots WHERE type = ? ORDER BY id DESC LIMIT 1";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, type.name());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String options = rs.getString("options");
                return List.of(options.split(","));
            }
        }
        return new ArrayList<>();
    }

    public int getMostRecentBallotId(BallotType type) throws Exception {
        String sql = "SELECT id FROM ballots WHERE type = ? ORDER BY id DESC LIMIT 1";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, type.name());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
            return -1; // Return -1 if no ballot found
        }
    }

    public List<Map<String, Object>> getAllBallotsOfType(BallotType type) throws Exception {
        String sql = "SELECT id, options FROM ballots WHERE type = ? ORDER BY id";
        List<Map<String, Object>> ballots = new ArrayList<>();

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, type.name());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Map<String, Object> ballot = new HashMap<>();
                ballot.put("id", rs.getInt("id"));
                ballot.put("options", List.of(rs.getString("options").split(",")));
                ballots.add(ballot);
            }
        }
        return ballots;
    }
}
