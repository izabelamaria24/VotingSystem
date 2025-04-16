package main.java.com.votingsystem.blockchain;

import java.util.ArrayList;
import java.util.List;

public class Blockchain {
    private List<Block> chain;

    public Blockchain() {
        this.chain = new ArrayList<>();
        this.chain.add(new Block("0", java.time.LocalDateTime.now().toString(), new ArrayList<>()));
    }

    public void addBlock(List<String> encryptedVotes) {
        Block previousBlock = this.chain.getLast();
        String previousHash = previousBlock.getHash();
        String timestamp = java.time.LocalDateTime.now().toString();

        Block newBlock = new Block(previousHash, timestamp, encryptedVotes);
        chain.add(newBlock);
    }

    public void displayChain() {
        for (Block block : chain) {
            System.out.println(block);
            block.displayVotes();
        }
    }
}
