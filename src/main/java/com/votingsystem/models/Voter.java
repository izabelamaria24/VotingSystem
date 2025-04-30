package com.votingsystem.models;

import java.nio.charset.StandardCharsets;
import java.security.*;

public class Voter {
    private String cnp;
    private PublicKey publicKey;
    private PrivateKey privateKey;

    public Voter(String cnp) throws NoSuchAlgorithmException {
        this.cnp = cnp;
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048);
        KeyPair pair = keyGen.generateKeyPair();
        this.publicKey = pair.getPublic();
        this.privateKey = pair.getPrivate();
    }

    public String getCnp() {
        return cnp;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public byte[] signVote(String voteHash) throws Exception {
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);
        signature.update(voteHash.getBytes(StandardCharsets.UTF_8));
        return signature.sign();
    }

    public boolean verifyVote(String voteHash, byte[] signatureBytes) throws Exception {
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initVerify(publicKey);
        signature.update(voteHash.getBytes(StandardCharsets.UTF_8));
        return signature.verify(signatureBytes);
    }
}
