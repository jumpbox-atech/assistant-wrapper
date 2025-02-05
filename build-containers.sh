#! /bin/bash
ROOT_DIR=$PWD
APP_NAME=assistant
APP_VER=2025.01
DB_NAME=${APP_NAME}-postgres
DB_VERSION=2025.01
BUILD_DB=true

# shellcheck disable=SC2164
cd "${ROOT_DIR}"

function package(){
  cd "${ROOT_DIR}" && mvn clean package
  cp "${ROOT_DIR}"/target/*.jar "${ROOT_DIR}"/docker/build/spring/app/app.jar
}
function buildDb(){
  if [ "$BUILD_DB" == 'true' ]; then
    mkdir -p "${ROOT_DIR}"/docker/build/postgres/config
    cp "${ROOT_DIR}"/src/main/resources/schema.sql "${ROOT_DIR}"/docker/build/postgres/config/
    # shellcheck disable=SC2164
    cd "${ROOT_DIR}"/docker/build/postgres
    docker build -t ${DB_NAME}:${DB_VERSION} .
    docker tag ${DB_NAME}:${DB_VERSION} ${DB_NAME}:latest
  fi
}
function buildApp() {
  # shellcheck disable=SC2164
  cd "${ROOT_DIR}"/docker/build/spring
  docker build -t ${APP_NAME}:${APP_VER} .
  docker tag ${APP_NAME}:${APP_VER} ${APP_NAME}:latest
}

buildDb
package
buildApp
