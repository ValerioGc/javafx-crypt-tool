package app.exception;

import java.security.NoSuchAlgorithmException;

/**
 * Custom exception indicating a failure during the initialization of the decryption algorithm.
 * <p>
 * This exception is thrown when an error occurs during the initialization of the algorithm used for
 * decryption. It extends {@link NoSuchAlgorithmException} to provide additional context regarding the failure,
 * typically due to an unavailable algorithm or misconfiguration.
 * </p>
*/
public class DecryptionInitializationException extends NoSuchAlgorithmException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new DecryptionInitializationException with the specified detail message.
     *
     * @param message the detail error message
    */
    public DecryptionInitializationException(String message) {
        super(message);
    }

    /**
     * Constructs a new DecryptionInitializationException with the specified detail message and cause.
     *
     * @param message the detail error message
     * @param cause   the underlying cause of the exception
    */
    public DecryptionInitializationException(String message, Throwable cause) {
        super(message, cause);
    }
}
