package app.gui;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import app.exception.StartApplicationException;
import app.testutil.FxTestSupport;
import javafx.application.Platform;
import javafx.stage.Stage;


/**
 * Test class for {@link EncryptPasswordGUI} services.
 * @since 1.0.0
 * @version 1.0.0
 */
class EncryptPasswordGuiTest {

    private static Stage primaryStage;

    @BeforeAll
    static void setUp() throws Exception {
        FxTestSupport.startFx();

        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                primaryStage = new Stage();
                new EncryptPasswordGUI().start(primaryStage);
            } catch (StartApplicationException e) {
                throw new RuntimeException(e);
            } finally {
                latch.countDown();
            }
        });
        assertTrue(latch.await(10, TimeUnit.SECONDS), "Timeout starting EncryptPasswordGUI");
    }

    /**
     * Test to verify that the primary stage is not null.
     */
    @Test
    void testPrimaryStageNotNull() {
        assertNotNull(primaryStage, "The primary stage should not be null");
    }

    /**
     * Test to verify that the scene has been set on the primary stage and the UI has loaded.
     */
    @Test
    void testSceneIsSet() {
        assertNotNull(primaryStage.getScene(), "The scene on the primary stage should not be null");
    }
}
