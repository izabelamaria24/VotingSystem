package main.java.com.votingsystem.ballots;

public abstract class Ballot {
    private String ballotType;

    public Ballot(String ballotType) {
        this.ballotType = ballotType;
    }

    public abstract void castVote(String voteOption);
}
