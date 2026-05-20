package app.gui;

import java.io.InputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import app.config.AppAssets;
import app.config.AppSettings;
import app.config.AppSettingsLoader;
import app.exception.StartApplicationException;
import app.gui.builder.MainSceneBuilder;
import app.text.AppMessages;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;


/**
 * Primary class for the application <b>GUI</b>
 * @author ValerioGc
 * @version 1.0.0
 * @since 1.0.0
*/
public class EncryptPasswordGUI extends Application {

    private static final Logger logger = LogManager.getLogger(EncryptPasswordGUI.class);
    private static final AppSettings appSettings = AppSettingsLoader.load();

    @Override
    public void start(Stage primaryStage) throws StartApplicationException {
        primaryStage.initStyle(StageStyle.TRANSPARENT);

        Scene mainPage = MainSceneBuilder.createScene(primaryStage);

        try (InputStream iconStream = getClass().getResourceAsStream(AppAssets.APP_ICON)) {
            if (iconStream != null) {
                Image appIcon = new Image(iconStream);
                primaryStage.getIcons().add(appIcon);
            } else {
                logger.error(AppMessages::errorLogo);
            }
        } catch (java.io.IOException e) {
            logger.error("Unable to close application icon resource", e);
        }

        primaryStage.setScene(mainPage);
        primaryStage.setTitle(appSettings.title());
        primaryStage.setResizable(appSettings.resizable());
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}