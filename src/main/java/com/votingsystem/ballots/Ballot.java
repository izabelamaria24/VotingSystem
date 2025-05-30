package com.votingsystem.ballots;

public abstract class Ballot {
    private BallotType ballotType;

    public Ballot(BallotType ballotType) {
        this.ballotType = ballotType;
    }

    public abstract void castVote(String voteOption);

    public void getResults() {
        System.out.println("Results for " + ballotType + "ballot:");
    }

    public BallotType getBallotType() {
        return ballotType;
    }
}
