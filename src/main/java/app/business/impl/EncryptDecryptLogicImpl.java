package app.business.impl;

import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;

import app.business.EncryptDecryptLogic;
import app.config.security.CryptoConfig;
import app.config.security.PBEEncryptor;
import app.exception.DecryptionInitializationException;
import app.exception.DecryptionOperationException;

/**
 * Implementation of {@link EncryptDecryptLogic} using {@link PBEEncryptor} and Bouncy Castle.
*/
public class EncryptDecryptLogicImpl implements EncryptDecryptLogic {

    @Override
    public String encrypt(String salt, String plainText, String algorithmName) throws NoSuchAlgorithmException {
        return encrypt(salt, plainText, algorithmName, PBEEncryptor.DEFAULT_ITERATIONS);
    }

    @Override
    public String encrypt(String salt, String plainText, String algorithmName, int iterations) throws NoSuchAlgorithmException {
        if (algorithmName == null)
            throw new NoSuchAlgorithmException("Algorithm not supported: " + algorithmName);
        CryptoConfig.ensureProviderRegistered();
        try {
            return PBEEncryptor.encrypt(salt, plainText, algorithmName, iterations);
        } catch (GeneralSecurityException e) {
            throw new DecryptionInitializationException("Algorithm initialization error: " + algorithmName);
        }
    }

    @Override
    public String decrypt(String salt, String encryptedText, String algorithm) throws NoSuchAlgorithmException {
        return decrypt(salt, encryptedText, algorithm, PBEEncryptor.DEFAULT_ITERATIONS);
    }

    @Override
    public String decrypt(String salt, String encryptedText, String algorithm, int iterations) throws NoSuchAlgorithmException {
        CryptoConfig.ensureProviderRegistered();
        try {
            return PBEEncryptor.decrypt(salt, encryptedText, algorithm, iterations);
        } catch (IllegalArgumentException e) {
            throw new DecryptionOperationException("Error during decryption: " + e.getMessage(), e);
        } catch (GeneralSecurityException e) {
            throw new DecryptionOperationException("Error during decryption: please verify the input parameters.", e);
        }
    }

    @Override
    public boolean isAlgorithmSupported(String algorithm) {
        return PBEEncryptor.isSupported(algorithm);
    }
}