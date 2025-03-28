package main.java.com.votingsystem.ballots;

public abstract class Ballot {
    private BallotType ballotType;

    public Ballot(BallotType ballotType) {
        this.ballotType = ballotType;
    }

    public abstract void castVote(String voteOption);
}
