package main.java.com.votingsystem.blockchain;

import java.util.ArrayList;
import java.util.List;

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
    }

    public boolean isChainValid() {
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
}
