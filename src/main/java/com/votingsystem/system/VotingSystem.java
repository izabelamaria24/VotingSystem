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
    private final Blockchain blockchain;
    private final Map<String, Voter> voters;
    private final Map<BallotType, Ballot> ballots;
    private final SecretKey encryptionKey;

    public VotingSystem() throws Exception {
        blockchain = new Blockchain();
        voters = new HashMap<>();
        ballots = new HashMap<>();
        encryptionKey = EncryptionUtil.generateKey();
    }

    public void addVoter(Voter voter) {
        voters.put(voter.getCnp(), voter);
        logAction("Added voter: " + voter.getCnp());
    }

    public void addBallot(BallotType ballotType, List<String> options) {
        Ballot ballot = BallotFactory.createBallot(ballotType, options);
        ballots.put(ballotType, ballot);
        logAction("Added ballot: " + ballotType);
    }

    public void castVote(Voter voter, Ballot ballot, String option) {
        if (!voters.containsKey(voter.getCnp())) {
            System.out.println("Invalid voter or ballot");
            logAction("Invalid voter or ballot for voter: " + voter.getCnp());
            return;
        }

        try {
            Vote vote = new Vote(voter, option, LocalDateTime.now());
            String hash = vote.hashVote();
            byte[] signature = voter.signVote(hash);

            if (voter.verifyVote(hash, signature)) {
                String encryptedVote = EncryptionUtil.encrypt(vote.toString(), encryptionKey);
                ballot.castVote(option);
                blockchain.addBlock(List.of(encryptedVote));
                logAction("Vote casted for voter: " + voter.getCnp());
            } else {
                System.out.println("Vote verification failed");
                logAction("Vote verification failed for voter: " + voter.getCnp());
            }
        } catch (Exception e) {
            System.out.println("Error signing or verifying vote: " + e.getMessage());
            logAction("Error signing or verifying vote for voter: " + voter.getCnp());
        }
    }

    public void displayAuditLogs() {
        AuditLog.displayLogs();
    }

    public Ballot getBallot(BallotType ballotType) {
        return ballots.get(ballotType);
    }

    public Blockchain getBlockchain() {
        return blockchain;
    }

    public Voter getVoter(String cnp) {
        return voters.get(cnp);
    }

    private void logAction(String action) {
        AuditLog.log(action);
    }
}