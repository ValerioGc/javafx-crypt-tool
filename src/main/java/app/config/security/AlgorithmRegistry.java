package app.config.security;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public final class AlgorithmRegistry {

    public static final Map<String, String> ALGORITHMS = Map.of(
        "SHA256 + AES (128-bit)", "PBEWITHSHA256AND128BITAES-CBC-BC",
        "SHA256 + AES (192-bit)", "PBEWITHSHA256AND192BITAES-CBC-BC",
        "SHA256 + AES (256-bit)", "PBEWITHSHA256AND256BITAES-CBC-BC"
    );

    public static final Map<String, String> JASYPT_ALGORITHMS;

    static {
        Map<String, String> m = new LinkedHashMap<>();

        // SHA256 + AES — modern, recommended with BC provider
        m.put("SHA256 + AES (128-bit)",        "PBEWITHSHA256AND128BITAES-CBC-BC");
        m.put("SHA256 + AES (192-bit)",        "PBEWITHSHA256AND192BITAES-CBC-BC");
        m.put("SHA256 + AES (256-bit)",        "PBEWITHSHA256AND256BITAES-CBC-BC");

        // SHA1 + AES — BC-specific, acceptable
        m.put("SHA1 + AES (128-bit)",          "PBEWITHSHA1AND128BITAES-CBC-BC");
        m.put("SHA1 + AES (192-bit)",          "PBEWITHSHA1AND192BITAES-CBC-BC");
        m.put("SHA1 + AES (256-bit)",          "PBEWITHSHA1AND256BITAES-CBC-BC");

        // MD5 + AES — OpenSSL-compatible KDF (weaker derivation, no PBKDF2)
        m.put("MD5 + AES-128 [OpenSSL]",       "PBEWITHMD5AND128BITAES-CBC-OPENSSL");
        m.put("MD5 + AES-192 [OpenSSL]",       "PBEWITHMD5AND192BITAES-CBC-OPENSSL");
        m.put("MD5 + AES-256 [OpenSSL]",       "PBEWITHMD5AND256BITAES-CBC-OPENSSL");

        // MD5 + legacy block ciphers
        m.put("MD5 + DES (default Jasypt)",    "PBEWithMD5AndDES");
        m.put("MD5 + TripleDES",               "PBEWithMD5AndTripleDES");

        // SHA1 + DES / TripleDES family
        m.put("SHA1 + DES",                    "PBEWithSHA1AndDES");
        m.put("SHA1 + DESede (3-key)",         "PBEWithSHA1AndDESede");
        m.put("SHA + 2-Key TripleDES",          "PBEWithSHAAnd2-KeyTripleDES-CBC");
        m.put("SHA + 3-Key TripleDES",          "PBEWithSHAAnd3-KeyTripleDES-CBC");

        // SHA1 + RC2 (legacy stream-ish block cipher)
        m.put("SHA1 + RC2 (40-bit)",           "PBEWithSHA1AndRC2_40");
        m.put("SHA1 + RC2 (128-bit)",          "PBEWithSHA1AndRC2_128");

        // SHA + RC4 — stream cipher, cryptographically weak
        m.put("SHA + RC4 (40-bit)",            "PBEWithSHAAnd40BitRC4");
        m.put("SHA + RC4 (128-bit)",           "PBEWithSHAAnd128BitRC4");

        JASYPT_ALGORITHMS = Collections.unmodifiableMap(m);
    }

    private AlgorithmRegistry() {
    }
}