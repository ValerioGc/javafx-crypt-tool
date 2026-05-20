package app.gui.builder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Locale;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import app.text.AppMessages;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

class HeaderBuilderTest extends ApplicationTest {

    private HeaderBuilder builder;
    private VBox root;
    private VBox header;
    private Label formIcon;
    private Stage stage;
    private AtomicInteger languageChanges;

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        builder = new HeaderBuilder();
        root = new VBox();
        root.getStyleClass().add("theme_light");
        formIcon = new Label("icon");
        formIcon.getStyleClass().add("form_icon");
        languageChanges = new AtomicInteger();
        header = builder.build(root, stage, languageChanges::incrementAndGet);
        root.getChildren().addAll(formIcon, header);
        stage.setScene(new Scene(root, 640, 420));
        stage.show();
    }

    @AfterEach
    void resetLocale() {
        AppMessages.setLocale(Locale.ENGLISH);
    }

    @Test
    void testBuildCreatesHeaderControls() {
        assertNotNull(header, "Header should be created");
        assertTrue(header.getStyleClass().contains("page_header"), "Header should expose the page_header style class");
        assertNotNull(header.lookup(".selector_button"), "Language selector should exist");
        assertNotNull(header.lookup(".icon_button"), "Theme selector should exist");
        assertNotNull(header.lookup(".window_titlebar"), "Window titlebar should exist");
    }

    @Test
    void testLanguageSelectionRefreshesTexts() throws Exception {
        MenuButton selector = (MenuButton) header.lookup(".selector_button");
        assertNotNull(selector, "Language selector should exist");

        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            selector.getItems().get(1).fire();
            latch.countDown();
        });
        assertTrue(latch.await(5, TimeUnit.SECONDS), "Timeout waiting for language selection");

        assertEquals(1, languageChanges.get(), "Language change callback should be invoked");
        assertNotNull(selector.getGraphic(), "Language selector graphic should be refreshed");
    }

    @Test
    void testThemeToggleSwitchesBothDirectionsAndUpdatesIconEffects() throws Exception {
        Button themeButton = (Button) header.lookup(".icon_button");
        assertNotNull(themeButton, "Theme button should exist");
        assertNull(formIcon.getEffect(), "Form icon should start without an invert effect");

        CountDownLatch darkLatch = new CountDownLatch(1);
        Platform.runLater(() -> {
            themeButton.fire();
            darkLatch.countDown();
        });
        assertTrue(darkLatch.await(5, TimeUnit.SECONDS), "Timeout waiting for dark theme toggle");

        assertTrue(root.getStyleClass().contains("theme_dark"), "Root should switch to dark theme");
        assertFalse(root.getStyleClass().contains("theme_light"), "Root should remove light theme");
        assertNotNull(formIcon.getEffect(), "Form icon should be inverted in dark mode");

        CountDownLatch lightLatch = new CountDownLatch(1);
        Platform.runLater(() -> {
            themeButton.fire();
            lightLatch.countDown();
        });
        assertTrue(lightLatch.await(5, TimeUnit.SECONDS), "Timeout waiting for light theme toggle");

        assertTrue(root.getStyleClass().contains("theme_light"), "Root should switch back to light theme");
        assertFalse(root.getStyleClass().contains("theme_dark"), "Root should remove dark theme");
        assertNull(formIcon.getEffect(), "Form icon invert effect should be removed in light mode");
    }

    @Test
    void testWindowMinimizeButtonAndUpdateTexts() throws Exception {
        HBox titlebar = (HBox) header.lookup(".window_titlebar");
        assertNotNull(titlebar, "Titlebar should exist");
        Button minimizeButton = null;
        Button closeButton = null;
        for (Node node : titlebar.getChildren()) {
            if (node instanceof Button button && button.getStyleClass().contains("window_btn_close")) {
                closeButton = button;
            } else if (node instanceof Button button) {
                minimizeButton = button;
            }
        }
        assertNotNull(minimizeButton, "Minimize button should exist");
        assertNotNull(closeButton, "Close button should exist");

        CountDownLatch latch = new CountDownLatch(1);
        Button buttonToFire = minimizeButton;
        Platform.runLater(() -> {
            builder.updateTexts();
            buttonToFire.fire();
            latch.countDown();
        });
        assertTrue(latch.await(5, TimeUnit.SECONDS), "Timeout waiting for minimize action");

        assertTrue(stage.isIconified(), "Minimize button should iconify the stage");
        assertEquals(AppMessages.windowMinimizeTooltip(), minimizeButton.getTooltip().getText());
        assertEquals(AppMessages.windowCloseTooltip(), closeButton.getTooltip().getText());
    }
}
