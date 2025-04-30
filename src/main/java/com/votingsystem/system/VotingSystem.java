package com.votingsystem.system;

import com.votingsystem.ballots.*;
import com.votingsystem.blockchain.Blockchain;
import com.votingsystem.database.BallotDAO;
import com.votingsystem.database.VoterDAO;
import com.votingsystem.models.Vote;
import com.votingsystem.models.Voter;
import com.votingsystem.security.AuditLog;
import com.votingsystem.security.EncryptionUtil;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.util.List;

public class VotingSystem {
    private final Blockchain blockchain;
    private final SecretKey encryptionKey;
    private final VoterDAO voterDAO;
    private final BallotDAO ballotDAO;

    public VotingSystem() throws Exception {
        blockchain = new Blockchain();
        encryptionKey = EncryptionUtil.generateKey();
        voterDAO = VoterDAO.getInstance();
        ballotDAO = BallotDAO.getInstance();
    }

    public void addVoter(Voter voter) {
        try {
            voterDAO.saveVoter(voter); // Save voter to the database
            logAction("Added voter: " + voter.getCnp());
        } catch (Exception e) {
            System.out.println("Error saving voter: " + e.getMessage());
        }
    }

    public void addBallot(BallotType ballotType, List<String> options) {
        try {
            ballotDAO.saveBallot(ballotType, options); // Save ballot to the database
            logAction("Added ballot: " + ballotType);
        } catch (Exception e) {
            System.out.println("Error saving ballot: " + e.getMessage());
        }
    }

    public Ballot getBallot(BallotType ballotType) {
        try {
            List<String> options = ballotDAO.getBallotOptions(ballotType.ordinal());
            if (options != null && !options.isEmpty()) {
                return BallotFactory.createBallot(ballotType, options);
            } else {
                System.out.println("No ballot found for type: " + ballotType);
            }
        } catch (Exception e) {
            System.out.println("Error retrieving ballot: " + e.getMessage());
        }
        return null;
    }

    public void castVote(Voter voter, Ballot ballot, String option) {
        try {
            if (voterDAO.getVoter(voter.getCnp()) == null) {
                System.out.println("Invalid voter or ballot");
                logAction("Invalid voter or ballot for voter: " + voter.getCnp());
                return;
            }

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

    public void displayResults(BallotType ballotType) {
        try {
            List<String> options = ballotDAO.getBallotOptions(ballotType.ordinal());
            if (options != null) {
                System.out.println("Results for " + ballotType + ": " + options);
            } else {
                System.out.println("No ballot found for type: " + ballotType);
            }
        } catch (Exception e) {
            System.out.println("Error retrieving ballot results: " + e.getMessage());
        }
    }

    public void displayAuditLogs() {
        AuditLog.displayLogs();
    }

    public Blockchain getBlockchain() {
        return blockchain;
    }

    public Voter getVoter(String cnp) {
        try {
            return voterDAO.getVoter(cnp); 
        } catch (Exception e) {
            System.out.println("Error retrieving voter: " + e.getMessage());
            return null;
        }
    }

    private void logAction(String action) {
        AuditLog.log(action);
    }
}