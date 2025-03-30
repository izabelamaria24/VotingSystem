package main.java.com.votingsystem.system;
import main.java.com.votingsystem.ballots.*;
import main.java.com.votingsystem.blockchain.Blockchain;
import main.java.com.votingsystem.models.Voter;

import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class VotingSystem {
    private Blockchain blockchain;
    private Map<String, Voter> voters;
    private Map<BallotType, Ballot> ballots;

    public VotingSystem() {
        blockchain = new Blockchain();
        voters = new HashMap<>();
        ballots = new HashMap<>();
    }

    public void addVoter(Voter voter) {
        voters.put(voter.getCnp(), voter);
    }

    public void addBallot(BallotType ballotType, List<String> options) {
        if (ballotType == BallotType.CANDIDATE_BALLOT) {
            ballots.put(BallotType.CANDIDATE_BALLOT, new CandidateBallot(options));
        }

        if (ballotType == BallotType.PARTY_BALLOT) {
            ballots.put(BallotType.PARTY_BALLOT, new PartyBallot(options));
        }

        if (ballotType == BallotType.ISSUE_BALLOT) {
            ballots.put(BallotType.ISSUE_BALLOT, new IssueBallot(options));
        }
    }

    public void castVote(Voter voter, Ballot ballot, String option) {
        if (voters.containsKey(voter.getCnp()) && ballots.containsKey(ballot.getType())) {
            ballot.castVote(option);

        } else {
            System.out.println("Invalid voter or ballot.");
        }
    }
}
