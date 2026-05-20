package app.gui.builder.component;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import app.config.security.AlgorithmRegistry;
import app.config.security.PBEEncryptor;
import app.testutil.FxTestSupport;
import app.text.AppMessages;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

class CustomInputBuilderImplTest {

    @BeforeAll
    static void initJFX() {
        FxTestSupport.startFx();
    }

    @Test
    void testCreateAlgorithmRow() {
        CustomInputBuilder builder = new CustomInputBuilder();
        HBox row = builder.createAlgorithmRow();
        assertNotNull(row, "Row should not be null");
        assertEquals(Pos.TOP_LEFT, row.getAlignment(), "Row alignment should be TOP_LEFT");
        assertEquals(2, row.getChildren().size(), "Row should have exactly 2 children");
        VBox fieldGroup = (VBox) row.getChildren().get(1);
        assertEquals(4, fieldGroup.getChildren().size(), "Field group should have exactly 4 children");
        assertTrue(fieldGroup.getChildren().get(0) instanceof Label, "First field group child should be a Label");
        Object secondChild = fieldGroup.getChildren().get(1);
        assertTrue(secondChild instanceof ComboBox, "Second field group child should be a ComboBox");
        @SuppressWarnings("unchecked")
        ComboBox<String> combo = (ComboBox<String>) secondChild;
        assertEquals(AppMessages.algorithmPlaceholder(), combo.getPromptText(), "ComboBox prompt text should be as expected");
        assertEquals(null, combo.getValue(), "ComboBox value should be null initially");
        assertEquals(combo, builder.getAlgorithmSelector(), "Algorithm selector should match the ComboBox in the row");
        assertTrue(fieldGroup.getChildren().get(2) instanceof javafx.scene.control.CheckBox, "Third child should be the Jasypt CheckBox");
        assertTrue(fieldGroup.getChildren().get(3) instanceof HBox, "Fourth child should be the iterations HBox");
    }

    @Test
    void testAlgorithmSelectionStoresInternalValueAndCanBeCleared() {
        CustomInputBuilder builder = new CustomInputBuilder();
        builder.createAlgorithmRow();

        ComboBox<String> combo = builder.getAlgorithmSelector();
        combo.setValue("SHA256 + AES (128-bit)");

        assertEquals("PBEWITHSHA256AND128BITAES-CBC-BC", combo.getUserData(),
                "Selecting a label should store the corresponding crypto algorithm");

        combo.setValue(null);

        assertNull(combo.getUserData(), "Clearing the selection should clear the stored crypto algorithm");
    }

    @Test
    void testCreateSaltingInput() {
        CustomInputBuilder builder = new CustomInputBuilder();
        HBox row = builder.createSaltingInput();
        assertNotNull(row, "Row should not be null");
        assertEquals(Pos.TOP_LEFT, row.getAlignment(), "Row alignment should be TOP_LEFT");
        assertEquals(2, row.getChildren().size(), "Row should have exactly 2 children");
        VBox fieldGroup = (VBox) row.getChildren().get(1);
        assertEquals(2, fieldGroup.getChildren().size(), "Field group should have exactly 2 children");
        assertTrue(fieldGroup.getChildren().get(0) instanceof Label, "First field group child should be a Label");
        // Second child is now an HBox wrapping the PasswordField and the toggle button
        Object secondChild = fieldGroup.getChildren().get(1);
        assertTrue(secondChild instanceof HBox, "Second field group child should be an HBox (input + toggle)");
        HBox inputRow = (HBox) secondChild;
        assertTrue(inputRow.getChildren().get(0) instanceof PasswordField, "First input row child should be a PasswordField");
        PasswordField saltingField = (PasswordField) inputRow.getChildren().get(0);
        assertEquals(AppMessages.saltingPlaceholder(), saltingField.getPromptText(), "Salting field prompt text should be as expected");
        assertEquals(saltingField, builder.getSaltingInput(), "Salting input should match the PasswordField in the row");
    }

    @Test
    void testCreateTextInput() {
        CustomInputBuilder builder = new CustomInputBuilder();
        HBox row = builder.createTextInput();
        assertNotNull(row, "Row should not be null");
        assertEquals(Pos.TOP_LEFT, row.getAlignment(), "Row alignment should be TOP_LEFT");
        assertEquals(2, row.getChildren().size(), "Row should have exactly 2 children");
        VBox fieldGroup = (VBox) row.getChildren().get(1);
        assertEquals(2, fieldGroup.getChildren().size(), "Field group should have exactly 2 children");
        assertTrue(fieldGroup.getChildren().get(0) instanceof Label, "First field group child should be a Label");
        Object secondChild = fieldGroup.getChildren().get(1);
        assertTrue(secondChild instanceof TextArea, "Second field group child should be a TextArea");
        TextArea textArea = (TextArea) secondChild;
        assertEquals(AppMessages.placeholderPassword(), textArea.getPromptText(), "Text area prompt text should be as expected");
        assertEquals(textArea, builder.getTextInput(), "Text input should match the TextArea in the row");
    }

    @Test
    void testUpdateTextsBeforeAndAfterCreatingControls() {
        CustomInputBuilder builder = new CustomInputBuilder();

        builder.updateTexts();

        builder.createAlgorithmRow();
        builder.createSaltingInput();
        builder.createTextInput();
        builder.updateTexts();

        assertEquals(AppMessages.algorithmPlaceholder(), builder.getAlgorithmSelector().getPromptText());
        assertEquals(AppMessages.saltingPlaceholder(), builder.getSaltingInput().getPromptText());
        assertEquals(AppMessages.placeholderPassword(), builder.getTextInput().getPromptText());
    }

    // ===== Jasypt mode controls =====

    @Test
    void testJasyptCheckboxExistsAndDefaultsToUnchecked() {
        CustomInputBuilder builder = new CustomInputBuilder();
        builder.createAlgorithmRow();

        CheckBox checkbox = builder.getJasyptCheckbox();
        assertNotNull(checkbox, "Jasypt checkbox should not be null after createAlgorithmRow");
        assertFalse(checkbox.isSelected(), "Jasypt checkbox should be unchecked by default");
        assertEquals(AppMessages.jasyptModeLabel(), checkbox.getText(), "Jasypt checkbox label should match i18n");
    }

    @Test
    void testIterationsFieldDefaultsTo1000AndIsHidden() {
        CustomInputBuilder builder = new CustomInputBuilder();
        builder.createAlgorithmRow();

        TextField iterationsField = builder.getIterationsField();
        assertNotNull(iterationsField, "Iterations field should not be null after createAlgorithmRow");
        assertEquals("1000", iterationsField.getText(), "Iterations field should default to 1000");
        // visibility is controlled on the parent HBox (iterationsRow), not the field itself
        assertFalse(iterationsField.getParent().isVisible(), "Iterations row should be hidden by default");
        assertFalse(iterationsField.getParent().isManaged(), "Iterations row should not be managed by default");
    }

    @Test
    void testJasyptToggleSwapsAlgorithmItemsAndResetsSelection() throws Exception {
        CustomInputBuilder builder = new CustomInputBuilder();
        builder.createAlgorithmRow();
        ComboBox<String> combo = builder.getAlgorithmSelector();
        CheckBox checkbox = builder.getJasyptCheckbox();

        assertTrue(combo.getItems().containsAll(AlgorithmRegistry.ALGORITHMS.keySet()),
                "ComboBox should show standard algorithms by default");

        CountDownLatch onLatch = new CountDownLatch(1);
        Platform.runLater(() -> { checkbox.setSelected(true); onLatch.countDown(); });
        assertTrue(onLatch.await(5, TimeUnit.SECONDS));

        List<String> supportedJasyptKeys = AlgorithmRegistry.JASYPT_ALGORITHMS.entrySet().stream()
                .filter(e -> PBEEncryptor.isSupported(e.getValue()))
                .map(Map.Entry::getKey)
                .toList();
        assertTrue(combo.getItems().containsAll(supportedJasyptKeys),
                "ComboBox should show supported Jasypt algorithms when mode is enabled");
        assertNull(combo.getValue(), "Algorithm selection should be reset on Jasypt toggle");

        CountDownLatch offLatch = new CountDownLatch(1);
        Platform.runLater(() -> { checkbox.setSelected(false); offLatch.countDown(); });
        assertTrue(offLatch.await(5, TimeUnit.SECONDS));

        assertTrue(combo.getItems().containsAll(AlgorithmRegistry.ALGORITHMS.keySet()),
                "ComboBox should restore standard algorithms when Jasypt mode is disabled");
    }

    @Test
    void testJasyptToggleShowsAndHidesIterationsField() throws Exception {
        CustomInputBuilder builder = new CustomInputBuilder();
        builder.createAlgorithmRow();
        CheckBox checkbox = builder.getJasyptCheckbox();
        TextField iterationsField = builder.getIterationsField();

        CountDownLatch onLatch = new CountDownLatch(1);
        Platform.runLater(() -> { checkbox.setSelected(true); onLatch.countDown(); });
        assertTrue(onLatch.await(5, TimeUnit.SECONDS));

        assertTrue(iterationsField.getParent().isVisible(), "Iterations row should be visible in Jasypt mode");
        assertTrue(iterationsField.getParent().isManaged(), "Iterations row should be managed in Jasypt mode");

        CountDownLatch offLatch = new CountDownLatch(1);
        Platform.runLater(() -> { checkbox.setSelected(false); offLatch.countDown(); });
        assertTrue(offLatch.await(5, TimeUnit.SECONDS));

        assertFalse(iterationsField.getParent().isVisible(), "Iterations row should be hidden when Jasypt mode is off");
        assertFalse(iterationsField.getParent().isManaged(), "Iterations row should not be managed when Jasypt mode is off");
    }
}
