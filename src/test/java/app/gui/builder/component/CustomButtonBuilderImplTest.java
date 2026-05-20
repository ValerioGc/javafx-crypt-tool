package app.gui.builder.component;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import app.business.EncryptDecryptLogic;
import app.config.security.PBEEncryptor;
import app.testutil.FxTestSupport;
import app.text.AppMessages;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;

class CustomButtonBuilderImplTest {

    @BeforeAll
    static void initJFXAndBC() {
        FxTestSupport.startFx();
    }

    @Test
    void testCreateRunButtonSuccess() throws Exception {
        CustomButtonBuilder builder = new CustomButtonBuilder();

        TextField resultField = new TextField();
        Label errorLabel = new Label();
        Button copyButton = new Button();
        ComboBox<String> algorithmSelector = new ComboBox<>();
        algorithmSelector.setUserData("PBEWITHSHA256AND128BITAES-CBC-BC");
        TextField saltingInput = new TextField("mysalt");
        TextField passwordInput = new TextField("myPassword");

        Button runButton = builder.createRunButton(
            new CustomButtonBuilder.RunOutputs(resultField, errorLabel, copyButton),
            () -> CustomRadioBuilder.OPERATION_ENCRYPT,
            algorithmSelector, saltingInput, passwordInput,
            () -> PBEEncryptor.DEFAULT_ITERATIONS
        );

        Platform.runLater(() -> runButton.setDisable(false));

        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> { runButton.fire(); latch.countDown(); });
        assertTrue(latch.await(5, TimeUnit.SECONDS), "Timeout waiting for runButton action");

        CountDownLatch latch2 = new CountDownLatch(1);
        Platform.runLater(latch2::countDown);
        assertTrue(latch2.await(5, TimeUnit.SECONDS), "Timeout waiting for FX event processing");

        assertTrue(resultField.isVisible(), "Result field should be visible on success");
        assertTrue(resultField.isManaged(), "Result field should be managed on success");
        assertTrue(copyButton.isVisible(), "Copy button should be visible on success");
        assertTrue(copyButton.isManaged(), "Copy button should be managed on success");
        assertFalse(errorLabel.isVisible(), "Error label should be invisible on success");
        assertFalse(errorLabel.isManaged(), "Error label should not be managed on success");
        assertNotNull(resultField.getText(), "Result field text should not be null");
        assertFalse(resultField.getText().isEmpty(), "Result field text should not be empty");
    }

    @Test
    void testCreateRunButtonUsesInjectedLogic() throws Exception {
        EncryptDecryptLogic fakeLogic = new EncryptDecryptLogic() {
            @Override
            public String encrypt(String salt, String plainText, String algorithmName) {
                return salt + ":" + plainText + ":" + algorithmName;
            }

            @Override
            public String decrypt(String salt, String encryptedText, String algorithm) {
                return "unused";
            }

            @Override
            public boolean isAlgorithmSupported(String algorithm) {
                return true;
            }
        };
        CustomButtonBuilder builder = new CustomButtonBuilder(fakeLogic);

        TextField resultField = new TextField();
        Label errorLabel = new Label();
        Button copyButton = new Button();
        ComboBox<String> algorithmSelector = new ComboBox<>();
        algorithmSelector.setUserData("TEST-ALGO");
        TextField saltingInput = new TextField("salt");
        TextField passwordInput = new TextField("input");

        Button runButton = builder.createRunButton(
            new CustomButtonBuilder.RunOutputs(resultField, errorLabel, copyButton),
            () -> CustomRadioBuilder.OPERATION_ENCRYPT,
            algorithmSelector, saltingInput, passwordInput,
            () -> PBEEncryptor.DEFAULT_ITERATIONS
        );

        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> { runButton.setDisable(false); runButton.fire(); latch.countDown(); });
        assertTrue(latch.await(5, TimeUnit.SECONDS), "Timeout waiting for injected run action");

        assertEquals("salt:input:TEST-ALGO", resultField.getText());
        assertTrue(resultField.isVisible(), "Injected logic result should be shown");
    }

    @Test
    void testCreateRunButtonDecryptSuccess() throws Exception {
        CustomButtonBuilder builder = new CustomButtonBuilder();

        TextField encryptedResult = new TextField();
        Label encryptError = new Label();
        Button encryptCopyButton = new Button();
        ComboBox<String> algorithmSelector = new ComboBox<>();
        algorithmSelector.setUserData("PBEWITHSHA256AND128BITAES-CBC-BC");
        TextField saltingInput = new TextField("mysalt");
        TextField passwordInput = new TextField("plainText");

        Button encryptButton = builder.createRunButton(
            new CustomButtonBuilder.RunOutputs(encryptedResult, encryptError, encryptCopyButton),
            () -> CustomRadioBuilder.OPERATION_ENCRYPT,
            algorithmSelector, saltingInput, passwordInput,
            () -> PBEEncryptor.DEFAULT_ITERATIONS
        );

        CountDownLatch encryptLatch = new CountDownLatch(1);
        Platform.runLater(() -> { encryptButton.setDisable(false); encryptButton.fire(); encryptLatch.countDown(); });
        assertTrue(encryptLatch.await(5, TimeUnit.SECONDS), "Timeout waiting for encrypt action");

        TextField decryptResult = new TextField();
        Label decryptError = new Label();
        Button decryptCopyButton = new Button();
        TextField encryptedInput = new TextField(encryptedResult.getText());

        Button decryptButton = builder.createRunButton(
            new CustomButtonBuilder.RunOutputs(decryptResult, decryptError, decryptCopyButton),
            () -> CustomRadioBuilder.OPERATION_DECRYPT,
            algorithmSelector, saltingInput, encryptedInput,
            () -> PBEEncryptor.DEFAULT_ITERATIONS
        );

        CountDownLatch decryptLatch = new CountDownLatch(1);
        Platform.runLater(() -> { decryptButton.setDisable(false); decryptButton.fire(); decryptLatch.countDown(); });
        assertTrue(decryptLatch.await(5, TimeUnit.SECONDS), "Timeout waiting for decrypt action");

        assertEquals("plainText", decryptResult.getText(), "Decrypt should restore the original input");
        assertTrue(decryptResult.isVisible(), "Decrypt result should be visible on success");
        assertFalse(decryptError.isVisible(), "Decrypt error should be hidden on success");
    }

    @Test
    void testCreateRunButtonOperationFailureShowsGenericError() throws Exception {
        CustomButtonBuilder builder = new CustomButtonBuilder();

        TextField resultField = new TextField();
        Label errorLabel = new Label();
        Button copyButton = new Button();
        ComboBox<String> algorithmSelector = new ComboBox<>();
        algorithmSelector.setUserData("PBEWITHSHA256AND128BITAES-CBC-BC");
        TextField saltingInput = new TextField("mysalt");
        TextField passwordInput = new TextField("not-valid-ciphertext");

        Button runButton = builder.createRunButton(
            new CustomButtonBuilder.RunOutputs(resultField, errorLabel, copyButton),
            () -> CustomRadioBuilder.OPERATION_DECRYPT,
            algorithmSelector, saltingInput, passwordInput,
            () -> PBEEncryptor.DEFAULT_ITERATIONS
        );

        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> { runButton.setDisable(false); runButton.fire(); latch.countDown(); });
        assertTrue(latch.await(5, TimeUnit.SECONDS), "Timeout waiting for runButton action");

        assertTrue(errorLabel.isVisible(), "Error label should be visible on operation failure");
        assertEquals(AppMessages.errorOperationFailed(), errorLabel.getText(),
                "Operation failures should show the generic localized error");
        assertFalse(copyButton.isVisible(), "Copy button (resultSection) should remain hidden on operation failure");
    }

    @Test
    void testCreateRunButtonFailure() throws Exception {
        CustomButtonBuilder builder = new CustomButtonBuilder();

        TextField resultField = new TextField();
        Label errorLabel = new Label();
        Button copyButton = new Button();
        ComboBox<String> algorithmSelector = new ComboBox<>();
        algorithmSelector.setUserData(null);
        TextField saltingInput = new TextField("");
        TextField passwordInput = new TextField("myPassword");

        Button runButton = builder.createRunButton(
            new CustomButtonBuilder.RunOutputs(resultField, errorLabel, copyButton),
            () -> CustomRadioBuilder.OPERATION_ENCRYPT,
            algorithmSelector, saltingInput, passwordInput,
            () -> PBEEncryptor.DEFAULT_ITERATIONS
        );

        Platform.runLater(() -> runButton.setDisable(false));

        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> { runButton.fire(); latch.countDown(); });
        assertTrue(latch.await(5, TimeUnit.SECONDS), "Timeout waiting for runButton action");

        CountDownLatch latch2 = new CountDownLatch(1);
        Platform.runLater(latch2::countDown);
        assertTrue(latch2.await(5, TimeUnit.SECONDS), "Timeout waiting for FX event processing");

        assertTrue(errorLabel.isVisible(), "Error label should be visible on failure");
        assertTrue(errorLabel.isManaged(), "Error label should be managed on failure");
        assertEquals(AppMessages.errorRequiredFields(), errorLabel.getText(),
                "Error label text should match expected message");
        assertFalse(copyButton.isVisible(), "Copy button (resultSection) should remain invisible on failure");
    }

    @Test
    void testCreateCopyButton() throws Exception {
        CustomButtonBuilder builder = new CustomButtonBuilder();
        TextField resultField = new TextField("CopiedText");
        Button copyButton = builder.createCopyButton(resultField);

        assertTrue(copyButton.getGraphic() instanceof ImageView, "Copy button graphic should be an ImageView");
        ImageView icon = (ImageView) copyButton.getGraphic();
        assertEquals(18, icon.getFitWidth(), "Copy icon should have a fixed width");
        assertEquals(18, icon.getFitHeight(), "Copy icon should have a fixed height");
        assertTrue(icon.isPreserveRatio(), "Copy icon should preserve its aspect ratio");

        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> { copyButton.fire(); latch.countDown(); });
        assertTrue(latch.await(5, TimeUnit.SECONDS), "Timeout waiting for copyButton action");

        CountDownLatch latch2 = new CountDownLatch(1);
        final String[] clipboardText = new String[1];
        Platform.runLater(() -> { clipboardText[0] = Clipboard.getSystemClipboard().getString(); latch2.countDown(); });
        assertTrue(latch2.await(5, TimeUnit.SECONDS), "Timeout waiting for FX event processing");
        assertEquals("CopiedText", clipboardText[0], "Clipboard should contain the text from the result field");
    }

    @Test
    void testUpdateTextsBeforeAndAfterCreatingButtons() {
        CustomButtonBuilder builder = new CustomButtonBuilder();

        builder.updateTexts();

        TextField resultField = new TextField();
        Label errorLabel = new Label();
        Button copyButton = builder.createCopyButton(resultField);
        ComboBox<String> algorithmSelector = new ComboBox<>();
        TextField saltingInput = new TextField();
        TextField passwordInput = new TextField();
        Button runButton = builder.createRunButton(
            new CustomButtonBuilder.RunOutputs(resultField, errorLabel, copyButton),
            () -> CustomRadioBuilder.OPERATION_ENCRYPT,
            algorithmSelector, saltingInput, passwordInput,
            () -> PBEEncryptor.DEFAULT_ITERATIONS
        );

        builder.updateTexts();

        assertEquals(AppMessages.buttonRunTooltip(), runButton.getTooltip().getText());
        assertEquals(AppMessages.buttonCopyTooltip(), copyButton.getTooltip().getText());
    }
}
