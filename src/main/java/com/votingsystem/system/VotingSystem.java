package main.java.com.votingsystem.system;

import main.java.com.votingsystem.ballots.*;
import main.java.com.votingsystem.blockchain.Blockchain;
import main.java.com.votingsystem.models.Vote;
import main.java.com.votingsystem.models.Voter;
import main.java.com.votingsystem.security.AuditLog;
import main.java.com.votingsystem.security.EncryptionUtil;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class VotingSystem {
    private Blockchain blockchain;
    private Map<String, Voter> voters;
    private Map<BallotType, Ballot> ballots;

    private SecretKey encryptionKey;

    public VotingSystem() throws Exception {
        blockchain = new Blockchain();
        voters = new HashMap<>();
        ballots = new HashMap<>();

        encryptionKey = EncryptionUtil.generateKey();
    }

    public void addVoter(Voter voter) {
        voters.put(voter.getCnp(), voter);

        AuditLog.log("Added voter: " + voter.getCnp());
    }

    public void addBallot(BallotType ballotType, List<String> options) {
        Ballot ballot = BallotFactory.createBallot(ballotType, options);
        ballots.put(ballotType, ballot);

        AuditLog.log("Added ballot: " + ballotType);
    }

    public void castVote(Voter voter, Ballot ballot, String option) {
        if (voters.containsKey(voter.getCnp())) {
            Vote vote = new Vote(voter, option, LocalDateTime.now());
            String hash = vote.hashVote();
            try {
                byte[] signature = voter.signVote(hash);
                if (voter.verifyVote(hash, signature)) {
                    String encryptedVote = EncryptionUtil.encrypt(vote.toString(), encryptionKey);

                    ballot.castVote(option);
                    blockchain.addBlock(List.of(encryptedVote));

                    AuditLog.log("Vote casted for voter: " + voter.getCnp());
                } else {
                    System.out.println("Vote verification failed");
                    AuditLog.log("Vote verification failed for voter: " + voter.getCnp());
                }
            } catch (Exception e) {
                System.out.println("Error signing or verifying vote: " + e.getMessage());
                AuditLog.log("Error signing or verifying vote for voter: " + e.getMessage());
            }
        } else {
            System.out.println("Invalid voter or ballot");
            AuditLog.log("Invalid voter or ballot for voter: " + voter.getCnp());
        }
    }

    public Ballot getBallot(BallotType ballotType) {
        return ballots.get(ballotType);
    }

    public Blockchain getBlockchain() {
        return blockchain;
    }
}
