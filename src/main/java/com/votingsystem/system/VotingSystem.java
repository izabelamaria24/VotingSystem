package main.java.com.votingsystem.system;

import main.java.com.votingsystem.ballots.*;
import main.java.com.votingsystem.blockchain.Blockchain;
import main.java.com.votingsystem.models.Vote;
import main.java.com.votingsystem.models.Voter;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.time.LocalDateTime;

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
        Ballot ballot = BallotFactory.createBallot(ballotType, options);
        ballots.put(ballotType, ballot);
    }

    public void castVote(Voter voter, Ballot ballot, String option) {
        if (voters.containsKey(voter.getCnp())) {
            Vote vote = new Vote(voter, option, LocalDateTime.now());
            String hash = vote.hashVote();
            try {
                byte[] signature = voter.signVote(hash);
                if (voter.verifyVote(hash, signature)) {
                    ballot.castVote(option);
                    blockchain.addBlock(List.of(vote));
                } else {
                    System.out.println("Vote verification failed");
                }
            } catch (Exception e) {
                System.out.println("Error signing or verifying vote: " + e.getMessage());
            }
        } else {
            System.out.println("Vote verification failed");
        }
    }
}
