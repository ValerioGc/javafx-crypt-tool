package app.gui;

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

        java.net.URL iconUrl = getClass().getResource(AppAssets.APP_ICON);
        if (iconUrl != null) {
            String iconUrlStr = iconUrl.toExternalForm();
            for (int size : new int[]{16, 32, 48, 64, 128, 256}) {
                primaryStage.getIcons().add(new Image(iconUrlStr, size, size, true, true));
            }
        } else {
            logger.error(AppMessages::errorLogo);
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