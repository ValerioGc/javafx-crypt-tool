package app.config;

/**
 * Application settings loaded from resources.
*/
public record AppSettings(
    String title,
    String version,
    String githubRepositoryUrl,
    int windowWidth,
    int windowHeight,
    boolean resizable,
    int viewPadding
) {
}