package com.votingsystem.models;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;

public class Vote {
    private Voter voter;
    private String voteOption;
    private LocalDateTime timestamp;

    public Vote(Voter voter, String voteOption, LocalDateTime timestamp) {
        this.voter = voter;
        this.voteOption = voteOption;
        this.timestamp = timestamp;
    }

    public String hashVote() {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            String input = voter.getCnp() + voteOption + timestamp;
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();

            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String toString() {
        return "Vote{" +
                "voter='" + voter + '\'' +
                ", voteOption='" + voteOption + '\'' +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }
}
