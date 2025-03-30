package main.java.com.votingsystem.ballots;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PartyBallot extends Ballot {
    private List<String>parties;
    private Map<String, Integer> votes;

    public PartyBallot(List<String> parties) {
        super(BallotType.PARTY_BALLOT);
        this.parties = parties;
        this.votes = new HashMap<>();

        for (String party : parties) {
            votes.put(party, 0);
        }
    }

    @Override
    public void castVote(String voteOption) {
        if (votes.containsKey(voteOption)) {
            votes.put(voteOption, votes.get(voteOption) + 1);
            System.out.println("Vote casted for party: " + voteOption);
        } else {
            System.out.println("Invalid party!");
        }
    }
}
