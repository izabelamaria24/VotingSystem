package com.votingsystem.ballots;
import java.util.List;

public class CandidateBallot extends Ballot {
    private List<String> candidates;
    private int[] votes; 

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

    @Override
    public void getResults() {
        System.out.println("Candidate Election Results:");
        for (int i = 0; i < candidates.size(); i++) {
            System.out.println(candidates.get(i) + ": " + votes[i] + " votes");
        }
    }
}
