package passwordmanager;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import javax.crypto.spec.GCMParameterSpec;
import java.security.GeneralSecurityException;

public class EncryptionManager {
    
    private static final int SALT_LENGTH = 16; // Length of the salt in bytes
    private final SecureRandom random = new SecureRandom();
    private static final int KEY_LENGTH = 256; // Length of the derived key in bits
    private static final int ITERATIONS = 600_000; // high iteration count
    private static final int IV_LENGTH = 12;

    public byte[] generateSalt() {
        byte[] salt = new byte[SALT_LENGTH];
        random.nextBytes(salt);
        System.out.println("Generated Salt: " + salt);
        return salt;
    }

    public byte[] generateIv() {
        byte[] byteIv = new byte[IV_LENGTH];
        random.nextBytes(byteIv);
        return byteIv;
    }

    public byte[] encrypt(String plaintext, SecretKey key, byte[] iv) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec spec = new GCMParameterSpec(128, iv);
        cipher.init(Cipher.ENCRYPT_MODE, key, spec);
        byte[] plaintextBytes = plaintext.getBytes(StandardCharsets.UTF_8);

        return cipher.doFinal(plaintextBytes);
    }

    public String decrypt(byte[] ciphertext, SecretKey key, byte[] iv) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec spec = new GCMParameterSpec(128, iv);
        cipher.init(Cipher.DECRYPT_MODE, key, spec);
        byte[] decryptedBytes = cipher.doFinal(ciphertext);

        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }

    public SecretKey deriveKey(char[] masterPassword, byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException {

        PBEKeySpec spec = new PBEKeySpec(masterPassword, salt, ITERATIONS, KEY_LENGTH);

        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            SecretKey derivedKey = factory.generateSecret(spec);
            SecretKey aesKey = new SecretKeySpec(derivedKey.getEncoded(), "AES");

            return aesKey;

        } finally {
            spec.clearPassword();
        }

    }
    
}
