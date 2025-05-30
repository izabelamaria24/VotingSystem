package com.votingsystem.ballots;

import java.util.List;

public class BallotFactory {
    public static Ballot createBallot(BallotType ballotType, List<String> options) {
        return switch (ballotType) {
            case CANDIDATE_BALLOT, PRESIDENTIAL -> new CandidateBallot(options);
            case PARTY_BALLOT, PARLIAMENTARY -> new PartyBallot(options);
            case ISSUE_BALLOT -> new IssueBallot(options);
        };
    }
}
