package com.votingsystem;
import com.votingsystem.ballots.BallotType;
import com.votingsystem.models.Voter;
import com.votingsystem.system.VotingSystem;

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
            System.out.println("6. Display Audit Logs");
            System.out.println("7. Export Blockchain");
            System.out.println("8. Import Blockchain");
            System.out.println("9. Exit");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> addVoter(scanner, votingSystem);
                case 2 -> addBallot(scanner, votingSystem);
                case 3 -> castVote(scanner, votingSystem);
                case 4 -> votingSystem.getBlockchain().displayChain();
                case 5 -> System.out.println("Blockchain valid: " + votingSystem.getBlockchain().isChainValid());
                case 6 -> votingSystem.displayAuditLogs();
                case 7 -> exportBlockchain(scanner, votingSystem);
                case 8 -> importBlockchain(scanner, votingSystem);
                case 9 -> {
                    System.out.println("Exiting...");
                    scanner.close();
                    return;
                }
                default -> System.out.println("Invalid option!");
            }
        }
    }

    private static void addVoter(Scanner scanner, VotingSystem votingSystem) {
        System.out.print("Enter Voter CNP: ");
        String cnp = scanner.nextLine();
        try {
            Voter voter = new Voter(cnp);
            votingSystem.addVoter(voter);
        } catch (Exception e) {
            System.out.println("Error adding voter: " + e.getMessage());
        }
    }

    private static void addBallot(Scanner scanner, VotingSystem votingSystem) {
        BallotType ballotType = getBallotType(scanner);
        System.out.print("Enter options (comma-separated): ");
        String options = scanner.nextLine();
        votingSystem.addBallot(ballotType, Arrays.asList(options.split(",")));
    }

    private static void castVote(Scanner scanner, VotingSystem votingSystem) {
        System.out.print("Enter Voter CNP: ");
        String cnp = scanner.nextLine();
        Voter voter = votingSystem.getVoter(cnp);
        if (voter == null) {
            System.out.println("Voter not found!");
            return;
        }
        BallotType ballotType = getBallotType(scanner);
        System.out.print("Enter your vote: ");
        String voteOption = scanner.nextLine();
        votingSystem.castVote(voter, votingSystem.getBallot(ballotType), voteOption);
    }

    private static BallotType getBallotType(Scanner scanner) {
        System.out.println("Choose Ballot Type: 1. Candidate 2. Party 3. Issue");
        int ballotTypeChoice = scanner.nextInt();
        scanner.nextLine();
        return switch (ballotTypeChoice) {
            case 1 -> BallotType.CANDIDATE_BALLOT;
            case 2 -> BallotType.PARTY_BALLOT;
            case 3 -> BallotType.ISSUE_BALLOT;
            default -> throw new IllegalArgumentException("Invalid ballot type");
        };
    }

    private static void exportBlockchain(Scanner scanner, VotingSystem votingSystem) {
        System.out.print("Enter file path to export blockchain: ");
        String exportPath = scanner.nextLine();
        try {
            votingSystem.getBlockchain().exportBlockchain(exportPath);
            System.out.println("Blockchain successfully exported to " + exportPath);
        } catch (Exception e) {
            System.out.println("Error exporting blockchain: " + e.getMessage());
        }
    }

    private static void importBlockchain(Scanner scanner, VotingSystem votingSystem) {
        System.out.print("Enter file path to import blockchain: ");
        String importPath = scanner.nextLine();
        try {
            votingSystem.getBlockchain().importBlockchain(importPath);
            System.out.println("Blockchain successfully imported from " + importPath);
        } catch (Exception e) {
            System.out.println("Error importing blockchain: " + e.getMessage());
        }
    }
}