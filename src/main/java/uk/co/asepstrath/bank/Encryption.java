package uk.co.asepstrath.bank;

//import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

//import static org.junit.Assert.assertEquals;

public class Encryption {
    public byte[] encrypt(String pass) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest md = MessageDigest.getInstance("SHA-512");
        byte[] hashedPass = md.digest(pass.getBytes(StandardCharsets.UTF_8));

        return hashedPass;
    }

    /**
     This method takes in a string and a byte array
     Encrypts the string into a byte array
     compares byte arrays and returns boolean true if they match
     */
    public boolean verifyPass(byte[] pass, String testPass) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        byte[] inputPass = encrypt(testPass);
        //System.out.println("pass 2 : " + inputPass);
        return ( Arrays.equals(pass,inputPass) );
    }

}
