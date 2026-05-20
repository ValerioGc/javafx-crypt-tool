package app.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;

class ApplicationExceptionTest {

    @Test
    void testStartApplicationExceptionConstructors() {
        RuntimeException cause = new RuntimeException("root");

        StartApplicationException withoutCause = new StartApplicationException("failed");
        StartApplicationException withCause = new StartApplicationException("failed", cause);

        assertEquals("Start application error: failed", withoutCause.getMessage());
        assertEquals("Start application error: failed", withCause.getMessage());
        assertSame(cause, withCause.getCause());
    }

    @Test
    void testDecryptionInitializationExceptionConstructors() {
        RuntimeException cause = new RuntimeException("provider");

        DecryptionInitializationException withoutCause = new DecryptionInitializationException("invalid algorithm");
        DecryptionInitializationException withCause = new DecryptionInitializationException("invalid algorithm", cause);

        assertEquals("invalid algorithm", withoutCause.getMessage());
        assertEquals("invalid algorithm", withCause.getMessage());
        assertSame(cause, withCause.getCause());
    }

    @Test
    void testDecryptionOperationExceptionConstructors() {
        RuntimeException cause = new RuntimeException("cipher");

        DecryptionOperationException withoutCause = new DecryptionOperationException("invalid input");
        DecryptionOperationException withCause = new DecryptionOperationException("invalid input", cause);

        assertEquals("invalid input", withoutCause.getMessage());
        assertEquals("invalid input", withCause.getMessage());
        assertSame(cause, withCause.getCause());
    }

    @Test
    void testResourceLoadingExceptionMessage() {
        ResourceLoadingException exception = new ResourceLoadingException("/missing.png");

        assertEquals("Resource not found: /missing.png", exception.getMessage());
    }
}
