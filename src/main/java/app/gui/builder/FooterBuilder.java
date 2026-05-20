package app.gui.builder;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import app.config.AppSettings;
import app.config.AppSettingsLoader;
import app.text.AppMessages;
import javafx.geometry.Pos;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

/**
 * Builds the application footer: version label + GitHub link.
*/
public final class FooterBuilder {

    private static final Logger logger = LogManager.getLogger(FooterBuilder.class);
    private static final AppSettings appSettings = AppSettingsLoader.load();

    private Label footerVersion;
    private Hyperlink footerGithubLink;

    public HBox build() {
        footerVersion = new Label(AppMessages.footerVersionLabel() + " " + appSettings.version());
        footerVersion.getStyleClass().add("footer_text");

        Label separator = new Label("|");
        separator.getStyleClass().add("footer_text");

        footerGithubLink = new Hyperlink(AppMessages.footerGithubLabel());
        footerGithubLink.getStyleClass().add("footer_link");
        footerGithubLink.setFocusTraversable(false);
        footerGithubLink.setOnAction(e -> openGithubRepository());

        HBox footer = new HBox(8, footerVersion, separator, footerGithubLink);
        footer.setAlignment(Pos.CENTER);
        footer.getStyleClass().add("app_footer");
        return footer;
    }

    public void updateTexts() {
        if (footerVersion != null)
            footerVersion.setText(AppMessages.footerVersionLabel() + " " + appSettings.version());
        
        if (footerGithubLink != null)
            footerGithubLink.setText(AppMessages.footerGithubLabel());
    }

    private void openGithubRepository() {
        try {
            if (Desktop.isDesktopSupported())
                Desktop.getDesktop().browse(URI.create(appSettings.githubRepositoryUrl()));
        } catch (IOException | SecurityException e) {
            logger.warn("Unable to open GitHub repository URL", e);
        }
    }
}