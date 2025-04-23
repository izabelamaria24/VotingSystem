import main.java.com.votingsystem.ballots.BallotType;
import main.java.com.votingsystem.models.Voter;
import main.java.com.votingsystem.system.VotingSystem;

import java.util.Arrays;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {
        VotingSystem votingSystem = new VotingSystem();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\nVoting System CLI:");
            System.out.println("1. Add Voter");
            System.out.println("2. Add Ballot");
            System.out.println("3. Cast Vote");
            System.out.println("4. Display Blockchain");
            System.out.println("5. Validate Blockchain");
            System.out.println("6. Exit");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); 

            switch (choice) {
                case 1 -> {
                    System.out.print("Enter Voter CNP: ");
                    String cnp = scanner.nextLine();
                    Voter voter = new Voter(cnp);
                    votingSystem.addVoter(voter);
                }
                case 2 -> {
                    System.out.println("Choose Ballot Type: 1. Candidate 2. Party 3. Issue");
                    int ballotTypeChoice = scanner.nextInt();
                    scanner.nextLine(); 
                    BallotType ballotType = switch (ballotTypeChoice) {
                        case 1 -> BallotType.CANDIDATE_BALLOT;
                        case 2 -> BallotType.PARTY_BALLOT;
                        case 3 -> BallotType.ISSUE_BALLOT;
                        default -> throw new IllegalArgumentException("Invalid ballot type");
                    };
                    System.out.print("Enter options (comma-separated): ");
                    String options = scanner.nextLine();
                    votingSystem.addBallot(ballotType, Arrays.asList(options.split(",")));
                }
                case 3 -> {
                    System.out.print("Enter Voter CNP: ");
                    String cnp = scanner.nextLine();
                    Voter voter = votingSystem.getVoter(cnp);
                    if (voter == null) {
                        System.out.println("Voter not found!");
                        continue;
                    }
                    System.out.println("Choose Ballot Type: 1. Candidate 2. Party 3. Issue");
                    int ballotTypeChoice = scanner.nextInt();
                    scanner.nextLine(); 
                    BallotType ballotType = switch (ballotTypeChoice) {
                        case 1 -> BallotType.CANDIDATE_BALLOT;
                        case 2 -> BallotType.PARTY_BALLOT;
                        case 3 -> BallotType.ISSUE_BALLOT;
                        default -> throw new IllegalArgumentException("Invalid ballot type");
                    };
                    System.out.print("Enter your vote: ");
                    String voteOption = scanner.nextLine();
                    votingSystem.castVote(voter, votingSystem.getBallot(ballotType), voteOption);
                }
                case 4 -> votingSystem.getBlockchain().displayChain();
                case 5 -> System.out.println("Blockchain valid: " + votingSystem.getBlockchain().isChainValid());
                case 6 -> {
                    System.out.println("Exiting...");
                    scanner.close();
                    return;
                }
                default -> System.out.println("Invalid option!");
            }
        }
    }
}