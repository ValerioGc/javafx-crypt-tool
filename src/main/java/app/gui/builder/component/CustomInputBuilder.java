package app.gui.builder.component;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import app.config.AppAssets;
import app.config.security.AlgorithmRegistry;
import app.config.security.PBEEncryptor;
import app.text.AppMessages;
import app.util.AppUtils;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;


public class CustomInputBuilder {

    private ComboBox<String> algorithmSelector;
    private TextField saltingInput;
    private TextField clearSaltingInput;
    private Button saltingToggleButton;
    private TextArea textInput;
    private Label algorithmLabel;
    private Label saltingLabel;
    private Label textLabel;
    private CheckBox jasyptCheckbox;
    private TextField iterationsField;
    private Label iterationsLabel;

    private static final Logger logger = LogManager.getLogger(CustomInputBuilder.class);
    private static final String FORM_LABEL_CLASS = "form_label";
    private static final String TEXT_FIELD_CLASS = "text_field";
    private static final AppUtils appUtils = new AppUtils();

    public ComboBox<String> getAlgorithmSelector() { return algorithmSelector; }
    public TextField getSaltingInput()             { return saltingInput; }
    public TextArea getTextInput()                 { return textInput; }
    public CheckBox getJasyptCheckbox()            { return jasyptCheckbox; }
    public TextField getIterationsField()          { return iterationsField; }

    public HBox createAlgorithmRow() {
        ImageView icon = createFormIcon(AppAssets.ALGORITHM_ICON);

        algorithmLabel = new Label(AppMessages.algorithmLabel());
        algorithmLabel.getStyleClass().add(FORM_LABEL_CLASS);

        algorithmSelector = new ComboBox<>(FXCollections.observableArrayList(AlgorithmRegistry.ALGORITHMS.keySet()));
        algorithmSelector.setPromptText(AppMessages.algorithmPlaceholder());
        algorithmSelector.setValue(null);
        algorithmSelector.getStyleClass().add("combo_box");
        algorithmSelector.setMinWidth(300);
        algorithmSelector.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(algorithmSelector, Priority.ALWAYS);

        jasyptCheckbox = new CheckBox(AppMessages.jasyptModeLabel());
        jasyptCheckbox.getStyleClass().add("jasypt_checkbox");

        iterationsLabel = new Label(AppMessages.jasyptIterationsLabel());
        iterationsLabel.getStyleClass().add(FORM_LABEL_CLASS);

        iterationsField = new TextField("1000");
        iterationsField.getStyleClass().add(TEXT_FIELD_CLASS);
        // Override pref-width: 100% and min-height: 42px from text_field CSS — not suitable for a small numeric input
        iterationsField.setStyle("-fx-pref-width: 110; -fx-min-height: 36;");
        iterationsField.setMinWidth(80);
        iterationsField.setMaxWidth(140);
        iterationsField.setTextFormatter(new TextFormatter<>(change ->
            change.getControlNewText().matches("\\d*") ? change : null
        ));

        HBox iterationsRow = new HBox(10, iterationsLabel, iterationsField);
        iterationsRow.setAlignment(Pos.CENTER_LEFT);
        iterationsRow.setMaxWidth(Double.MAX_VALUE);
        iterationsRow.setVisible(false);
        iterationsRow.setManaged(false);

        algorithmSelector.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                Map<String, String> map = jasyptCheckbox.isSelected()
                    ? AlgorithmRegistry.JASYPT_ALGORITHMS
                    : AlgorithmRegistry.ALGORITHMS;
                algorithmSelector.setUserData(map.get(newValue));
                logger.debug("Selected: {} -> value: {}", newValue, map.get(newValue));
            } else {
                algorithmSelector.setUserData(null);
                logger.debug("No algorithm selected");
            }
        });

        var supportedJasyptNames = AlgorithmRegistry.JASYPT_ALGORITHMS.entrySet().stream()
            .filter(e -> PBEEncryptor.isSupported(e.getValue()))
            .map(Map.Entry::getKey)
            .toList();

        jasyptCheckbox.selectedProperty().addListener((obs, o, selected) -> {
            algorithmSelector.setValue(null);
            algorithmSelector.setUserData(null);
            if (selected) {
                algorithmSelector.setItems(FXCollections.observableArrayList(supportedJasyptNames));
                iterationsRow.setVisible(true);
                iterationsRow.setManaged(true);
            } else {
                algorithmSelector.setItems(FXCollections.observableArrayList(AlgorithmRegistry.ALGORITHMS.keySet()));
                iterationsRow.setVisible(false);
                iterationsRow.setManaged(false);
            }
        });

        VBox fieldGroup = new VBox(7, algorithmLabel, algorithmSelector, jasyptCheckbox, iterationsRow);
        fieldGroup.getStyleClass().add("field_group");
        fieldGroup.setAlignment(Pos.CENTER_LEFT);
        fieldGroup.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(fieldGroup, Priority.ALWAYS);

        HBox row = new HBox(14, icon, fieldGroup);
        row.setAlignment(Pos.TOP_LEFT);
        row.setMaxWidth(Double.MAX_VALUE);
        row.getStyleClass().add("form_row");
        return row;
    }

    public HBox createSaltingInput() {
        ImageView icon = createFormIcon(AppAssets.SALTING_ICON);

        saltingLabel = new Label(AppMessages.saltingLabel());
        saltingLabel.getStyleClass().add(FORM_LABEL_CLASS);

        PasswordField maskedInput = new PasswordField();
        maskedInput.setPromptText(AppMessages.saltingPlaceholder());
        maskedInput.setMaxWidth(Double.MAX_VALUE);
        maskedInput.getStyleClass().add(TEXT_FIELD_CLASS);
        HBox.setHgrow(maskedInput, Priority.ALWAYS);

        clearSaltingInput = new TextField();
        clearSaltingInput.setPromptText(AppMessages.saltingPlaceholder());
        clearSaltingInput.setMaxWidth(Double.MAX_VALUE);
        clearSaltingInput.getStyleClass().add(TEXT_FIELD_CLASS);
        clearSaltingInput.setVisible(false);
        clearSaltingInput.setManaged(false);
        HBox.setHgrow(clearSaltingInput, Priority.ALWAYS);

        clearSaltingInput.textProperty().bindBidirectional(maskedInput.textProperty());

        ImageView eyeIcon = appUtils.loadImage(AppAssets.EYE_ICON, 18, 18);
        ImageView eyeSlashIcon = appUtils.loadImage(AppAssets.EYE_SLASH_ICON, 18, 18);

        saltingToggleButton = new Button();
        saltingToggleButton.setGraphic(eyeIcon);
        saltingToggleButton.getStyleClass().add("icon_button");
        saltingToggleButton.setTooltip(new Tooltip(AppMessages.passwordToggleTooltip()));
        saltingToggleButton.setOnAction(e -> {
            boolean showing = clearSaltingInput.isVisible();
            clearSaltingInput.setVisible(!showing);
            clearSaltingInput.setManaged(!showing);
            maskedInput.setVisible(showing);
            maskedInput.setManaged(showing);
            saltingToggleButton.setGraphic(!showing ? eyeSlashIcon : eyeIcon);
        });

        saltingInput = maskedInput;

        HBox inputRow = new HBox(6, maskedInput, clearSaltingInput, saltingToggleButton);
        inputRow.setAlignment(Pos.CENTER_LEFT);
        inputRow.setMaxWidth(Double.MAX_VALUE);

        return createFormRow(icon, saltingLabel, inputRow);
    }

    public HBox createTextInput() {
        ImageView icon = createFormIcon(AppAssets.PASSWORD_ICON);

        textLabel = new Label(AppMessages.labelPassword());
        textLabel.getStyleClass().add(FORM_LABEL_CLASS);

        textInput = new TextArea();
        textInput.setPromptText(AppMessages.placeholderPassword());
        textInput.setWrapText(true);
        textInput.setPrefRowCount(3);
        textInput.setMaxWidth(Double.MAX_VALUE);
        textInput.getStyleClass().add("text_area");
        VBox.setVgrow(textInput, Priority.ALWAYS);

        return createFormRow(icon, textLabel, textInput);
    }

    public void updateTexts() {
        if (algorithmLabel != null)
            algorithmLabel.setText(AppMessages.algorithmLabel());
        if (algorithmSelector != null)
            algorithmSelector.setPromptText(AppMessages.algorithmPlaceholder());
        if (saltingLabel != null)
            saltingLabel.setText(AppMessages.saltingLabel());
        if (saltingInput != null)
            saltingInput.setPromptText(AppMessages.saltingPlaceholder());
        if (clearSaltingInput != null)
            clearSaltingInput.setPromptText(AppMessages.saltingPlaceholder());
        if (saltingToggleButton != null && saltingToggleButton.getTooltip() != null)
            saltingToggleButton.getTooltip().setText(AppMessages.passwordToggleTooltip());
        if (textLabel != null)
            textLabel.setText(AppMessages.labelPassword());
        if (textInput != null)
            textInput.setPromptText(AppMessages.placeholderPassword());
        if (jasyptCheckbox != null)
            jasyptCheckbox.setText(AppMessages.jasyptModeLabel());
        if (iterationsLabel != null)
            iterationsLabel.setText(AppMessages.jasyptIterationsLabel());
    }

    private ImageView createFormIcon(String resourcePath) {
        ImageView icon = appUtils.loadImage(resourcePath, 30, 30);
        icon.getStyleClass().add("form_icon");
        return icon;
    }

    private HBox createFormRow(ImageView icon, Label label, javafx.scene.Node control) {
        VBox fieldGroup = new VBox(7, label, control);
        fieldGroup.getStyleClass().add("field_group");
        fieldGroup.setAlignment(Pos.CENTER_LEFT);
        fieldGroup.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(fieldGroup, Priority.ALWAYS);

        HBox row = new HBox(14, icon, fieldGroup);
        row.setAlignment(Pos.TOP_LEFT);
        row.setMaxWidth(Double.MAX_VALUE);
        row.getStyleClass().add("form_row");
        return row;
    }
}
