#! /bin/bash

JAVA_START="java -jar app.jar"
FINAL_JAVA_START=''
FINAL_JAVA_START_ECHO=''
ENV_MAPPING_FILE="/home/dummy/config/env_mapping.csv"

# Function to validate each mandatory environment variable
validateEnv(){
  local varName=$1
  if [ -z "${!varName}" ]; then
    echo "Error: ${varName} is not set. Please set it in the run command or compose file."
    exit 1
  fi
}

# Function to append validated environment variables as arguments
appendArgument(){
  local argName=$1
  local varName=$2
  local value="${!varName}"  # Get the value of the environment property

  FINAL_JAVA_START+=" ${argName}=\"${value}\""
  FINAL_JAVA_START_ECHO+=" \\ \n${argName}=\"${value}\""
}

# Function to wait for database to become ready
waitForDb(){
  echo "Waiting for database to be ready."
  sleep 5
}

# Function to start the Spring application using FINAL_JAVA_START
startSpringApp(){
  echo "All checks completed."
  echo -e "Starting spring app with the following properties:
${JAVA_START}${FINAL_JAVA_START_ECHO}
"
  eval "${JAVA_START}${FINAL_JAVA_START}"
}

function run(){
  # Read the CSV file and process each line
  echo "Validating required environment variables."
  while IFS=',' read -r varName argName; do
    if [[ "$varName" != "EnvironmentVariable" ]]; then
      validateEnv "${varName}"
      appendArgument "$argName" "$varName"
    fi
  done < "$ENV_MAPPING_FILE"
  echo "Environment variables have passed validation."

  waitForDb
  startSpringApp
}

run
