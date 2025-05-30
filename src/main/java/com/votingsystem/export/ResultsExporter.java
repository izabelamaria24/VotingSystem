package com.votingsystem.export;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import com.opencsv.CSVWriter;
import com.votingsystem.ballots.BallotType;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class ResultsExporter {

    public static void exportToCSV(String filePath, BallotType type, int ballotId, Map<String, Integer> results, List<String> options) throws IOException {
        try (CSVWriter writer = new CSVWriter(new FileWriter(filePath))) {
            // Write header
            writer.writeNext(new String[]{"Option", "Votes", "Percentage"});

            // Calculate total votes
            int totalVotes = results.values().stream().mapToInt(Integer::intValue).sum();

            // Write data for each option
            for (String option : options) {
                int votes = results.getOrDefault(option, 0);
                double percentage = totalVotes > 0 ? (votes * 100.0) / totalVotes : 0;
                writer.writeNext(new String[]{
                    option,
                    String.valueOf(votes),
                    String.format("%.2f%%", percentage)
                });
            }

            // Write total
            writer.writeNext(new String[]{"Total", String.valueOf(totalVotes), "100%"});
        }
    }

    public static void exportToPDF(String filePath, BallotType type, int ballotId, Map<String, Integer> results, List<String> options) throws IOException, DocumentException {
        Document document = new Document();
        PdfWriter.getInstance(document, new java.io.FileOutputStream(filePath));
        document.open();

        // Add title
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
        Paragraph title = new Paragraph("Election Results - " + type.toString(), titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
        document.add(new Paragraph("\n"));

        // Add ballot info
        document.add(new Paragraph("Ballot ID: " + ballotId));
        document.add(new Paragraph("\n"));

        // Calculate total votes
        int totalVotes = results.values().stream().mapToInt(Integer::intValue).sum();

        // Create results table
        com.itextpdf.text.List list = new com.itextpdf.text.List();
        for (String option : options) {
            int votes = results.getOrDefault(option, 0);
            double percentage = totalVotes > 0 ? (votes * 100.0) / totalVotes : 0;
            list.add(new ListItem(String.format("%s: %d votes (%.2f%%)",
                option, votes, percentage)));
        }
        document.add(list);

        // Add total
        document.add(new Paragraph("\nTotal Votes: " + totalVotes));

        // Add timestamp
        document.add(new Paragraph("\nGenerated on: " + java.time.LocalDateTime.now()));

        document.close();
    }

    public static void createRealTimeChart(Map<String, Integer> results, List<String> options) {
        // Print ASCII chart for console visualization
        System.out.println("\nReal-time Vote Distribution:");
        System.out.println("==========================");

        int totalVotes = results.values().stream().mapToInt(Integer::intValue).sum();
        int maxBarLength = 50; // Maximum length of the bar

        for (String option : options) {
            int votes = results.getOrDefault(option, 0);
            double percentage = totalVotes > 0 ? (votes * 100.0) / totalVotes : 0;
            int barLength = totalVotes > 0 ? (int)((votes * maxBarLength) / totalVotes) : 0;

            // Print the bar
            System.out.printf("%-15s [%-" + maxBarLength + "s] %3d (%5.1f%%)\n",
                option,
                "=".repeat(barLength),
                votes,
                percentage);
        }

        System.out.println("==========================");
        System.out.println("Total Votes: " + totalVotes);
    }
}
