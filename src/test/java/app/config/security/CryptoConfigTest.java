package app.config.security;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.security.Provider;
import java.security.Security;

import org.junit.jupiter.api.Test;

/**
 * Test class for {@link CryptoConfig}.
 */
class CryptoConfigTest {

    @Test
    void testBouncyCastleProviderIsRegistered() {
        Security.removeProvider("BC");
        assertNull(Security.getProvider("BC"), "Bouncy Castle Provider should be absent before configuration");

        CryptoConfig.ensureProviderRegistered();

        Provider bcProvider = Security.getProvider("BC");
        assertNotNull(bcProvider, "Bouncy Castle Provider deve essere registrato");
    }
}
