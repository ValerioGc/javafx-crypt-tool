#!/bin/bash

if [ -z "$SONAR_TOKEN" ]; then
    echo "SONAR_TOKEN environment variable is not set. Please enter it:"
    read -r -p "SONAR_TOKEN: " SONAR_TOKEN
    if [ -z "$SONAR_TOKEN" ]; then
        echo "SONAR_TOKEN not provided. It is required to run the sonar analysis. Exiting."
        exit 1
    fi
    export SONAR_TOKEN
fi

mvn clean verify sonar:sonar -Psonar
