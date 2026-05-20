# Development Guide

Everything needed to build, test, and release EncSeal from source.

---

## Tech Stack

| Component | Technology | Version |
|---|---|---|
| Language | Java | 21 |
| UI | JavaFX | 21.0.2 |
| Crypto | Bouncy Castle | 1.84 |
| Logging | Log4j 2 | 2.24.3 |
| Build | Maven | 3.8+ |
| Unit tests | JUnit 5 | 5.9.3 |
| UI tests | TestFX | 4.0.18 |
| Static analysis | SonarQube | local |

---

## Prerequisites

- **[Liberica Full JDK 21](https://bell-sw.com/pages/downloads/#jdk-21-lts)** — standard JDK distributions (OpenJDK, Temurin) do not include JavaFX modules required by jlink and TestFX
- **Maven 3.8+**
- **Git**

Set `JAVA_HOME` to the Liberica Full JDK installation before building or running package scripts.

---

## Build

```bash
git clone https://github.com/ValerioGc/java-crypt-tool.git
cd java-crypt-tool
mvn clean package
java -jar target/EncSeal-1.0.0.jar
```

---

## Tests

```bash
mvn clean test
```

- **Business logic** — `app.business`, `app.config.security`: pure JUnit 5
- **UI** — `app.gui.*`: TestFX, requires JavaFX toolkit

When running with Liberica Full JDK, Surefire automatically passes the required `--add-opens` flags so TestFX can access JavaFX internals via reflection.

---

## Project Structure

```
src/
  main/java/app/
    business/              # Encrypt/decrypt logic interface + impl
    config/security/       # CryptoConfig, AlgorithmRegistry (EncSeal + Jasypt maps), PBEEncryptor
    exception/             # Custom exceptions
    gui/builder/           # Scene, Header, Footer builders
    gui/builder/component/ # Input, Button, Radio builders
    text/                  # i18n facade (AppMessages)
    util/                  # AppUtils
  main/resources/
    i18n/                  # Translations (EN, IT, FR, ES, DE)
    icons/                 # PNG icons and flags
    style/                 # CSS (buttons, text, select)
    application.properties
  test/java/app/
    business/
    config/security/
    gui/builder/
    gui/builder/component/
    testutil/              # FxTestSupport — JavaFX bootstrap helper for tests
```

---

## SonarQube

Requires a local SonarQube server on `http://localhost:9000`.

```bash
# Pass token via env — do not commit tokens
$env:SONAR_TOKEN = "your_token"
mvn clean verify sonar:sonar -Psonar
```

JaCoCo report: `target/site/jacoco/index.html`

---

## Portable Builds

Package scripts use `%JAVA_HOME%\bin\jlink` and `%JAVA_HOME%\bin\jpackage` directly — `JAVA_HOME` must point to Liberica Full JDK 21.

**Windows** (run on Windows):
```cmd
scripts\package-windows.cmd --skip-tests
```
Output: `target/dist/javafx-crypt-tool-windows/javafx-crypt-tool.exe`

**Linux** (run on Linux or via WSL):
```bash
sh scripts/package-linux.sh --skip-tests
```
Output: `target/dist/javafx-crypt-tool-linux/bin/javafx-crypt-tool`

> jpackage does not support cross-compilation. Use the GitHub Actions release pipeline to build Linux packages from Windows.

---

## CI / CD

Two GitHub Actions workflows are defined in `.github/workflows/`:

| Workflow | Trigger | What it does |
|---|---|---|
| `ci.yml` | push / PR to `main` | Runs tests on Windows and Linux (matrix), uploads JaCoCo report |
| `release.yml` | push tag `v*.*.*` | Builds both platform packages in parallel, publishes GitHub Release |

Both use `distribution: liberica` with `java-package: jdk+fx` in `actions/setup-java`.

### Releasing

```bash
git tag v1.0.0
git push origin v1.0.0
```

The release pipeline builds Windows on `windows-latest` and Linux on `ubuntu-latest` in parallel, then creates a GitHub Release with both artifacts. Release notes are extracted automatically from the matching version block in `CHANGELOG.md`.

---

## Contributing

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/your-feature`
3. Run tests: `mvn clean test`
4. Open a pull request against `main`
