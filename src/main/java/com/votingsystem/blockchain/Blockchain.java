package main.java.com.votingsystem.blockchain;

import java.util.ArrayList;
import java.util.List;

import javax.crypto.SecretKey;

import main.java.com.votingsystem.security.EncryptionUtil;

public class Blockchain {
    private List<Block> chain;
    private int difficulty;

    public Blockchain() {
        this.chain = new ArrayList<>();
        this.chain.add(new Block("0", java.time.LocalDateTime.now().toString(), new ArrayList<>()));
        this.difficulty = 4;
    }

    public void addBlock(List<String> encryptedVotes) {
        Block previousBlock = this.chain.get(this.chain.size() - 1);
        String previousHash = previousBlock.getHash();
        String timestamp = java.time.LocalDateTime.now().toString();

        Block newBlock = new Block(previousHash, timestamp, encryptedVotes);
        System.out.println("Mining block...");
        newBlock.mineBlock(difficulty);
        chain.add(newBlock);
        System.out.println("Block successfully added to the blockchain!");
    }

    public boolean isChainValid() {
        Block genesisBlock = chain.get(0);
        if (!genesisBlock.getPreviousHash().equals("0")) {
            System.out.println("Invalid genesis block!");
            return false;
        }
    
        for (int i = 1; i < chain.size(); i++) {
            Block currentBlock = chain.get(i);
            Block previousBlock = chain.get(i - 1);
    
            if (!currentBlock.getHash().equals(currentBlock.calculateHash())) {
                System.out.println("Invalid hash at block " + i);
                return false;
            }
    
            if (!currentBlock.getPreviousHash().equals(previousBlock.getHash())) {
                System.out.println("Invalid previous hash at block " + i);
                return false;
            }
    
            if (!currentBlock.getHash().substring(0, difficulty).equals("0".repeat(difficulty))) {
                System.out.println("Invalid proof of work at block " + i);
                return false;
            }
        }
    
        return true;
    }

    public void displayChain() {
        System.out.println("Blockchain:");
        for (int i = 0; i < chain.size(); i++) {
            Block block = chain.get(i);
            System.out.println("Block " + i + ":");
            System.out.println("Hash: " + block.getHash());
            System.out.println("Previous Hash: " + block.getPreviousHash());
            System.out.println("Votes: ");
            block.displayVotes();
            System.out.println("-----------------------------");
        }
    }

    public void decryptVotes(SecretKey encryptionKey) {
        System.out.println("Decrypting votes...");
        for (Block block : chain) {
            for (String encryptedVote : block.getVotes()) {
                try {
                    String decryptedVote = EncryptionUtil.decrypt(encryptedVote, encryptionKey);
                    System.out.println("Decrypted Vote: " + decryptedVote);
                } catch (Exception e) {
                    System.out.println("Error decrypting vote: " + e.getMessage());
                }
            }
        }
    }

    public void importBlockchain(String filePath) {
        try (java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.FileReader(filePath))) {
            chain.clear();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                String hash = parts[0];
                String previousHash = parts[1];
                List<String> votes = List.of(parts[2].split(";"));
                Block block = new Block(previousHash, java.time.LocalDateTime.now().toString(), votes);
                block.mineBlock(difficulty); 
                chain.add(block);
            }
            System.out.println("Blockchain imported from " + filePath);
        } catch (Exception e) {
            System.out.println("Error importing blockchain: " + e.getMessage());
        }
    }

    public void exportBlockchain(String filePath) {
        try (java.io.FileWriter writer = new java.io.FileWriter(filePath)) {
            for (Block block : chain) {
                writer.write(block.getHash() + "," + block.getPreviousHash() + "," + block.getVotes() + "\n");
            }
            System.out.println("Blockchain exported to " + filePath);
        } catch (Exception e) {
            System.out.println("Error exporting blockchain: " + e.getMessage());
        }
    }
}
