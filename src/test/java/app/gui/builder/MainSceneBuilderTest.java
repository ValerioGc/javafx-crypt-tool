package app.gui.builder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Locale;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import app.config.AppSettingsLoader;
import app.exception.StartApplicationException;
import app.text.AppMessages;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.MenuButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Test class for verifying the construction and configuration of the main scene.
 * <p>
 * This class uses the TestFX framework to initialize a JavaFX environment,
 * allowing for comprehensive UI testing of the scene built by the MainSceneBuilder.
 * </p>
 */
class MainSceneBuilderTest extends ApplicationTest {

    private Scene scene;

    /**
     * This method is automatically invoked by the TestFX framework to start the JavaFX application.
     * <p>
     * It creates the main scene using the {@code MainSceneBuilder}, sets the scene on the provided stage,
     * and displays the stage. This ensures that the scene is fully initialized and ready for testing.
     * </p>
     *
     * @param stage the primary stage provided by TestFX on which the scene is displayed
     * @throws StartApplicationException if an error occurs during the creation or initialization of the scene
     */
    @Override
    public void start(Stage stage) throws StartApplicationException {
        scene = MainSceneBuilder.createScene(stage);
        stage.setScene(scene);
        stage.show();
    }

    @AfterEach
    void resetLocale() {
        AppMessages.setLocale(Locale.ENGLISH);
    }

    /**
     * Tests that the main scene is not null.
     * <p>
     * This test ensures that the scene has been properly instantiated by the {@code MainSceneBuilder}
     * and is available for further UI tests.
     * </p>
     */
    @Test
    void testSceneIsNotNull() {
        assertNotNull(scene, "The scene should not be null");
    }

    /**
     * Tests that the stylesheet has been correctly loaded into the scene.
     * <p>
     * This test verifies that the scene's list of stylesheets is not empty,
     * ensuring that the UI components have the expected styling applied.
     * </p>
     */
    @Test
    void testStylesheetLoaded() {
        assertFalse(scene.getStylesheets().isEmpty(), "The stylesheet should be loaded into the scene");
    }

    /**
     * Tests that the ComboBox used for algorithm selection exists in the scene.
     * <p>
     * This test uses the lookup mechanism provided by JavaFX to search for a node with the CSS class
     * {@code "combo-box"}. The presence of this ComboBox is essential for allowing users to select an algorithm,
     * and its existence confirms the correct setup of the UI component.
     * </p>
     */
    @Test
    void testAlgorithmSelectorExists() {
        ComboBox<?> comboBox = (ComboBox<?>) scene.lookup(".combo-box");
        assertNotNull(comboBox, "The ComboBox for algorithm selection should exist in the scene");
    }

    @Test
    void testDefaultThemeIsLight() {
        assertTrue(scene.getRoot().getStyleClass().contains("theme_light"),
                "Root should have theme_light class by default");
        assertFalse(scene.getRoot().getStyleClass().contains("theme_dark"),
                "Root should not have theme_dark by default");
    }

    @Test
    void testContentBodyUsesConfiguredTopPadding() {
        VBox body = (VBox) scene.lookup(".content_body");

        assertNotNull(body, "Content body should exist");
        assertEquals(AppSettingsLoader.load().viewPadding(), body.getPadding().getTop(),
                "Content body top padding should come from application settings");
    }

    @Test
    void testThemeToggleSwitchesToDark() throws Exception {
        Node header = scene.lookup(".page_header");
        assertNotNull(header, "Header node with page_header class should exist");
        Button themeBtn = (Button) header.lookup(".icon_button");
        assertNotNull(themeBtn, "Theme toggle button with icon_button class should exist in header");

        final boolean[] hasDark  = {false};
        final boolean[] hasLight = {false};

        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            themeBtn.fire();
            hasDark[0]  = scene.getRoot().getStyleClass().contains("theme_dark");
            hasLight[0] = scene.getRoot().getStyleClass().contains("theme_light");
            latch.countDown();
        });
        assertTrue(latch.await(5, TimeUnit.SECONDS), "Timeout waiting for theme toggle");

        assertTrue(hasDark[0],  "Root should have theme_dark after toggle");
        assertFalse(hasLight[0], "Root should not have theme_light after toggle");
    }

    @Test
    void testRequiredFieldListenersUpdateRunButtonState() throws Exception {
        @SuppressWarnings("unchecked")
        ComboBox<String> algorithmSelector = (ComboBox<String>) scene.lookup(".combo-box");
        TextField saltingInput = findTextFieldByPrompt(AppMessages.saltingPlaceholder());
        TextArea textInput = findTextAreaByPrompt(AppMessages.placeholderPassword());
        Button runButton = (Button) scene.lookup(".btn_run");

        assertNotNull(algorithmSelector, "Algorithm selector should exist");
        assertNotNull(saltingInput, "Salting input should exist");
        assertNotNull(textInput, "Text input should exist");
        assertNotNull(runButton, "Run button should exist");
        assertTrue(runButton.isDisabled(), "Run button should start disabled");

        CountDownLatch fillLatch = new CountDownLatch(1);
        Platform.runLater(() -> {
            algorithmSelector.setValue("SHA256 + AES (128-bit)");
            saltingInput.setText("salt");
            textInput.setText("hello");
            fillLatch.countDown();
        });
        assertTrue(fillLatch.await(5, TimeUnit.SECONDS), "Timeout waiting for form filling");

        assertFalse(runButton.isDisabled(), "Run button should be enabled when all required values are present");

        CountDownLatch clearLatch = new CountDownLatch(1);
        Platform.runLater(() -> {
            saltingInput.clear();
            clearLatch.countDown();
        });
        assertTrue(clearLatch.await(5, TimeUnit.SECONDS), "Timeout waiting for salting clear");

        assertTrue(runButton.isDisabled(), "Run button should be disabled again when a required value is missing");
    }

    @Test
    void testLanguageSelectionRefreshesSceneTexts() throws Exception {
        MenuButton languageSelector = (MenuButton) scene.lookup(".selector_button");
        assertNotNull(languageSelector, "Language selector should exist");

        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            languageSelector.getItems().get(1).fire();
            latch.countDown();
        });
        assertTrue(latch.await(5, TimeUnit.SECONDS), "Timeout waiting for language switch");

        Button runButton = (Button) scene.lookup(".btn_run");
        assertNotNull(runButton, "Run button should exist");
        assertEquals(AppMessages.buttonRunTooltip(), runButton.getTooltip().getText(),
                "Run button tooltip should be refreshed after language switch");
    }

    // ===== Jasypt mode =====

    @Test
    void testJasyptCheckboxExistsAndIsUncheckedByDefault() {
        CheckBox jasyptCheckbox = (CheckBox) scene.lookup(".jasypt_checkbox");
        assertNotNull(jasyptCheckbox, "Jasypt mode checkbox should exist in the scene");
        assertFalse(jasyptCheckbox.isSelected(), "Jasypt checkbox should be unchecked by default");
    }

    @Test
    void testJasyptModeResetsAlgorithmAndDisablesRunButton() throws Exception {
        @SuppressWarnings("unchecked")
        ComboBox<String> algorithmSelector = (ComboBox<String>) scene.lookup(".combo-box");
        TextField saltingInput = findTextFieldByPrompt(AppMessages.saltingPlaceholder());
        TextArea textInput = findTextAreaByPrompt(AppMessages.placeholderPassword());
        Button runButton = (Button) scene.lookup(".btn_run");
        CheckBox jasyptCheckbox = (CheckBox) scene.lookup(".jasypt_checkbox");

        CountDownLatch fillLatch = new CountDownLatch(1);
        Platform.runLater(() -> {
            algorithmSelector.setValue("SHA256 + AES (128-bit)");
            saltingInput.setText("salt");
            textInput.setText("hello");
            fillLatch.countDown();
        });
        assertTrue(fillLatch.await(5, TimeUnit.SECONDS));
        assertFalse(runButton.isDisabled(), "Run button should be enabled when all fields are filled");

        CountDownLatch jasyptLatch = new CountDownLatch(1);
        Platform.runLater(() -> { jasyptCheckbox.setSelected(true); jasyptLatch.countDown(); });
        assertTrue(jasyptLatch.await(5, TimeUnit.SECONDS));

        assertTrue(runButton.isDisabled(),
                "Run button should be disabled after Jasypt toggle resets the algorithm selection");
    }

    @Test
    void testJasyptModeEnablesRunButtonWithValidIterations() throws Exception {
        @SuppressWarnings("unchecked")
        ComboBox<String> algorithmSelector = (ComboBox<String>) scene.lookup(".combo-box");
        TextField saltingInput = findTextFieldByPrompt(AppMessages.saltingPlaceholder());
        TextArea textInput = findTextAreaByPrompt(AppMessages.placeholderPassword());
        Button runButton = (Button) scene.lookup(".btn_run");
        CheckBox jasyptCheckbox = (CheckBox) scene.lookup(".jasypt_checkbox");

        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            jasyptCheckbox.setSelected(true);
            algorithmSelector.setValue("MD5 + DES (default Jasypt)");
            saltingInput.setText("salt");
            textInput.setText("hello");
            latch.countDown();
        });
        assertTrue(latch.await(5, TimeUnit.SECONDS));

        assertFalse(runButton.isDisabled(),
                "Run button should be enabled in Jasypt mode when all fields and iterations are valid");
    }

    private TextField findTextFieldByPrompt(String promptText) {
        return scene.getRoot().lookupAll(".text-field")
            .stream()
            .filter(TextField.class::isInstance)
            .map(TextField.class::cast)
            .filter(field -> promptText.equals(field.getPromptText()))
            .findFirst()
            .orElse(null);
    }

    private TextArea findTextAreaByPrompt(String promptText) {
        return scene.getRoot().lookupAll(".text-area")
            .stream()
            .filter(TextArea.class::isInstance)
            .map(TextArea.class::cast)
            .filter(area -> promptText.equals(area.getPromptText()))
            .findFirst()
            .orElse(null);
    }
}
