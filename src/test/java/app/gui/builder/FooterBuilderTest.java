package app.gui.builder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import app.config.AppSettingsLoader;
import app.testutil.FxTestSupport;
import app.text.AppMessages;
import javafx.geometry.Pos;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

class FooterBuilderTest {

    @BeforeAll
    static void initJFX() {
        FxTestSupport.startFx();
    }

    @Test
    void testBuildCreatesVersionSeparatorAndLink() {
        FooterBuilder builder = new FooterBuilder();
        HBox footer = builder.build();

        assertNotNull(footer, "Footer should be created");
        assertEquals(Pos.CENTER, footer.getAlignment(), "Footer should be centered");
        assertTrue(footer.getStyleClass().contains("app_footer"), "Footer should expose the app_footer style class");
        assertEquals(3, footer.getChildren().size(), "Footer should contain version, separator and GitHub link");

        Label version = (Label) footer.getChildren().get(0);
        Label separator = (Label) footer.getChildren().get(1);
        Hyperlink link = (Hyperlink) footer.getChildren().get(2);

        assertEquals(AppMessages.footerVersionLabel() + " " + AppSettingsLoader.load().version(), version.getText());
        assertEquals("|", separator.getText());
        assertEquals(AppMessages.footerGithubLabel(), link.getText());
        assertTrue(version.getStyleClass().contains("footer_text"));
        assertTrue(link.getStyleClass().contains("footer_link"));
    }

    @Test
    void testUpdateTextsBeforeAndAfterBuild() {
        FooterBuilder builder = new FooterBuilder();

        builder.updateTexts();

        HBox footer = builder.build();
        builder.updateTexts();

        Label version = (Label) footer.getChildren().get(0);
        Hyperlink link = (Hyperlink) footer.getChildren().get(2);
        assertEquals(AppMessages.footerVersionLabel() + " " + AppSettingsLoader.load().version(), version.getText());
        assertEquals(AppMessages.footerGithubLabel(), link.getText());
    }
}
