package app.exception;

/**
 * Custom exception for general errors occurring during the application's startup.
 * <p>
 * This exception is thrown when a problem occurs during the initialization or startup process of the application.
 * It provides detailed error messages and, optionally, the underlying cause of the failure.
 * </p>
*/
public class StartApplicationException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new StartApplicationException with a specified detail message.
     *
     * @param message the detail error message.
    */
    public StartApplicationException(String message) {
        super("Start application error: " + message);
    }

    /**
     * Constructs a new StartApplicationException with a specified detail message and cause.
     *
     * @param message the detail error message.
     * @param cause   the underlying cause of the exception.
    */
    public StartApplicationException(String message, Throwable cause) {
        super("Start application error: " + message, cause);
    }
}