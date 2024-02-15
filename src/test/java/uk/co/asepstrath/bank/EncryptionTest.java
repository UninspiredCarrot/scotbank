package uk.co.asepstrath.bank;

import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class EncryptionTest {
    Encryption encrypt = new Encryption();
    @Test
    public void passwordTest(){
        String pass1 = "password123";
        String pass2 = "password123";
        String pass3 = "pafghjbknlm4567";

        try {
            assertTrue(Arrays.equals(encrypt.encrypt(pass1), encrypt.encrypt(pass2)));
            assertFalse(encrypt.verifyPass(encrypt.encrypt(pass1), pass3));
            assertTrue(encrypt.verifyPass(encrypt.encrypt(pass1), pass2));
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}
