package app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.util.stream.Collectors;

import javax.crypto.Cipher;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import app.config.security.AlgorithmRegistry;
import app.config.security.CryptoConfig;
import app.config.security.PBEEncryptor;
import app.exception.StartApplicationException;
import app.gui.EncryptPasswordGUI;


/**
 * <h1>Main Application Launcher</h1>
 * <p>
 * This class contains the main method that starts the application,
 * initializing the necessary resources and handling any required configurations.
 * </p>
 * 
 * @author <b>ValerioGc</b>
 * @version 1.0.0
 * @since 1.0.0
 * @see app.config.AppSettings AppSettings
 * @see <a href="https://github.com/ValerioGc/java-crypt-tool">GitHub Repository</a>
*/
public class MainLauncher {

    private static final Logger logger = LogManager.getLogger(MainLauncher.class);

	public static void main(String[] args) throws StartApplicationException {
    	try {
            bootstrap();
            EncryptPasswordGUI.main(args);
    	} catch(Exception e) {
            logger.error("Unable to start application", e);
    		throw new StartApplicationException("Unable to start application", e);
        }
    }
	
    static void bootstrap() throws NoSuchAlgorithmException {
        printLogo();

        Security.setProperty("crypto.policy", "unlimited");
        CryptoConfig.ensureProviderRegistered();
        initSecurity();
    }

	 
    /**
     * Initializes the <b>security configuration</b> by:
     * <ul>
     *   <li>Logging the current cryptography policy's AES key length.</li>
     *   <li>Testing and logging each algorithm defined in {@link AlgorithmRegistry#ALGORITHMS}.</li>
     *   <li>Listing all available security providers.</li>
     * </ul>
     *
     * @throws NoSuchAlgorithmException if a requested cryptographic algorithm is not available
     */
	private static void initSecurity() throws NoSuchAlgorithmException {

		logger.info("Policy di crittografia attuale: {}", Cipher.getMaxAllowedKeyLength("AES"));
        logger.info("##### Algoritmi supportati #####");
        for (String algo : AlgorithmRegistry.ALGORITHMS.values()) {
            if (PBEEncryptor.isSupported(algo))
                logger.info("Supported: {}", algo);
            else
                logger.info("NOT Supported: {}", algo);
        }
        
        // ##################### Security Provider ##########################
        logger.info("Elenco provider sicurezza disponibili:");
        for (var provider : Security.getProviders())       	
            logger.info("{}", provider.getName());
        
	}
    
    /**
     * Print the application logo in console and logger on start
    */
    private static void printLogo() {

 	   String fileName = "logo.txt";

       try (InputStream inputStream = MainLauncher.class.getClassLoader().getResourceAsStream(fileName)) {
           if (inputStream == null) {
               logger.warn("Logo resource not found: {}", fileName);
               return;
           }

    	   try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
    	       String asciiArt = reader
                    .lines()
                    .collect(Collectors.joining("\n"));

	           logger.info("\n {}", asciiArt);
           }
		} catch (IOException e) {
			logger.error("Error loading logo", e);
		}
    }
}