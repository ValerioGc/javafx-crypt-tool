package app.config;

import java.util.ResourceBundle;

/**
 * Loads application settings from application.properties.
*/
public final class AppSettingsLoader {

    private static final String BUNDLE_BASE_NAME = "application";
    private static final AppSettings SETTINGS = loadFromBundle();

    private AppSettingsLoader() {
    }

    public static AppSettings load() {
        return SETTINGS;
    }

    private static AppSettings loadFromBundle() {
        ResourceBundle bundle = ResourceBundle.getBundle(BUNDLE_BASE_NAME);

        return new AppSettings(
            bundle.getString("app.title"),
            resolveVersion(bundle.getString("app.version")),
            bundle.getString("app.githubRepositoryUrl"),
            Integer.parseInt(bundle.getString("window.width")),
            Integer.parseInt(bundle.getString("window.height")),
            Boolean.parseBoolean(bundle.getString("window.resizable")),
            Integer.parseInt(bundle.getString("view.padding"))
        );
    }

    private static String resolveVersion(String configuredVersion) {
        if (!configuredVersion.startsWith("${"))
            return configuredVersion;

        String implementationVersion = AppSettingsLoader.class.getPackage().getImplementationVersion();
        return implementationVersion != null ? implementationVersion : "dev";
    }
}