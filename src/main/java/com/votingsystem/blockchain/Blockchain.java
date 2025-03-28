package main.java.com.votingsystem.blockchain;
import java.util.ArrayList;
import java.util.List;
import main.java.com.votingsystem.models.Vote;

public class Blockchain {
    private List<Block> chain;

    public Blockchain() {
        this.chain = new ArrayList<>();
    }

    public void addBlock(List<Vote> votes) {
        // new block data
        Block previousBlock = this.chain.get(this.chain.size() - 1);
        String previousHash = previousBlock.getHash();
        String timestamp = java.time.LocalDateTime.now().toString();

        Block newBlock = new Block(previousHash, timestamp, votes);
        chain.add(newBlock);
    }

    public void displayChain() {
        for (Block block : chain) {
            System.out.println(block);
            block.displayVotes();
        }
    }
}
