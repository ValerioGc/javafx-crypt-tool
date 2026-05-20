package app.exception;

/**
 * Custom exception indicating an error occurred during the decryption operation.
 * <p>
 * This exception is thrown when the decryption process fails, typically because of invalid input parameters or
 * an unexpected problem during the operation. It extends {@link IllegalArgumentException} to offer more
 * context specific to decryption failures.
 * </p>
*/
public class DecryptionOperationException extends IllegalArgumentException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new DecryptionOperationException with the specified detail message.
     *
     * @param message the detail error message
     */
    public DecryptionOperationException(String message) {
        super(message);
    }

    /**
     * Constructs a new DecryptionOperationException with the specified detail message and cause.
     *
     * @param message the detail error message
     * @param cause   the underlying cause of the exception
    */
    public DecryptionOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}