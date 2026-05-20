package app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import app.config.security.CryptoConfig;
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
 * @see <a href="https://github.com/ValerioGc/javafx-crypt-tool">GitHub Repository</a>
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