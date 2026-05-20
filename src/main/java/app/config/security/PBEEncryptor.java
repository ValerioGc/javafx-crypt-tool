package app.config.security;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

/**
 * Password-based encryption/decryption using javax.crypto + Bouncy Castle.
 *
 * <p>Output format: {@code Base64( salt[16] || ciphertext )}.
 * The 16-byte random salt is prepended to the ciphertext so decryption
 * only needs the password and algorithm - no separate storage required.
 * Both key and IV are derived deterministically from password + salt by
 * Bouncy Castle's PBE key derivation, so no IV is stored separately.
*/
public final class PBEEncryptor {

    private static final int SALT_LENGTH = 16;
    public static final int DEFAULT_ITERATIONS = 600_000;
    private static final String PROVIDER = "BC";
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private PBEEncryptor() {
    }

    public static String encrypt(String password, String plaintext, String algorithm) throws GeneralSecurityException {
        return encrypt(password, plaintext, algorithm, DEFAULT_ITERATIONS);
    }

    public static String encrypt(String password, String plaintext, String algorithm, int iterations) throws GeneralSecurityException {
        byte[] salt = newSalt();
        byte[] ciphertext = process(Cipher.ENCRYPT_MODE, password, salt, algorithm, plaintext.getBytes(StandardCharsets.UTF_8), iterations);
        byte[] output = new byte[SALT_LENGTH + ciphertext.length];
        System.arraycopy(salt, 0, output, 0, SALT_LENGTH);
        System.arraycopy(ciphertext, 0, output, SALT_LENGTH, ciphertext.length);
        return Base64.getEncoder().encodeToString(output);
    }

    public static String decrypt(String password, String encoded, String algorithm) throws GeneralSecurityException {
        return decrypt(password, encoded, algorithm, DEFAULT_ITERATIONS);
    }

    public static String decrypt(String password, String encoded, String algorithm, int iterations) throws GeneralSecurityException {
        byte[] combined = decodeBase64(encoded);
        if (combined.length <= SALT_LENGTH)
            throw new IllegalArgumentException("Invalid encrypted input: data too short");

        byte[] salt = Arrays.copyOf(combined, SALT_LENGTH);
        byte[] ciphertext = Arrays.copyOfRange(combined, SALT_LENGTH, combined.length);
        byte[] plaintext = process(Cipher.DECRYPT_MODE, password, salt, algorithm, ciphertext, iterations);
        return new String(plaintext, StandardCharsets.UTF_8);
    }

    /**
     * Returns true if the algorithm is usable with the current provider.
     * Performs a full cipher init with a dummy password to catch
     * algorithms that are registered but require unsupported key sizes.
    */
    public static boolean isSupported(String algorithm) {
        try {
            CryptoConfig.ensureProviderRegistered();
            byte[] dummySalt = new byte[SALT_LENGTH];
            SecretKeyFactory skf = SecretKeyFactory.getInstance(algorithm, PROVIDER);
            char[] probe = algorithm.toCharArray();
            PBEKeySpec keySpec = new PBEKeySpec(probe);
            SecretKey key;
            try {
                key = skf.generateSecret(keySpec);
            } finally {
                keySpec.clearPassword();
                Arrays.fill(probe, '\0');
            }
            Cipher c = Cipher.getInstance(algorithm, PROVIDER);
            c.init(Cipher.ENCRYPT_MODE, key, new PBEParameterSpec(dummySalt, 1));
            return true;
        } catch (GeneralSecurityException | RuntimeException e) {
            return false;
        }
    }

    private static byte[] process(int mode, String password, byte[] salt, String algorithm, byte[] data, int iterations) throws GeneralSecurityException {
        SecretKeyFactory skf = SecretKeyFactory.getInstance(algorithm, PROVIDER);
        char[] passwordChars = password.toCharArray();
        PBEKeySpec keySpec = new PBEKeySpec(passwordChars);
        SecretKey key;
        try {
            key = skf.generateSecret(keySpec);
        } finally {
            keySpec.clearPassword();
            Arrays.fill(passwordChars, '\0');
        }
        Cipher cipher = Cipher.getInstance(algorithm, PROVIDER);
        cipher.init(mode, key, new PBEParameterSpec(salt, iterations));
        return cipher.doFinal(data);
    }

    private static byte[] decodeBase64(String encoded) {
        try {
            return Base64.getDecoder().decode(encoded);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid encrypted input: not valid Base64", e);
        }
    }

    private static byte[] newSalt() {
        byte[] salt = new byte[SALT_LENGTH];
        SECURE_RANDOM.nextBytes(salt);
        return salt;
    }
}