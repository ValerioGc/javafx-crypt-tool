package app.text;

import java.io.Serializable;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * I18n facade for application messages.
*/
public class AppMessages implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final String BUNDLE_BASE_NAME = "i18n.messages";

    private static Locale currentLocale = Locale.ENGLISH;
    private static ResourceBundle bundle = ResourceBundle.getBundle(BUNDLE_BASE_NAME, currentLocale);

    private AppMessages() {
    }

    public static void setLocale(Locale locale) {
        currentLocale = locale;
        bundle = ResourceBundle.getBundle(BUNDLE_BASE_NAME, currentLocale);
    }

    public static Locale getCurrentLocale() {
        return currentLocale;
    }

    public static String get(String key) {
        return bundle.getString(key);
    }

    public static String algorithmLabel() { return bundle.getString("algorithm.label"); }
    public static String algorithmPlaceholder() { return bundle.getString("algorithm.placeholder"); }
    public static String pageDescription() { return bundle.getString("page.description"); }
    public static String saltingLabel() { return bundle.getString("salting.label"); }
    public static String saltingPlaceholder() { return bundle.getString("salting.placeholder"); }
    public static String placeholderPassword() { return bundle.getString("password.placeholder"); }
    public static String labelPassword() { return bundle.getString("password.label"); }
    public static String radioEncrypt() { return bundle.getString("radio.encrypt"); }
    public static String radioDecrypt() { return bundle.getString("radio.decrypt"); }
    public static String modeLabel() { return bundle.getString("mode.label"); }
    public static String buttonRunLabel() { return bundle.getString("button.run.label"); }
    public static String buttonRunTooltip() { return bundle.getString("button.run.tooltip"); }
    public static String buttonCopyTooltip() { return bundle.getString("button.copy.tooltip"); }
    public static String windowMinimizeTooltip() { return bundle.getString("window.minimize.tooltip"); }
    public static String windowCloseTooltip() { return bundle.getString("window.close.tooltip"); }
    public static String footerVersionLabel() { return bundle.getString("footer.version"); }
    public static String footerGithubLabel() { return bundle.getString("footer.github"); }
    public static String languageTooltip() { return bundle.getString("language.tooltip"); }
    public static String themeTooltip() { return bundle.getString("theme.tooltip"); }
    public static String errorLogo() { return bundle.getString("error.logo"); }
    public static String errorDecrypt() { return bundle.getString("error.decrypt"); }
    public static String errorRequiredFields() { return bundle.getString("error.requiredFields"); }
    public static String errorOperationFailed() { return bundle.getString("error.operationFailed"); }
    public static String styleNotFound() { return bundle.getString("error.styleNotFound"); }
    public static String passwordToggleTooltip() { return bundle.getString("password.toggle.tooltip"); }
    public static String jasyptModeLabel() { return bundle.getString("jasypt.mode.label"); }
    public static String jasyptIterationsLabel() { return bundle.getString("jasypt.iterations.label"); }
    public static String passwordStrengthWeak() { return bundle.getString("password.strength.weak"); }
    public static String passwordStrengthFair() { return bundle.getString("password.strength.fair"); }
    public static String passwordStrengthStrong() { return bundle.getString("password.strength.strong"); }
    public static String passwordStrengthVeryStrong() { return bundle.getString("password.strength.veryStrong"); }
}
