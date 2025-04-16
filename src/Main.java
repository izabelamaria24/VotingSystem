import main.java.com.votingsystem.ballots.BallotType;
import main.java.com.votingsystem.models.Voter;
import main.java.com.votingsystem.system.VotingSystem;

import java.util.Arrays;

public class Main {
    public static void main(String[] args) throws Exception {
        VotingSystem votingSystem = new VotingSystem();

        Voter voter1 = new Voter("1234567890123");
        Voter voter2 = new Voter("9876543210987");
        votingSystem.addVoter(voter1);
        votingSystem.addVoter(voter2);

        votingSystem.addBallot(BallotType.CANDIDATE_BALLOT, Arrays.asList("Candidate A", "Candidate B"));
        votingSystem.addBallot(BallotType.PARTY_BALLOT, Arrays.asList("Party A", "Party B"));

        votingSystem.castVote(voter1, votingSystem.getBallot(BallotType.CANDIDATE_BALLOT), "Candidate A");
        votingSystem.castVote(voter2, votingSystem.getBallot(BallotType.PARTY_BALLOT), "Party B");

        votingSystem.getBlockchain().displayChain();
    }
}
