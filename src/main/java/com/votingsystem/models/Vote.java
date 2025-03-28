package main.java.com.votingsystem.models;

public class Vote {
    private String voterCNP;
    private String voteOption;
    private String timestamp;

    public Vote(String voterCNP, String voteOption, String timestamp) {
        this.voterCNP = voterCNP;
        this.voteOption = voteOption;
        this.timestamp = timestamp;
    }
}
