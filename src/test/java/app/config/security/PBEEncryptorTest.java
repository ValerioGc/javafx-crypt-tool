package app.config.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.security.GeneralSecurityException;
import java.util.Base64;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;


/**
 * Tests for {@link PBEEncryptor}.
 */
class PBEEncryptorTest {

    private static final String PASSWORD  = "test-password-123";
    private static final String PLAINTEXT = "Hello, Crypt Tool!";
    private static final String ALGORITHM = "PBEWITHSHA256AND256BITAES-CBC-BC";

    @BeforeAll
    static void registerProvider() {
        CryptoConfig.ensureProviderRegistered();
    }

    // ===== Round-trip =====

    @Test
    void testRoundTripAllAlgorithms() throws GeneralSecurityException {
        for (String algo : AlgorithmRegistry.ALGORITHMS.values()) {
            String encrypted = PBEEncryptor.encrypt(PASSWORD, PLAINTEXT, algo);
            assertNotNull(encrypted, "Encrypted output must not be null for: " + algo);

            String decrypted = PBEEncryptor.decrypt(PASSWORD, encrypted, algo);
            assertEquals(PLAINTEXT, decrypted, "Round-trip failed for: " + algo);
        }
    }

    @Test
    void testEncryptProducesBase64() throws GeneralSecurityException {
        String encrypted = PBEEncryptor.encrypt(PASSWORD, PLAINTEXT, ALGORITHM);
        // Must not throw - valid Base64
        byte[] decoded = Base64.getDecoder().decode(encrypted);
        // Decoded length must be > 16 (16 bytes salt + at least 1 block of ciphertext)
        assertTrue(decoded.length > 16, "Output must contain salt + ciphertext");
    }

    @Test
    void testEncryptProducesDifferentOutputEachTime() throws GeneralSecurityException {
        String first  = PBEEncryptor.encrypt(PASSWORD, PLAINTEXT, ALGORITHM);
        String second = PBEEncryptor.encrypt(PASSWORD, PLAINTEXT, ALGORITHM);
        assertNotEquals(first, second, "Each encryption must produce a unique output (random salt)");
    }

    // ===== Wrong password =====

    @Test
    void testDecryptWithWrongPasswordThrows() throws GeneralSecurityException {
        String encrypted = PBEEncryptor.encrypt(PASSWORD, PLAINTEXT, ALGORITHM);
        assertThrows(GeneralSecurityException.class, () ->
                PBEEncryptor.decrypt("wrong-password", encrypted, ALGORITHM));
    }

    // ===== Malformed input =====

    @Test
    void testDecryptInvalidBase64Throws() {
        assertThrows(IllegalArgumentException.class, () ->
                PBEEncryptor.decrypt(PASSWORD, "not-valid-base64!!!", ALGORITHM));
    }

    @Test
    void testDecryptDataTooShortThrows() {
        // 8 bytes < SALT_LENGTH (16) → must throw
        String tooShort = Base64.getEncoder().encodeToString(new byte[8]);
        assertThrows(IllegalArgumentException.class, () ->
                PBEEncryptor.decrypt(PASSWORD, tooShort, ALGORITHM));
    }

    @Test
    void testDecryptEmptyStringThrows() {
        assertThrows(IllegalArgumentException.class, () ->
                PBEEncryptor.decrypt(PASSWORD, "", ALGORITHM));
    }

    // ===== isSupported =====

    @Test
    void testIsSupportedReturnsTrueForValidAlgorithm() {
        assertTrue(PBEEncryptor.isSupported(ALGORITHM));
    }

    @Test
    void testIsSupportedReturnsFalseForUnknownAlgorithm() {
        assertFalse(PBEEncryptor.isSupported("NON_EXISTENT_ALGORITHM"));
    }

    @Test
    void testIsSupportedReturnsTrueForAllRegisteredAlgorithms() {
        for (String algo : AlgorithmRegistry.ALGORITHMS.values())
            assertTrue(PBEEncryptor.isSupported(algo), "Algorithm must be supported: " + algo);
    }

    // ===== DEFAULT_ITERATIONS constant =====

    @Test
    void testDefaultIterationsConstantValue() {
        assertEquals(600_000, PBEEncryptor.DEFAULT_ITERATIONS,
                "DEFAULT_ITERATIONS must be 600,000 for standard javafx-crypt-tool mode");
    }

    // ===== Custom iterations (Jasypt compatibility) =====

    @Test
    void testRoundTripWithCustomIterations() throws GeneralSecurityException {
        String encrypted = PBEEncryptor.encrypt(PASSWORD, PLAINTEXT, ALGORITHM, 1_000);
        String decrypted = PBEEncryptor.decrypt(PASSWORD, encrypted, ALGORITHM, 1_000);
        assertEquals(PLAINTEXT, decrypted, "Round-trip with custom iterations must restore original plaintext");
    }

    @Test
    void testRoundTripAllJasyptAlgorithms() throws GeneralSecurityException {
        for (String algo : AlgorithmRegistry.JASYPT_ALGORITHMS.values()) {
            if (!PBEEncryptor.isSupported(algo)) continue;
            String encrypted = PBEEncryptor.encrypt(PASSWORD, PLAINTEXT, algo, 1_000);
            String decrypted = PBEEncryptor.decrypt(PASSWORD, encrypted, algo, 1_000);
            assertEquals(PLAINTEXT, decrypted, "Jasypt round-trip failed for: " + algo);
        }
    }

    @Test
    void testDifferentIterationsYieldIncompatibleCiphertext() throws GeneralSecurityException {
        String encryptedWith1000 = PBEEncryptor.encrypt(PASSWORD, PLAINTEXT, ALGORITHM, 1_000);
        assertThrows(GeneralSecurityException.class, () ->
                PBEEncryptor.decrypt(PASSWORD, encryptedWith1000, ALGORITHM, PBEEncryptor.DEFAULT_ITERATIONS),
                "Decrypting with wrong iterations count must fail");
    }

    @Test
    void testIsSupportedForJasyptDefaultAlgorithm() {
        assertTrue(PBEEncryptor.isSupported("PBEWithMD5AndDES"),
                "Jasypt default algorithm PBEWithMD5AndDES must be supported by the BC provider");
    }

    @Test
    void testIsSupportedForCommonJasyptAlgorithms() {
        assertTrue(PBEEncryptor.isSupported("PBEWithMD5AndDES"),                    "PBEWithMD5AndDES must be supported");
        assertTrue(PBEEncryptor.isSupported("PBEWithSHA1AndDES"),                   "PBEWithSHA1AndDES must be supported");
        assertTrue(PBEEncryptor.isSupported("PBEWithSHA1AndDESede"),                "PBEWithSHA1AndDESede must be supported");
        assertTrue(PBEEncryptor.isSupported("PBEWithSHAAnd3-KeyTripleDES-CBC"),     "PBEWithSHAAnd3-KeyTripleDES-CBC must be supported");
        assertTrue(PBEEncryptor.isSupported("PBEWithSHAAnd2-KeyTripleDES-CBC"),     "PBEWithSHAAnd2-KeyTripleDES-CBC must be supported");
        assertTrue(PBEEncryptor.isSupported("PBEWITHSHA256AND128BITAES-CBC-BC"),    "SHA256+AES-128 must be supported");
        assertTrue(PBEEncryptor.isSupported("PBEWITHSHA256AND192BITAES-CBC-BC"),    "SHA256+AES-192 must be supported");
        assertTrue(PBEEncryptor.isSupported("PBEWITHSHA256AND256BITAES-CBC-BC"),    "SHA256+AES-256 must be supported");
        assertTrue(PBEEncryptor.isSupported("PBEWITHSHA1AND128BITAES-CBC-BC"),      "SHA1+AES-128 must be supported");
        assertTrue(PBEEncryptor.isSupported("PBEWITHSHA1AND256BITAES-CBC-BC"),      "SHA1+AES-256 must be supported");
        assertTrue(PBEEncryptor.isSupported("PBEWITHMD5AND128BITAES-CBC-OPENSSL"),  "MD5+AES-128 OpenSSL must be supported");
        assertTrue(PBEEncryptor.isSupported("PBEWITHMD5AND256BITAES-CBC-OPENSSL"),  "MD5+AES-256 OpenSSL must be supported");
        assertTrue(PBEEncryptor.isSupported("PBEWithSHAAnd40BitRC4"),               "SHA+RC4-40 must be supported");
        assertTrue(PBEEncryptor.isSupported("PBEWithSHAAnd128BitRC4"),              "SHA+RC4-128 must be supported");
    }

    @Test
    void testRoundTripNewJasyptAlgorithms() throws GeneralSecurityException {
        String[] algosSafe = {
            "PBEWITHSHA256AND192BITAES-CBC-BC",
            "PBEWITHSHA1AND128BITAES-CBC-BC",
            "PBEWITHSHA1AND256BITAES-CBC-BC",
            "PBEWITHMD5AND128BITAES-CBC-OPENSSL",
            "PBEWITHMD5AND256BITAES-CBC-OPENSSL",
            "PBEWithSHA1AndDES",
            "PBEWithSHAAnd3-KeyTripleDES-CBC",
            "PBEWithSHAAnd2-KeyTripleDES-CBC",
        };
        for (String algo : algosSafe) {
            if (!PBEEncryptor.isSupported(algo)) continue;
            String encrypted = PBEEncryptor.encrypt(PASSWORD, PLAINTEXT, algo, 1_000);
            String decrypted = PBEEncryptor.decrypt(PASSWORD, encrypted, algo, 1_000);
            assertEquals(PLAINTEXT, decrypted, "Round-trip failed for: " + algo);
        }
    }
}
