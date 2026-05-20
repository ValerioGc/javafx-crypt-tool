@echo off

if not defined SONAR_TOKEN (
    echo "SONAR_TOKEN environment variable is not set. Please enter it:"
    set /p SONAR_TOKEN=SONAR_TOKEN:
    if not defined SONAR_TOKEN (
        echo "SONAR_TOKEN not provided. It is required to run the sonar analysis. Exiting."
        exit /b
    )
)

mvn clean verify sonar:sonar -Psonar
