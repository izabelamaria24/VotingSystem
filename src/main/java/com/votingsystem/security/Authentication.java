package main.java.com.votingsystem.security;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;

public class Authentication {
    private static final GoogleAuthenticator gAuth = new GoogleAuthenticator();

    public static String generateSecretKey() {
        GoogleAuthenticatorKey key = gAuth.createCredentials();
        return key.getKey();
    }

    public static boolean verifyCode(String secretKey, int verificationCode) {
        return gAuth.authorize(secretKey, verificationCode);
    }
}
