package com.votingsystem.database;

import com.votingsystem.ballots.BallotType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

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
}