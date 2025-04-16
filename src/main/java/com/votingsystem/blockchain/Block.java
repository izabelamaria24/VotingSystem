package main.java.com.votingsystem.blockchain;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.List;

public class Block {
    private String previousHash;
    private String hash;
    private String timestamp;

    private List<String> votes; // list of votes, a block has multiple votes

    public Block(String previousHash, String timestamp, List<String> votes) {
        this.previousHash = previousHash;
        this.timestamp = timestamp;
        this.votes = votes;
        this.hash = calculateHash();
    }

    private String calculateHash() {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            String input = previousHash + timestamp + votes.toString();
            byte[] hashBytes = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();

            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String getHash() {
        return hash;
    }

    public void displayVotes() {
        for (String vote : votes) {
            System.out.println(vote);
        }
    }
}
