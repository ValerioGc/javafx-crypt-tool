package app.gui.builder;

import java.net.URL;

import app.config.AppSettings;
import app.config.AppSettingsLoader;
import app.exception.StartApplicationException;
import app.gui.builder.component.CustomButtonBuilder;
import app.gui.builder.component.CustomInputBuilder;
import app.gui.builder.component.CustomRadioBuilder;
import app.text.AppMessages;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import app.config.security.PBEEncryptor;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.shape.Rectangle;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Main scene builder class.
*/
public class MainSceneBuilder {

    private static final AppSettings appSettings = AppSettingsLoader.load();
    private MainSceneBuilder() {
    }

    public static Scene createScene(Stage stage) throws StartApplicationException {
        CustomButtonBuilder customButtonBuilder = new CustomButtonBuilder();
        CustomRadioBuilder customRadioBuilder = new CustomRadioBuilder();
        CustomInputBuilder customInputBuilder = new CustomInputBuilder();
        HeaderBuilder headerBuilder = new HeaderBuilder();
        FooterBuilder footerBuilder = new FooterBuilder();

        // ======================== Form components ========================
        HBox algorithmBox = customInputBuilder.createAlgorithmRow();
        ComboBox<String> algorithmSelector = customInputBuilder.getAlgorithmSelector();
        CheckBox jasyptCheckbox = customInputBuilder.getJasyptCheckbox();
        TextField iterationsField = customInputBuilder.getIterationsField();

        HBox saltingBox = customInputBuilder.createSaltingInput();
        TextField saltingInput = customInputBuilder.getSaltingInput();

        HBox textBox = customInputBuilder.createTextInput();
        TextArea textInput = customInputBuilder.getTextInput();

        HBox modeToggleRow = customRadioBuilder.createRadioButtonsRow();
        Label modeLabel = new Label(AppMessages.modeLabel());
        modeLabel.getStyleClass().add("form_label");
        VBox modeSection = new VBox(8, modeLabel, modeToggleRow);
        modeSection.getStyleClass().add("field_group");
        modeSection.setMaxWidth(Double.MAX_VALUE);

        // ======================== Result & error ========================
        TextArea resultArea = new TextArea();
        resultArea.getStyleClass().add("result_field");
        resultArea.setEditable(false);
        resultArea.setFocusTraversable(false);
        resultArea.setWrapText(true);
        resultArea.setPrefRowCount(3);
        resultArea.setMaxWidth(Double.MAX_VALUE);
        VBox.setVgrow(resultArea, Priority.ALWAYS);

        Label errorLabel = new Label();
        errorLabel.getStyleClass().add("error_text");
        errorLabel.setMaxWidth(Double.MAX_VALUE);
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);

        // ======================== Buttons ========================
        Button copyButton = customButtonBuilder.createCopyButton(resultArea);
        StackPane.setAlignment(copyButton, Pos.TOP_RIGHT);
        StackPane.setMargin(copyButton, new Insets(6, 6, 0, 0));

        StackPane resultSection = new StackPane(resultArea, copyButton);
        resultSection.setMaxWidth(Double.MAX_VALUE);
        resultSection.setVisible(false);
        resultSection.setManaged(false);

        Button runButton = customButtonBuilder.createRunButton(
            new CustomButtonBuilder.RunOutputs(resultArea, errorLabel, resultSection),
            customRadioBuilder::getSelectedOperation,
            algorithmSelector, saltingInput, textInput,
            () -> {
                if (!jasyptCheckbox.isSelected()) return PBEEncryptor.DEFAULT_ITERATIONS;
                try { return Integer.parseInt(iterationsField.getText()); }
                catch (NumberFormatException e) { return PBEEncryptor.DEFAULT_ITERATIONS; }
            }
        );

        HBox runRow = new HBox(runButton);
        runRow.setAlignment(Pos.CENTER);
        runRow.setMaxWidth(Double.MAX_VALUE);

        HBox errorRow = new HBox(errorLabel);
        errorRow.setAlignment(Pos.CENTER);
        HBox.setHgrow(errorLabel, Priority.ALWAYS);
        errorLabel.setAlignment(Pos.CENTER);

        // ======================== Event listeners ========================
        Runnable updateState = () ->
            updateRunButtonState(runButton, saltingInput, textInput, algorithmSelector, jasyptCheckbox, iterationsField);

        saltingInput.textProperty().addListener((obs, o, n) -> updateState.run());
        textInput.textProperty().addListener((obs, o, n) -> updateState.run());
        algorithmSelector.valueProperty().addListener((obs, o, n) -> updateState.run());
        jasyptCheckbox.selectedProperty().addListener((obs, o, n) -> updateState.run());
        iterationsField.textProperty().addListener((obs, o, n) -> updateState.run());

        // ======================== Layout ========================
        VBox root = new VBox();
        root.getStyleClass().addAll("app_root", "theme_light");

        VBox view = new VBox(0);
        view.setFillWidth(true);
        view.setMaxWidth(Double.MAX_VALUE);
        view.getStyleClass().add("content_panel");

        VBox body = new VBox(20);
        body.setPadding(new Insets(appSettings.viewPadding(), 24, 0, 24));
        body.setFillWidth(true);
        body.setMaxWidth(Double.MAX_VALUE);
        body.getStyleClass().add("content_body");

        Runnable updateTexts = () -> {
            customInputBuilder.updateTexts();
            customRadioBuilder.updateTexts();
            customButtonBuilder.updateTexts();
            headerBuilder.updateTexts();
            footerBuilder.updateTexts();
            modeLabel.setText(AppMessages.modeLabel());
        };

        body.getChildren().addAll(algorithmBox, saltingBox, textBox, modeSection, runRow, resultSection, errorRow, footerBuilder.build());

        ScrollPane scrollPane = new ScrollPane(body);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.getStyleClass().add("content_scroll");
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        Rectangle viewClip = new Rectangle();
        viewClip.setArcWidth(40);
        viewClip.setArcHeight(40);
        view.layoutBoundsProperty().addListener((obs, old, bounds) -> {
            viewClip.setWidth(bounds.getWidth());
            viewClip.setHeight(bounds.getHeight());
        });
        view.setClip(viewClip);

        view.getChildren().addAll(headerBuilder.build(root, stage, updateTexts), scrollPane);
        VBox.setVgrow(view, Priority.ALWAYS);
        root.getChildren().add(view);

        Scene scene = new Scene(root, appSettings.windowWidth(), appSettings.windowHeight(), javafx.scene.paint.Color.TRANSPARENT);

        URL cssResource = MainSceneBuilder.class.getResource("/main.css");
        if (cssResource == null)
            throw new StartApplicationException(AppMessages.styleNotFound());

        scene.getStylesheets().add(cssResource.toExternalForm());
        return scene;
    }

    private static void updateRunButtonState(Button runButton, TextField saltingInput, TextInputControl textInput,
            ComboBox<String> algorithmSelector, CheckBox jasyptCheckbox, TextField iterationsField) {
        boolean isAlgorithmSelected = algorithmSelector.getUserData() != null;
        boolean isSaltingFilled = !saltingInput.getText().isBlank();
        boolean isTextFilled = !textInput.getText().isBlank();
        boolean isIterationsValid = !jasyptCheckbox.isSelected() || isValidIterations(iterationsField.getText());
        runButton.setDisable(!(isAlgorithmSelected && isSaltingFilled && isTextFilled && isIterationsValid));
    }

    private static boolean isValidIterations(String text) {
        try {
            return Integer.parseInt(text) > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}