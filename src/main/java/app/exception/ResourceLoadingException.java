package app.exception;

/**
 * Thrown when an application resource cannot be loaded from the classpath.
*/
public class ResourceLoadingException extends IllegalStateException {

    private static final long serialVersionUID = 1L;

    public ResourceLoadingException(String resourcePath) {
        super("Resource not found: " + resourcePath);
    }
}