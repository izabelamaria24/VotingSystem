package com.votingsystem.system;

import com.votingsystem.ballots.*;
import com.votingsystem.blockchain.Blockchain;
import com.votingsystem.database.BallotDAO;
import com.votingsystem.database.VoterDAO;
import com.votingsystem.database.VoteDAO;
import com.votingsystem.models.Vote;
import com.votingsystem.models.Voter;
import com.votingsystem.security.AuditLog;
import com.votingsystem.security.EncryptionUtil;
import com.votingsystem.export.ResultsExporter;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class VotingSystem {
    private final Blockchain blockchain;
    private final SecretKey encryptionKey;
    private final VoterDAO voterDAO;
    private final BallotDAO ballotDAO;
    private final VoteDAO voteDAO;

    public VotingSystem() throws Exception {
        blockchain = new Blockchain();
        encryptionKey = EncryptionUtil.generateKey();
        voterDAO = VoterDAO.getInstance();
        ballotDAO = BallotDAO.getInstance();
        voteDAO = VoteDAO.getInstance();
    }

    public void addVoter(Voter voter) {
        try {
            voterDAO.saveVoter(voter);
            logAction("Added voter: " + voter.getCnp());
        } catch (Exception e) {
            System.out.println("Error saving voter: " + e.getMessage());
        }
    }

    public void addBallot(BallotType ballotType, List<String> options) {
        try {
            ballotDAO.saveBallot(ballotType, options);
            logAction("Added ballot: " + ballotType);
        } catch (Exception e) {
            System.out.println("Error saving ballot: " + e.getMessage());
        }
    }

    public Ballot getBallot(BallotType ballotType) {
        try {
            List<String> options = ballotDAO.getMostRecentBallotOptions(ballotType);
            if (!options.isEmpty()) {
                return BallotFactory.createBallot(ballotType, options);
            } else {
                System.out.println("No ballot found for type: " + ballotType);
            }
        } catch (Exception e) {
            System.out.println("Error retrieving ballot: " + e.getMessage());
        }
        return null;
    }

    public List<Map<String, Object>> getAllBallotsOfType(BallotType type) {
        try {
            return ballotDAO.getAllBallotsOfType(type);
        } catch (Exception e) {
            System.out.println("Error retrieving ballots: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public Ballot getBallotById(BallotType type, int ballotId) {
        try {
            List<String> options = ballotDAO.getBallotOptions(ballotId);
            if (!options.isEmpty()) {
                return BallotFactory.createBallot(type, options);
            } else {
                System.out.println("No ballot found with ID: " + ballotId);
            }
        } catch (Exception e) {
            System.out.println("Error retrieving ballot: " + e.getMessage());
        }
        return null;
    }

    public void castVote(Voter voter, BallotType type, int ballotId, String option) {
        try {
            Voter dbVoter = voterDAO.getVoter(voter.getCnp());
            if (dbVoter == null) {
                System.out.println("Error: Voter not registered in the system");
                logAction("Attempted vote by unregistered voter: " + voter.getCnp());
                return;
            }

            if (voteDAO.hasVoted(voter.getCnp(), ballotId)) {
                System.out.println("You have already voted on this ballot!");
                logAction("Attempted duplicate vote by voter: " + voter.getCnp());
                return;
            }

            Ballot ballot = getBallotById(type, ballotId);
            if (ballot == null) {
                System.out.println("Error: Ballot not found");
                return;
            }

            if (!isValidVoteOption(ballot, option)) {
                System.out.println("Invalid vote option!");
                logAction("Invalid vote option attempted by voter: " + voter.getCnp());
                return;
            }

            Vote vote = new Vote(voter, option, LocalDateTime.now());
            String hash = vote.hashVote();
            byte[] signature = voter.signVote(hash);

            if (voter.verifyVote(hash, signature)) {
                String encryptedVote = EncryptionUtil.encrypt(vote.toString(), encryptionKey);
                ballot.castVote(option);
                blockchain.addBlock(List.of(encryptedVote));

                voteDAO.saveVote(voter.getCnp(), ballotId, option);
                logAction("Vote successfully cast by voter: " + voter.getCnp());
            } else {
                System.out.println("Vote verification failed");
                logAction("Vote verification failed for voter: " + voter.getCnp());
            }
        } catch (Exception e) {
            System.out.println("Error processing vote: " + e.getMessage());
            logAction("Error processing vote for voter: " + voter.getCnp());
        }
    }

    private boolean isValidVoteOption(Ballot ballot, String option) {
        try {
            List<String> options;
            if (ballot instanceof CandidateBallot) {
                options = ballotDAO.getMostRecentBallotOptions(BallotType.CANDIDATE_BALLOT);
            } else if (ballot instanceof PartyBallot) {
                options = ballotDAO.getMostRecentBallotOptions(BallotType.PARTY_BALLOT);
            } else if (ballot instanceof IssueBallot) {
                options = ballotDAO.getMostRecentBallotOptions(BallotType.ISSUE_BALLOT);
            } else {
                return false;
            }
            return options.contains(option);
        } catch (Exception e) {
            System.out.println("Error validating vote option: " + e.getMessage());
            return false;
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

    public void displayBallotResults(BallotType type, int ballotId) {
        try {
            Map<String, Integer> results = voteDAO.getVoteResults(ballotId);
            List<String> options = ballotDAO.getBallotOptions(ballotId);

            System.out.println("\nResults for Ballot ID: " + ballotId);
            System.out.println("Ballot Type: " + type);
            System.out.println("----------------");

            for (String option : options) {
                int votes = results.getOrDefault(option, 0);
                System.out.println(option + ": " + votes + " votes");
            }

            int totalVotes = results.values().stream().mapToInt(Integer::intValue).sum();
            System.out.println("----------------");
            System.out.println("Total votes cast: " + totalVotes);

            if (totalVotes > 0) {
                System.out.println("\nPercentage Distribution:");
                for (String option : options) {
                    int votes = results.getOrDefault(option, 0);
                    double percentage = (votes * 100.0) / totalVotes;
                    System.out.printf("%s: %.2f%%\n", option, percentage);
                }
            }
        } catch (Exception e) {
            System.out.println("Error retrieving ballot results: " + e.getMessage());
        }
    }

    public void exportResultsToCSV(BallotType type, int ballotId, String filePath) {
        try {
            Map<String, Integer> results = voteDAO.getVoteResults(ballotId);
            List<String> options = ballotDAO.getBallotOptions(ballotId);

            ResultsExporter.exportToCSV(filePath, type, ballotId, results, options);
            System.out.println("Results successfully exported to " + filePath);
            logAction("Exported results for ballot " + ballotId + " to CSV");
        } catch (Exception e) {
            System.out.println("Error exporting results to CSV: " + e.getMessage());
        }
    }

    public void exportResultsToPDF(BallotType type, int ballotId, String filePath) {
        try {
            Map<String, Integer> results = voteDAO.getVoteResults(ballotId);
            List<String> options = ballotDAO.getBallotOptions(ballotId);

            ResultsExporter.exportToPDF(filePath, type, ballotId, results, options);
            System.out.println("Results successfully exported to " + filePath);
            logAction("Exported results for ballot " + ballotId + " to PDF");
        } catch (Exception e) {
            System.out.println("Error exporting results to PDF: " + e.getMessage());
        }
    }

    public void showRealTimeResults(BallotType type, int ballotId) {
        try {
            Map<String, Integer> results = voteDAO.getVoteResults(ballotId);
            List<String> options = ballotDAO.getBallotOptions(ballotId);

            System.out.println("\nReal-time Results for Ballot ID: " + ballotId);
            System.out.println("Type: " + type);
            ResultsExporter.createRealTimeChart(results, options);
            logAction("Displayed real-time results for ballot " + ballotId);
        } catch (Exception e) {
            System.out.println("Error showing real-time results: " + e.getMessage());
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

    public void displayVoters() {
        try {
            List<Voter> voters = voterDAO.getAllVoters();
            if (voters.isEmpty()) {
                System.out.println("No voters registered in the system.");
                return;
            }

            System.out.println("\nRegistered Voters:");
            System.out.println("----------------");
            for (Voter voter : voters) {
                System.out.println("CNP: " + voter.getCnp());
            }
            System.out.println("----------------");
            System.out.println("Total voters: " + voters.size());
        } catch (Exception e) {
            System.out.println("Error retrieving voters: " + e.getMessage());
        }
    }

    private void logAction(String action) {
        AuditLog.log(action);
    }
}

