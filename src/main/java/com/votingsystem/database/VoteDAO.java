package com.votingsystem.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import com.votingsystem.ballots.BallotType;

public class VoteDAO {
    private static VoteDAO instance;
    private final DatabaseUtil databaseUtil;

    private VoteDAO() {
        this.databaseUtil = DatabaseUtil.getInstance();
    }

    public static synchronized VoteDAO getInstance() {
        if (instance == null) {
            instance = new VoteDAO();
        }
        return instance;
    }

    public void saveVote(int voterId, int ballotId, String voteOption) throws Exception {
        String sql = "INSERT INTO votes (voter_id, ballot_id, vote_option) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, voterId);
            pstmt.setInt(2, ballotId);
            pstmt.setString(3, voteOption);

            pstmt.executeUpdate();
        }
    }

    public void saveVote(String voterCnp, int ballotId, String voteOption) throws Exception {
        String sql = """
            INSERT INTO votes (voter_id, ballot_id, vote_option) 
            SELECT v.id, ?, ? 
            FROM voters v 
            WHERE v.cnp = ?
        """;

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, ballotId);
            pstmt.setString(2, voteOption);
            pstmt.setString(3, voterCnp);
            pstmt.executeUpdate();
        }
    }

    public boolean hasVoted(int voterId, BallotType ballotType) throws Exception {
        String sql = """
            SELECT v.id FROM votes v 
            JOIN ballots b ON v.ballot_id = b.id 
            WHERE v.voter_id = ? AND b.type = ?
        """;

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, voterId);
            stmt.setString(2, ballotType.name());
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        }
    }

    public boolean hasVoted(String voterCnp, BallotType ballotType) throws Exception {
        String sql = """
            SELECT v.id 
            FROM votes v 
            JOIN ballots b ON v.ballot_id = b.id 
            JOIN voters vt ON v.voter_id = vt.id
            WHERE vt.cnp = ? AND b.type = ?
        """;

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, voterCnp);
            stmt.setString(2, ballotType.name());
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        }
    }

    public boolean hasVoted(String voterCnp, int ballotId) throws Exception {
        String sql = """
            SELECT v.id 
            FROM votes v 
            JOIN voters vt ON v.voter_id = vt.id
            WHERE vt.cnp = ? AND v.ballot_id = ?
        """;

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, voterCnp);
            stmt.setInt(2, ballotId);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        }
    }

    public Map<String, Integer> getVoteResults(int ballotId) throws Exception {
        String sql = """
            SELECT vote_option, COUNT(*) as vote_count 
            FROM votes 
            WHERE ballot_id = ? 
            GROUP BY vote_option
        """;

        Map<String, Integer> results = new HashMap<>();
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, ballotId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String option = rs.getString("vote_option");
                int count = rs.getInt("vote_count");
                results.put(option, count);
            }
        }
        return results;
    }
}
