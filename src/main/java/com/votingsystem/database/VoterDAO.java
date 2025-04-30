package com.votingsystem.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.votingsystem.models.Voter;

public class VoterDAO {
    private static VoterDAO instance;

    private VoterDAO() {}

    public static VoterDAO getInstance() {
        if (instance == null) {
            instance = new VoterDAO();
        }
        return instance;
    }

    public void saveVoter(Voter voter) throws Exception {
        String sql = "INSERT INTO voters (cnp, public_key) VALUES (?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, voter.getCnp());
            stmt.setString(2, voter.getPublicKey().toString());
            stmt.executeUpdate();
        }
    }

    public Voter getVoter(String cnp) throws Exception {
        String sql = "SELECT * FROM voters WHERE cnp = ?";
        try (Connection conn = DatabaseUtil.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, cnp);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Voter(rs.getString("cnp")); // Generate keys dynamically
            }
        }
        return null;
    }
}