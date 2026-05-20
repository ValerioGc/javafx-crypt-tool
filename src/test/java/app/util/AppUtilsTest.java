package app.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import app.exception.ResourceLoadingException;
import app.testutil.FxTestSupport;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * Test class for {@link AppUtils} services.
 */
class AppUtilsTest {

    private AppUtils appUtils;

    @BeforeEach
    void startFx() {
        FxTestSupport.startFx();
    }

    @BeforeEach
    void setUp() {
        appUtils = new AppUtils();
    }

    /**
     * Test for the createResultBox method.
     * <p>
     * This test creates dummy nodes (a TextField, a Button, and a Label),
     * calls createResultBox and verifies that:
     * <ul>
     *   <li>The returned HBox is not null.</li>
     *   <li>The HBox contains three children in the correct order.</li>
     *   <li>The child nodes are set to be invisible.</li>
     * </ul>
     */
    @Test
    void testCreateResultBox() {
        TextField resultField = new TextField();
        Button copyButton = new Button("Copy");
        Label errorLabel = new Label("Error");

        VBox container = appUtils.createResultBox(resultField, copyButton, errorLabel);

        assertNotNull(container, "Result container should not be null");

        assertEquals(2, container.getChildren().size(), "Result container should have 2 children");

        assertTrue(container.getChildren().get(0) instanceof HBox, "The first child should be an HBox");
        HBox resultBox = (HBox) container.getChildren().get(0);
        assertEquals(2, resultBox.getChildren().size(), "Result box HBox should have 2 children");
        assertSame(resultField, resultBox.getChildren().get(0), "Result field should be the first child in resultBox");
        assertSame(copyButton, resultBox.getChildren().get(1), "Copy button should be the second child in resultBox");

        assertTrue(container.getChildren().get(1) instanceof HBox, "The second child should be an HBox");
        HBox errorBox = (HBox) container.getChildren().get(1);
        assertEquals(1, errorBox.getChildren().size(), "Error box HBox should have 1 child");
        assertSame(errorLabel, errorBox.getChildren().get(0), "Error label should be the only child in errorBox");

        assertFalse(resultField.isVisible(), "Result field should be invisible initially");
        assertFalse(copyButton.isVisible(), "Copy button should be invisible initially");
        assertFalse(errorLabel.isVisible(), "Error label should be invisible initially");

        assertFalse(resultField.isManaged(), "Result field should not be managed when invisible");
        assertFalse(copyButton.isManaged(), "Copy button should not be managed when invisible");
        assertFalse(errorLabel.isManaged(), "Error label should not be managed when invisible");
    }


    /**
     * Test for the loadImage method with a valid resource {@link AppUtils#loadImage(String)}.
     * <p>
     * This test verifies that when a valid resource path is provided,
     * the method returns an ImageView with a non null image.
     * </p>
     */
    @Test
    void testLoadImageValidResource() {
        String resourcePath = "/icons/copy.png";
        ImageView imageView = appUtils.loadImage(resourcePath);
        assertNotNull(imageView, "ImageView should not be null for a valid resource");
        assertNotNull(imageView.getImage(), "The image inside ImageView should not be null");
    }

    /**
     * Test for the loadImage method when the resource is not found.
     * <p>
     * This test verifies that the method throws a ResourceLoadingException with an appropriate
     * message when the provided resource path is invalid.
     * </p>
     */
    @Test
    void testLoadImageFileNotFound() {
        String invalidResourcePath = "/nonexistent.png";
        Exception exception = assertThrows(ResourceLoadingException.class, () -> {
            appUtils.loadImage(invalidResourcePath);
        });
        assertTrue(exception.getMessage().contains("Resource not found"),
                "Exception message should contain 'Resource not found'");
    }
}
