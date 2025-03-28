package main.java.com.votingsystem.ballots;
import java.util.List;

public class CandidateBallot extends Ballot {
    private List<String> candidates;
    private int[] votes; // count votes for each candidate

    public CandidateBallot(List<String> candidates) {
        super(BallotType.CANDIDATE_BALLOT);
        this.candidates = candidates;
        this.votes = new int[candidates.size()];
    }

    @Override
    public void castVote(String voteOption) {
        int index = candidates.indexOf(voteOption);
        if (index != -1) {
            votes[index]++;
            System.out.println(voteOption + " casted to " + candidates.get(index));
        } else {
            System.out.println("Candidate not valid!");
        }
    }
}
