package app.gui.builder;

import java.util.Locale;

import app.config.AppAssets;
import app.config.AppSettings;
import app.config.AppSettingsLoader;
import app.text.AppMessages;
import app.util.AppUtils;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Builds the application header: window controls bar + logo/title/actions row.
 */
public final class HeaderBuilder {

    private static final int FLAG_WIDTH = 22;
    private static final int FLAG_HEIGHT = 16;
    private static final int THEME_ICON_SIZE = 16;
    private static final String DARK_THEME = "theme_dark";
    private static final String LIGHT_THEME = "theme_light";

    private static final AppUtils appUtils = new AppUtils();
    private static final AppSettings appSettings = AppSettingsLoader.load();

    private MenuButton languageSelector;
    private Button themeSelector;
    private Button minimizeButton;
    private Button closeButton;
    private Label pageDescription;
    private ImageView brandLogo;
    private ImageView themeIcon;

    /**
     * Builds the full header VBox (window titlebar + content row).
     *
     * @param root the app root VBox (needed for theme toggling and icon lookups)
     * @param stage  the primary stage (needed for drag-to-move and window controls)
     * @param onLanguageChange callback invoked after a language change so callers can refresh all texts
    */
    public VBox build(VBox root, Stage stage, Runnable onLanguageChange) {
        VBox header = new VBox(14, buildTopBar(stage), buildContentRow(root, onLanguageChange));
        header.setMaxWidth(Double.MAX_VALUE);
        header.getStyleClass().add("page_header");
        return header;
    }

    public void updateTexts() {
        if (languageSelector != null && languageSelector.getTooltip() != null)
            languageSelector.getTooltip().setText(AppMessages.languageTooltip());
        if (themeSelector != null && themeSelector.getTooltip() != null)
            themeSelector.getTooltip().setText(AppMessages.themeTooltip());
        if (minimizeButton != null && minimizeButton.getTooltip() != null)
            minimizeButton.getTooltip().setText(AppMessages.windowMinimizeTooltip());
        if (closeButton != null && closeButton.getTooltip() != null)
            closeButton.getTooltip().setText(AppMessages.windowCloseTooltip());
        if (pageDescription != null)
            pageDescription.setText(AppMessages.pageDescription());
    }

    // ===== TOP BAR (drag handle + window controls) =====

    private HBox buildTopBar(Stage stage) {
        Region dragHandle = new Region();
        dragHandle.setCursor(Cursor.MOVE);
        HBox.setHgrow(dragHandle, Priority.ALWAYS);

        final double[] dragDelta = {0, 0};
        dragHandle.setOnMousePressed(e -> {
            dragDelta[0] = stage.getX() - e.getScreenX();
            dragDelta[1] = stage.getY() - e.getScreenY();
        });
        dragHandle.setOnMouseDragged(e -> {
            stage.setX(e.getScreenX() + dragDelta[0]);
            stage.setY(e.getScreenY() + dragDelta[1]);
        });

        minimizeButton = new Button("-");
        minimizeButton.getStyleClass().add("window_btn");
        minimizeButton.setTooltip(new Tooltip(AppMessages.windowMinimizeTooltip()));
        minimizeButton.setOnAction(e -> stage.setIconified(true));

        closeButton = new Button("X");
        closeButton.getStyleClass().addAll("window_btn", "window_btn_close");
        closeButton.setTooltip(new Tooltip(AppMessages.windowCloseTooltip()));
        closeButton.setOnAction(e -> Platform.exit());

        HBox topBar = new HBox(4, dragHandle, minimizeButton, closeButton);
        topBar.setAlignment(Pos.CENTER_RIGHT);
        topBar.setPadding(new Insets(12, 12, 4, 12));
        topBar.setMaxWidth(Double.MAX_VALUE);
        topBar.getStyleClass().add("window_titlebar");
        return topBar;
    }

    // ===== CONTENT ROW (logo + title + desc  |  lang + theme) =====

    private HBox buildContentRow(VBox root, Runnable onLanguageChange) {
        brandLogo = appUtils.loadImage(AppAssets.APP_ICON, 50, 50);
        brandLogo.getStyleClass().add("brand_logo");
        applyBrandLogoTheme(false);

        Label headerTitle = new Label(appSettings.title());
        headerTitle.getStyleClass().add("header_title");

        pageDescription = new Label(AppMessages.pageDescription());
        pageDescription.getStyleClass().add("header_description");
        pageDescription.setWrapText(true);

        VBox titleGroup = new VBox(3, headerTitle, pageDescription);
        titleGroup.setAlignment(Pos.CENTER_LEFT);

        HBox left = new HBox(14, brandLogo, titleGroup);
        left.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(left, Priority.ALWAYS);

        languageSelector = buildLanguageSelector(onLanguageChange);
        themeSelector = buildThemeSelector(root);

        HBox right = new HBox(10, languageSelector, themeSelector);
        right.setAlignment(Pos.CENTER_RIGHT);

        HBox contentRow = new HBox(left, right);
        contentRow.setAlignment(Pos.CENTER);
        contentRow.setMaxWidth(Double.MAX_VALUE);
        contentRow.setPadding(new Insets(0, 20, 18, 24));
        return contentRow;
    }

    // ===== Language selector =====

    private MenuButton buildLanguageSelector(Runnable onLanguageChange) {
        MenuButton selector = new MenuButton();
        selector.setGraphic(createFlagIcon(AppAssets.ENGLISH_FLAG));
        selector.getStyleClass().add("selector_button");
        selector.setCursor(Cursor.HAND);
        selector.setTooltip(new Tooltip(AppMessages.languageTooltip()));

        selector.getItems().addAll(
            createLanguageItem("English",  AppAssets.ENGLISH_FLAG,  Locale.ENGLISH,              selector, onLanguageChange),
            createLanguageItem("Italiano", AppAssets.ITALIAN_FLAG,  Locale.ITALIAN,              selector, onLanguageChange),
            createLanguageItem("Français", AppAssets.FRENCH_FLAG,   Locale.FRENCH,               selector, onLanguageChange),
            createLanguageItem("Español",  AppAssets.SPANISH_FLAG,  Locale.forLanguageTag("es"), selector, onLanguageChange),
            createLanguageItem("Deutsch",  AppAssets.GERMAN_FLAG,   Locale.GERMAN,               selector, onLanguageChange)
        );
        return selector;
    }

    private MenuItem createLanguageItem(String label, String flagPath, Locale locale,
                                                MenuButton selector, Runnable onLanguageChange) {
        MenuItem item = new MenuItem(label);
        item.setGraphic(createFlagIcon(flagPath));
        item.setOnAction(e -> {
            AppMessages.setLocale(locale);
            selector.setGraphic(createFlagIcon(flagPath));
            onLanguageChange.run();
        });
        return item;
    }

    private ImageView createFlagIcon(String flagPath) {
        ImageView flag = appUtils.loadImage(flagPath, FLAG_WIDTH, FLAG_HEIGHT);
        flag.getStyleClass().add("flag_icon");
        return flag;
    }

    // ===== Theme selector =====

    private Button buildThemeSelector(VBox root) {
        themeIcon = createThemeIcon(AppAssets.THEME_DARK_ICON);
        appUtils.applyInvertEffect(themeIcon, true);
        Button selector = new Button();
        selector.setGraphic(themeIcon);
        selector.getStyleClass().add("icon_button");
        selector.setCursor(Cursor.HAND);
        selector.setTooltip(new Tooltip(AppMessages.themeTooltip()));

        selector.setOnAction(e -> {
            boolean darkMode = root.getStyleClass().contains(DARK_THEME);
            if (darkMode) {
                root.getStyleClass().remove(DARK_THEME);
                if (!root.getStyleClass().contains(LIGHT_THEME))
                    root.getStyleClass().add(LIGHT_THEME);
                
                themeIcon = createThemeIcon(AppAssets.THEME_DARK_ICON);
                selector.setGraphic(themeIcon);
                applyThemeColors(root, false);
            } else {
                root.getStyleClass().remove(LIGHT_THEME);
                if (!root.getStyleClass().contains(DARK_THEME))
                    root.getStyleClass().add(DARK_THEME);
                
                themeIcon = createThemeIcon(AppAssets.THEME_LIGHT_ICON);
                selector.setGraphic(themeIcon);
                applyThemeColors(root, true);
            }
        });
        return selector;
    }

    private void applyThemeColors(VBox root, boolean darkMode) {
        if (brandLogo != null)
            applyBrandLogoTheme(darkMode);

        if (themeIcon != null)
            appUtils.applyInvertEffect(themeIcon, true);

        for (Node icon : root.lookupAll(".form_icon"))
            appUtils.applyInvertEffect(icon, darkMode);

        for (Node icon : root.lookupAll(".icon_invert"))
            appUtils.applyInvertEffect(icon, darkMode);
    }

    private void applyBrandLogoTheme(boolean darkMode) {
        appUtils.applyInvertEffect(brandLogo, darkMode);
    }

    // ===== Theme icons =====

    private ImageView createThemeIcon(String resourcePath) {
        return appUtils.loadImage(resourcePath, THEME_ICON_SIZE, THEME_ICON_SIZE);
    }
}