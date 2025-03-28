package main.java.com.votingsystem.blockchain;
import main.java.com.votingsystem.models.Vote;

import java.util.List;

public class Block {
    private String previousHash;
    private String hash;
    private String timestamp;

    private List<Vote> votes; // list of votes, a block has multiple votes

    public Block(String previousHash, String timestamp, List<Vote> votes) {
        this.previousHash = previousHash;
        this.timestamp = timestamp;
        this.votes = votes;

        // TODO - calculate block hash
    }

    private String calculateHash() {
        // TODO, hashing algorithm, maybe SHA-256
        return "";
    }

    public String getHash() {
        return hash;
    }

    public void displayVotes() {
        for (Vote vote : votes) {
            System.out.println(vote);
        }
    }
}
