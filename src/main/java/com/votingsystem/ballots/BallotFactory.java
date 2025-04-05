package main.java.com.votingsystem.ballots;

import java.util.List;

public class BallotFactory {
    public static Ballot createBallot(BallotType ballotType, List<String> options) {
        return switch (ballotType) {
            case CANDIDATE_BALLOT -> new CandidateBallot(options);
            case PARTY_BALLOT -> new PartyBallot(options);
            case ISSUE_BALLOT -> new IssueBallot(options);
            default -> throw new IllegalArgumentException("Invalid ballot type: " + ballotType);
        };
    }
}
