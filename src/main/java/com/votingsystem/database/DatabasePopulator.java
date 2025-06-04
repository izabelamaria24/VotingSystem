package com.votingsystem.database;

import com.votingsystem.ballots.BallotType;
import java.sql.SQLException;
import java.util.List;

public class DatabasePopulator {
    public static void main(String[] args) {
        try {
            SetupDatabase.main(new String[]{});

            VoterDAO voterDAO = VoterDAO.getInstance();
            int voterId1 = voterDAO.saveVoterAndReturnId("1234567890123", "public_key_1");
            int voterId2 = voterDAO.saveVoterAndReturnId("9876543210987", "public_key_2");
            int voterId3 = voterDAO.saveVoterAndReturnId("5555555555555", "public_key_3");

            System.out.println("Voters inserted successfully!");

            BallotDAO ballotDAO = BallotDAO.getInstance();
            int ballotId1 = ballotDAO.saveBallotAndReturnId(BallotType.PRESIDENTIAL, List.of("Alice", "Bob", "Charlie"));
            int ballotId2 = ballotDAO.saveBallotAndReturnId(BallotType.PARLIAMENTARY, List.of("PartyA", "PartyB", "PartyC"));

            System.out.println("Ballots inserted successfully!");

            VoteDAO voteDAO = VoteDAO.getInstance();
            voteDAO.saveVote(voterId1, ballotId1, "Alice");
            voteDAO.saveVote(voterId2, ballotId1, "Bob");
            voteDAO.saveVote(voterId3, ballotId2, "PartyA");
            voteDAO.saveVote(voterId1, ballotId2, "PartyB");

            System.out.println("Database populated with multiple rows successfully!");
        } catch (SQLException e) {
            System.err.println("SQL Error occurred: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Unexpected error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

