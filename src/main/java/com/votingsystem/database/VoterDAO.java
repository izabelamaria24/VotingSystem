package com.votingsystem.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

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

    public int saveVoterAndReturnId(String cnp, String publicKey) throws Exception {
        String sql = "INSERT INTO voters (cnp, public_key) VALUES (?, ?) RETURNING id";
        try (Connection conn = DatabaseUtil.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, cnp);
            stmt.setString(2, publicKey);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            throw new Exception("Failed to get ID of inserted voter");
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

    public List<Voter> getAllVoters() throws Exception {
        String sql = "SELECT cnp, public_key FROM voters ORDER BY id";
        List<Voter> voters = new ArrayList<>();

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String cnp = rs.getString("cnp");
                Voter voter = new Voter(cnp);
                voters.add(voter);
            }
        }
        return voters;
    }
}
