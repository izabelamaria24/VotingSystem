package com.votingsystem.ballots;
import java.util.List;

public class IssueBallot extends Ballot {
    private List<String> issues;
    private int[] votes;

    public IssueBallot(List<String> issues) {
        super(BallotType.ISSUE_BALLOT);
        this.issues = issues;
        this.votes = new int[issues.size()];
    }

    @Override
    public void castVote(String voteOption) {
        int index = issues.indexOf(voteOption);
        if (index != -1) {
            votes[index]++;
            System.out.println("Vote casted for issue: " + voteOption);
        } else {
            System.out.println("Invalid issue!");
        }
    }

    @Override
    public void getResults() {
        System.out.println("Issue Voting Results:");
        for (int i = 0; i < issues.size(); i++) {
            System.out.println(issues.get(i) + ": " + votes[i] + " votes");
        }
    }
}
