package app;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;

/**
 * Test for launcher bootstrap logic without launching the JavaFX application.
 */
class MainLauncherTest {

	@Test
    void testBootstrapInitializesApplicationEnvironment() {
        assertDoesNotThrow(MainLauncher::bootstrap);
    }
}
