package app.business;

import java.security.NoSuchAlgorithmException;

public interface EncryptDecryptLogic {

    String encrypt(String salt, String plainText, String algorithmName) throws NoSuchAlgorithmException;

    String decrypt(String salt, String encryptedText, String algorithm) throws NoSuchAlgorithmException;

    boolean isAlgorithmSupported(String algorithm);

    default String encrypt(String salt, String plainText, String algorithmName, int iterations) throws NoSuchAlgorithmException {
        return encrypt(salt, plainText, algorithmName);
    }

    default String decrypt(String salt, String encryptedText, String algorithm, int iterations) throws NoSuchAlgorithmException {
        return decrypt(salt, encryptedText, algorithm);
    }
}
