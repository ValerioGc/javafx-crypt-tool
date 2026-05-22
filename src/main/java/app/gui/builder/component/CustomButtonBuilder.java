package app.gui.builder.component;

import java.security.NoSuchAlgorithmException;
import java.util.Objects;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

import app.business.EncryptDecryptLogic;
import app.business.impl.EncryptDecryptLogicImpl;
import app.config.AppAssets;
import app.exception.DecryptionOperationException;
import app.text.AppMessages;
import app.util.AppUtils;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputControl;
import javafx.scene.control.Tooltip;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;


public class CustomButtonBuilder {

    private static final double COPY_ICON_SIZE = 18;
    private static final AppUtils appUtils = new AppUtils();
    private final EncryptDecryptLogic encryptDecryptLogic;
    private Button runButton;
    private Button copyButton;

    public CustomButtonBuilder() {
        this(new EncryptDecryptLogicImpl());
    }

    public CustomButtonBuilder(EncryptDecryptLogic encryptDecryptLogic) {
        this.encryptDecryptLogic = Objects.requireNonNull(encryptDecryptLogic);
    }

    public record RunOutputs(TextInputControl resultField, Label errorLabel, Node resultSection) {}

    public Button createRunButton(RunOutputs outputs, Supplier<String> operationSupplier,
                                  ComboBox<String> algorithmSelector,
                                  TextInputControl saltingInput, TextInputControl textInput,
                                  IntSupplier iterationsSupplier
    ) {
        runButton = new Button("▶  " + AppMessages.buttonRunLabel());
        runButton.getStyleClass().add("btn_run");
        runButton.setTooltip(new Tooltip(AppMessages.buttonRunTooltip()));
        runButton.setDisable(true);

        runButton.setOnAction(event -> {
            String salt = saltingInput.getText();
            String input = textInput.getText();
            String algorithmValue = (String) algorithmSelector.getUserData();

            outputs.resultSection().setVisible(false);
            outputs.resultSection().setManaged(false);
            outputs.errorLabel().setVisible(false);
            outputs.errorLabel().setManaged(false);

            if (salt.isBlank() || input.isBlank() || algorithmValue == null) {
                outputs.errorLabel().setText(AppMessages.errorRequiredFields());
                outputs.errorLabel().setVisible(true);
                outputs.errorLabel().setManaged(true);
                return;
            }

            try {
                String operation = operationSupplier.get();
                int iterations = iterationsSupplier.getAsInt();
                String output = CustomRadioBuilder.OPERATION_ENCRYPT.equals(operation)
                    ? encryptDecryptLogic.encrypt(salt, input, algorithmValue, iterations)
                    : encryptDecryptLogic.decrypt(salt, input, algorithmValue, iterations);

                outputs.resultField().setText(output);
                outputs.resultSection().setVisible(true);
                outputs.resultSection().setManaged(true);
            } catch (DecryptionOperationException | NoSuchAlgorithmException e) {
                outputs.errorLabel().setText(AppMessages.errorOperationFailed());
                outputs.errorLabel().setVisible(true);
                outputs.errorLabel().setManaged(true);
            }
        });

        return runButton;
    }

    public Button createCopyButton(TextInputControl resultField) {
        copyButton = new Button();
        javafx.scene.image.ImageView copyIcon = appUtils.loadImage(AppAssets.COPY_ICON, COPY_ICON_SIZE, COPY_ICON_SIZE);
        copyIcon.getStyleClass().add("icon_invert");
        copyButton.setGraphic(copyIcon);
        copyButton.getStyleClass().add("icon_button");
        copyButton.setTooltip(new Tooltip(AppMessages.buttonCopyTooltip()));

        copyButton.setOnAction(event -> {
            Clipboard clipboard = Clipboard.getSystemClipboard();
            ClipboardContent content = new ClipboardContent();
            content.putString(resultField.getText());
            clipboard.setContent(content);
        });

        return copyButton;
    }

    public void updateTexts() {
        if (runButton != null) {
            runButton.setText("▶  " + AppMessages.buttonRunLabel());
            runButton.getTooltip().setText(AppMessages.buttonRunTooltip());
        }
        if (copyButton != null && copyButton.getTooltip() != null)
            copyButton.getTooltip().setText(AppMessages.buttonCopyTooltip());
    }
}
