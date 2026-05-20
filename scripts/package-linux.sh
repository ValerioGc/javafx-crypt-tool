#!/usr/bin/env sh
set -eu

APP_NAME="javafx-crypt-tool"
ROOT="$(CDPATH= cd -- "$(dirname -- "$0")/.." && pwd)"
TARGET="$ROOT/target"
DIST="$TARGET/dist"
RUNTIME_IMAGE="$TARGET/runtime-image-linux"
STAGE="$TARGET/stage"
PACKAGE_DIR="$DIST/$APP_NAME-linux"

BASE_MODULES="java.base,java.desktop,java.logging,java.prefs,java.xml,java.naming,jdk.crypto.ec,jdk.localedata"
JAVAFX_MODULES="javafx.base,javafx.controls,javafx.graphics"

cd "$ROOT"

echo "[1/5] Building Maven package..."
if [ "${1:-}" = "--skip-tests" ]; then
  mvn clean package -DskipTests -q
else
  mvn clean package -q
fi

VERSION="$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout)"
JAR_NAME="$APP_NAME-$VERSION.jar"
JAR_PATH="$TARGET/$JAR_NAME"

if [ ! -f "$JAR_PATH" ]; then
  echo "[ERROR] JAR not found: $JAR_PATH" >&2
  exit 1
fi

echo "[2/5] Creating runtime image via jlink..."
# Detect whether JavaFX modules are available in this JDK
if jlink --list-modules 2>/dev/null | grep -q "javafx.controls"; then
  MODULES="$BASE_MODULES,$JAVAFX_MODULES"
  echo "JavaFX modules found in JDK - including them in runtime image."
else
  MODULES="$BASE_MODULES"
  echo "WARNING: JavaFX modules not found in JDK. Using standard modules only."
  echo "The app will use JavaFX from the fat JAR (classpath mode)."
  echo "For a self-contained runtime with JavaFX, use Liberica Full JDK 21."
fi

rm -rf "$RUNTIME_IMAGE"
jlink \
  --add-modules "$MODULES" \
  --output "$RUNTIME_IMAGE" \
  --strip-debug \
  --no-header-files \
  --no-man-pages \
  --compress=zip-6 || {
    echo "[ERROR] jlink failed." >&2
    exit 1
  }

echo "[3/5] Creating app image via jpackage..."
rm -rf "$STAGE" "$PACKAGE_DIR"
mkdir -p "$STAGE" "$DIST"
cp "$JAR_PATH" "$STAGE/$JAR_NAME"

jpackage \
  --name "$APP_NAME" \
  --input "$STAGE" \
  --main-jar "$JAR_NAME" \
  --type app-image \
  --runtime-image "$RUNTIME_IMAGE" \
  --dest "$DIST"

# jpackage outputs DIST/APP_NAME — rename to APP_NAME-linux
if [ -d "$DIST/$APP_NAME" ] && [ ! -d "$PACKAGE_DIR" ]; then
  mv "$DIST/$APP_NAME" "$PACKAGE_DIR"
fi

if [ ! -d "$PACKAGE_DIR" ]; then
  echo "[ERROR] Package directory not found after jpackage." >&2
  exit 1
fi

echo "[4/4] Done."
echo "Package: $PACKAGE_DIR"
