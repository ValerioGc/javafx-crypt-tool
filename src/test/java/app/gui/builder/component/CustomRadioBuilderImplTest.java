package app.gui.builder.component;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import app.testutil.FxTestSupport;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;

/**
 * Test class for {@link CustomRadioBuilder}.
 * <p>
 * Verifies that the toggle button row initialises with Encrypt selected,
 * and that clicking each button correctly updates the selected operation.
 * </p>
 */
class CustomRadioBuilderImplTest {

    @BeforeAll
    static void initJFX() {
        FxTestSupport.startFx();
    }

    @Test
    void testCreateRadioButtonsRow() throws Exception {
        CustomRadioBuilder builder = new CustomRadioBuilder();
        assertEquals(CustomRadioBuilder.OPERATION_ENCRYPT, builder.getSelectedOperation(),
            "Encrypt should be the fallback operation before the row is created");

        HBox container = builder.createRadioButtonsRow();

        assertNotNull(container, "The HBox should not be null");
        assertEquals(2, container.getChildren().size(), "The HBox should contain exactly 2 children");

        Button encryptBtn = (Button) container.getChildren().get(0);
        Button decryptBtn = (Button) container.getChildren().get(1);

        assertNotNull(encryptBtn, "Encrypt button should not be null");
        assertNotNull(decryptBtn, "Decrypt button should not be null");

        // Encrypt is active by default
        assertEquals(CustomRadioBuilder.OPERATION_ENCRYPT, builder.getSelectedOperation(),
            "Encrypt should be selected by default");
        assertTrue(encryptBtn.getStyleClass().contains("mode_toggle_btn_active"),
            "Encrypt button should have the active class initially");
        assertFalse(decryptBtn.getStyleClass().contains("mode_toggle_btn_active"),
            "Decrypt button should NOT have the active class initially");

        // Click decrypt and verify selection switches
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            decryptBtn.fire();
            latch.countDown();
        });
        assertTrue(latch.await(5, TimeUnit.SECONDS), "Timeout waiting for FX event processing");

        assertEquals(CustomRadioBuilder.OPERATION_DECRYPT, builder.getSelectedOperation(),
            "Decrypt should be selected after clicking the decrypt button");
        assertTrue(decryptBtn.getStyleClass().contains("mode_toggle_btn_active"),
            "Decrypt button should have the active class after click");
        assertFalse(encryptBtn.getStyleClass().contains("mode_toggle_btn_active"),
            "Encrypt button should NOT have the active class after decrypt click");

        CountDownLatch latch2 = new CountDownLatch(1);
        Platform.runLater(() -> {
            encryptBtn.fire();
            latch2.countDown();
        });
        assertTrue(latch2.await(5, TimeUnit.SECONDS), "Timeout waiting for FX event processing");

        assertEquals(CustomRadioBuilder.OPERATION_ENCRYPT, builder.getSelectedOperation(),
            "Encrypt should be selected again after clicking the encrypt button");
        assertTrue(encryptBtn.getStyleClass().contains("mode_toggle_btn_active"),
            "Encrypt button should have the active class after click");
        assertFalse(decryptBtn.getStyleClass().contains("mode_toggle_btn_active"),
            "Decrypt button should NOT have the active class after encrypt click");

        builder.updateTexts();
        assertFalse(encryptBtn.getText().isBlank(), "Encrypt button text should be refreshed");
        assertFalse(decryptBtn.getText().isBlank(), "Decrypt button text should be refreshed");
    }
}
