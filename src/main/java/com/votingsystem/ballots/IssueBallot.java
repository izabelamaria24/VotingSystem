package main.java.com.votingsystem.ballots;
import java.util.List;

public class IssueBallot extends Ballot {
    private List<String> issues;
    private int[] votes;

    // votes will be yes/no for this type of ballot

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
}
