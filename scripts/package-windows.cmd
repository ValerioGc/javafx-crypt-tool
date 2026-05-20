@echo off
setlocal EnableExtensions EnableDelayedExpansion

set "APP_NAME=javafx-crypt-tool"
set "ROOT=%~dp0"
for %%I in ("%ROOT%..") do set "ROOT=%%~fI"

set "TARGET=%ROOT%\target"
set "DIST=%TARGET%\dist"
set "RUNTIME_IMAGE=%TARGET%\runtime-image-win"
set "STAGE=%TARGET%\stage"
set "PACKAGE_DIR=%DIST%\%APP_NAME%-windows"
set "SKIP_TESTS="

set "BASE_MODULES=java.base,java.desktop,java.logging,java.prefs,java.xml,java.naming,jdk.crypto.ec,jdk.localedata"
set "JAVAFX_MODULES=javafx.base,javafx.controls,javafx.graphics"

if /I "%~1"=="--skip-tests" set "SKIP_TESTS=-DskipTests"
if /I "%~1"=="/skip-tests"  set "SKIP_TESTS=-DskipTests"

if not defined JAVA_HOME (echo [ERROR] JAVA_HOME is not set. & exit /b 1)
if not exist "%JAVA_HOME%\bin\jlink.exe"    (echo [ERROR] jlink not found in JAVA_HOME=%JAVA_HOME% & exit /b 1)
if not exist "%JAVA_HOME%\bin\jpackage.exe" (echo [ERROR] jpackage not found in JAVA_HOME=%JAVA_HOME% & exit /b 1)

set "JLINK=%JAVA_HOME%\bin\jlink.exe"
set "JPACKAGE=%JAVA_HOME%\bin\jpackage.exe"

where mvn >nul 2>nul || (echo [ERROR] mvn not found in PATH. & exit /b 1)

pushd "%ROOT%" || exit /b 1

echo [1/4] Building Maven package...
call mvn clean package %SKIP_TESTS% -q
if errorlevel 1 goto fail_popd

set "VERSION="
for /f "delims=" %%V in ('call mvn help:evaluate -Dexpression^=project.version -q -DforceStdout 2^>nul') do (
    if not defined VERSION set "VERSION=%%V"
)
if not defined VERSION (echo [ERROR] Cannot read version from pom.xml. & goto fail_popd)
echo       Version: %VERSION%   JDK: %JAVA_HOME%

set "JAR_NAME=%APP_NAME%-%VERSION%.jar"
set "JAR_PATH=%TARGET%\%JAR_NAME%"
if not exist "%JAR_PATH%" (echo [ERROR] JAR not found: %JAR_PATH% & goto fail_popd)

echo [2/4] Creating runtime image via jlink...
set "HAS_JAVAFX="
if exist "%JAVA_HOME%\jmods\javafx.controls.jmod" set "HAS_JAVAFX=1"

if defined HAS_JAVAFX (
    set "MODULES=%BASE_MODULES%,%JAVAFX_MODULES%"
    echo JavaFX modules found - runtime will be self-contained.
) else (
    set "MODULES=%BASE_MODULES%"
    echo WARNING: No javafx.* modules in %JAVA_HOME%. Runtime will use classpath JavaFX.
)

if exist "%RUNTIME_IMAGE%" rmdir /s /q "%RUNTIME_IMAGE%"
"%JLINK%" ^
  --add-modules "%MODULES%" ^
  --output "%RUNTIME_IMAGE%" ^
  --strip-debug ^
  --no-header-files ^
  --no-man-pages ^
  --compress=zip-6
if errorlevel 1 goto fail_popd

echo [3/4] Creating app image via jpackage...
if exist "%STAGE%" rmdir /s /q "%STAGE%"
if exist "%PACKAGE_DIR%" rmdir /s /q "%PACKAGE_DIR%"
mkdir "%STAGE%"
mkdir "%DIST%" 2>nul
copy /y "%JAR_PATH%" "%STAGE%\%JAR_NAME%" >nul

set "ICON_PATH=%ROOT%\src\main\resources\icons\logo.ico"
set "ICON_ARG="
if exist "%ICON_PATH%" (
    set "ICON_ARG=--icon "%ICON_PATH%""
    echo       Icon: %ICON_PATH%
) else (
    echo WARNING: logo.ico not found at %ICON_PATH% - launcher will use default icon.
    echo Convert logo.png to logo.ico and place it alongside logo.png.
)

"%JPACKAGE%" ^
  --name "%APP_NAME%" ^
  --input "%STAGE%" ^
  --main-jar "%JAR_NAME%" ^
  --type app-image ^
  --runtime-image "%RUNTIME_IMAGE%" ^
  --dest "%DIST%" ^
  %ICON_ARG%
if errorlevel 1 goto fail_popd

if exist "%DIST%\%APP_NAME%" (
    if exist "%PACKAGE_DIR%" rmdir /s /q "%PACKAGE_DIR%"
    move "%DIST%\%APP_NAME%" "%PACKAGE_DIR%" >nul
)
if not exist "%PACKAGE_DIR%" (echo [ERROR] Package directory not found after jpackage. & goto fail_popd)

echo [4/4] Done.
echo Package: %PACKAGE_DIR%
popd
exit /b 0

:fail_popd
popd
echo Packaging failed.
exit /b 1