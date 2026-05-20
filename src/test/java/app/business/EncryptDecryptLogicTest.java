package app.business;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.security.NoSuchAlgorithmException;
import java.security.Security;

import org.junit.jupiter.api.Test;

import app.business.impl.EncryptDecryptLogicImpl;
import app.config.security.AlgorithmRegistry;

/**
 * Test class for {@link EncryptDecryptLogic}.
 */
class EncryptDecryptLogicTest {
	
	
	private static final EncryptDecryptLogicImpl encryptDecryptLogic = new EncryptDecryptLogicImpl(); 

    /**
     * Tests that encryption followed by <b>decryption</b> returns the original plaintext.
     *
     * @throws NoSuchAlgorithmException if the algorithm is not supported.
     */
    @Test
    void testEncryptDecrypt() throws NoSuchAlgorithmException {
        String salt = "mysalt";
        String plainText = "myPassword";

        for (String algorithm : AlgorithmRegistry.ALGORITHMS.values()) {
            String encrypted = encryptDecryptLogic.encrypt(salt, plainText, algorithm);
            assertNotNull(encrypted, "Encrypted text should not be null for " + algorithm);

            String decrypted = encryptDecryptLogic.decrypt(salt, encrypted, algorithm);
            assertEquals(plainText, decrypted, "Decrypted text should equal the original plaintext for " + algorithm);
        }
    }

    @Test
    void testEncryptRegistersBouncyCastleProviderWhenMissing() throws NoSuchAlgorithmException {
        Security.removeProvider("BC");
        assertNull(Security.getProvider("BC"), "Bouncy Castle provider should be absent before encryption");

        String encrypted = encryptDecryptLogic.encrypt(
            "mysalt",
            "myPassword",
            "PBEWITHSHA256AND128BITAES-CBC-BC"
        );

        assertNotNull(encrypted, "Encrypted text should not be null");
        assertNotNull(Security.getProvider("BC"), "Bouncy Castle provider should be registered by the business logic");
    }

    /**
     * Tests that decrypting an invalid encrypted text throws an <b>IllegalArgumentException</b>.
     */
    @Test
    void testDecryptInvalid() {
        String salt = "mysalt";
        String invalidEncrypted = "invalidEncryptedText";
        String algorithm = "PBEWITHSHA256AND128BITAES-CBC-BC";
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            encryptDecryptLogic.decrypt(salt, invalidEncrypted, algorithm);
        });
        assertTrue(exception.getMessage().contains("Error during decryption"),
                "Exception message should indicate error during decryption");
    }

    /**
     * Tests that <b>isAlgorithmSupported</b> returns true for a supported algorithm.
     */
    @Test
    void testIsAlgorithmSupportedTrue() {
        String algorithm = "PBEWITHSHA256AND256BITAES-CBC-BC";
        boolean supported = encryptDecryptLogic.isAlgorithmSupported(algorithm);
        assertTrue(supported, "Algorithm should be supported");
    }

    /**
     * Tests that <b>isAlgorithmSupported</b> returns false for an unsupported algorithm.
     */
    @Test
    void testIsAlgorithmSupportedFalse() {
        String algorithm = "NON_EXISTENT_ALGORITHM";
        boolean supported = encryptDecryptLogic.isAlgorithmSupported(algorithm);
        assertFalse(supported, "Algorithm should not be supported");
    }

    // ===== Jasypt compatibility (custom iterations) =====

    @Test
    void testEncryptDecryptWithCustomIterations() throws NoSuchAlgorithmException {
        String algorithm = "PBEWithMD5AndDES";
        int jasyptIterations = 1_000;

        String encrypted = encryptDecryptLogic.encrypt("jasyptsalt", "jasyptText", algorithm, jasyptIterations);
        assertNotNull(encrypted, "Encrypted result should not be null");

        String decrypted = encryptDecryptLogic.decrypt("jasyptsalt", encrypted, algorithm, jasyptIterations);
        assertEquals("jasyptText", decrypted, "Decrypted text must match original plaintext");
    }

    @Test
    void testEncryptDecryptAllJasyptAlgorithms() throws NoSuchAlgorithmException {
        int jasyptIterations = 1_000;
        for (String algorithm : AlgorithmRegistry.JASYPT_ALGORITHMS.values()) {
            if (!encryptDecryptLogic.isAlgorithmSupported(algorithm)) continue;
            String encrypted = encryptDecryptLogic.encrypt("salt", "text", algorithm, jasyptIterations);
            assertNotNull(encrypted, "Encrypted result should not be null for: " + algorithm);
            String decrypted = encryptDecryptLogic.decrypt("salt", encrypted, algorithm, jasyptIterations);
            assertEquals("text", decrypted, "Round-trip failed for Jasypt algorithm: " + algorithm);
        }
    }

    @Test
    void testDefaultOverloadDelegatesToDefaultIterations() throws NoSuchAlgorithmException {
        String algorithm = "PBEWITHSHA256AND128BITAES-CBC-BC";
        String encryptedDefault = encryptDecryptLogic.encrypt("salt", "text", algorithm);
        String decrypted = encryptDecryptLogic.decrypt("salt", encryptedDefault, algorithm, 600_000);
        assertEquals("text", decrypted, "Default overload must use DEFAULT_ITERATIONS");
    }

    /**
     * Tests that weak/deprecated algorithms have been removed from the registry.
     */
    @Test
    void testRemovedAlgorithmsNotInRegistry() {
        assertFalse(AlgorithmRegistry.ALGORITHMS.values().contains("PBEWITHMD5ANDDES"),
                "MD5+DES must not be in the registry");
        assertFalse(AlgorithmRegistry.ALGORITHMS.values().contains("PBEWITHSHA1ANDDESEDE"),
                "SHA1+3DES must not be in the registry");
        assertFalse(AlgorithmRegistry.ALGORITHMS.values().contains("PBEWITHSHAAND128BITAES-CBC-BC"),
                "SHA1+AES-128 must not be in the registry");
        assertFalse(AlgorithmRegistry.ALGORITHMS.values().contains("PBEWITHSHAAND256BITAES-CBC-BC"),
                "SHA1+AES-256 must not be in the registry");
        assertFalse(AlgorithmRegistry.ALGORITHMS.values().contains("PBEWITHMD5ANDTRIPLEDES"),
                "MD5+3DES must not be in the registry");
        assertFalse(AlgorithmRegistry.ALGORITHMS.values().contains("PBEWITHHMACSHA1ANDAES_128"),
                "HMAC-SHA1+AES-128 must not be in the registry");
        assertFalse(AlgorithmRegistry.ALGORITHMS.values().contains("PBEWITHHMACSHA1ANDAES_256"),
                "HMAC-SHA1+AES-256 must not be in the registry");
        assertFalse(AlgorithmRegistry.ALGORITHMS.values().contains("PBEWITHHMACSHA512ANDAES_128"),
                "HMAC-SHA512+AES-128 must not be in the registry");
        assertFalse(AlgorithmRegistry.ALGORITHMS.values().contains("PBEWITHHMACSHA512ANDAES_256"),
                "HMAC-SHA512+AES-256 must not be in the registry");
    }
}
