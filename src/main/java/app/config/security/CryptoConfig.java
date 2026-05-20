package app.config.security;

import java.security.Security;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

/**
 * <b>CryptoConfig</b> is responsible for configuring the cryptographic environment for the application.
 * <p>
 * This class ensures that the Bouncy Castle security provider is registered in the Java Security API.
 * If the provider is not already registered, it registers it and logs the registration outcome.
 * </p>
*/
public class CryptoConfig {

    private static final Logger logger = LogManager.getLogger(CryptoConfig.class);

    private CryptoConfig(){ }
    
    /**
     * Initializes the cryptographic configuration.
     * <p>
     * This method checks whether the Bouncy Castle provider ("BC") is already registered.
     * If it is not found, the method adds the provider and logs whether the registration was successful.
     * It then retrieves and logs all supported cipher algorithms from the security providers.
     * </p>
    */
    public static synchronized void ensureProviderRegistered() {
        if (Security.getProvider("BC") != null)
            return;

        Security.addProvider(new BouncyCastleProvider());

        if (Security.getProvider("BC") != null) {
            logger.info("Bouncy Castle has been successfully registered!");
            logger.debug("Supported cipher algorithms: {}", Security.getAlgorithms("Cipher"));
        } else {
            logger.error("Bouncy Castle has NOT been registered.");
        }
    }
}
