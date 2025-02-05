# Assistant Wrapper

### Description

This application is an interface to integrate with OpenAi's [Assistant](https://platform.openai.com/docs/assistants/overview) platform and removes the need of sharing Assistants on the OpenAi platform.

### Pre-requisites

In order for this system to run, you are required to have:

1. A computer with [docker](https://docs.docker.com/engine/install/) and [docker compose](https://docs.docker.com/compose/install/) installed
2. An smtp server or connection details to one
3. A working Assistant on the OpenAi platform
4. An OpenAi [api key](https://platform.openai.com/docs/quickstart)

#### Building Locally

The project includes docker build files. To build the database and application containers, you can use the script named: [build-containers.sh](build-containers.sh).

#### Customization

The project allows for customization of the **application name** and **logo**.

To change the application name, set the environment variable named ```ATECH_APP_NAME``` to the required name. To change the application logo, replace the file named: [default.png](src/main/resources/static/images/default.png). The logo image is required to be at least ```512px``` x ```512px``` in size.

### Usage

The project includes a docker compose and environment properties file. Please refer to the directory [compose](docker/compose). Once running, Assistants are captured using ```Settings``` -> ```Assistants``` -> ```Add```. Here you will be required to capture the following info:

| **Field**                           | **Description**                                                                  |
|-------------------------------------|----------------------------------------------------------------------------------|
| _name_                              | The name of the assistant that will be displayed to users                        |
| _description_                       | A description for the assistant                                                  |
| _openai organisation id_            | The OpenAi organisation id                                                       |
| _openai assistant id_               | The OpenAi assistant id                                                          |
| _openai assistant api key_          | The OpenAi assistant api key                                                     |
| _assistant additional instructions_ | (OPTIONAL) Additional instructions that will be pre-pended to any question asked |

### Security

The application is configured to use 2 types of registrations using the environment variable ```ATECH_APP_REGISTER_TYPE```. These include:

1. ```public``` which allows for any person to register for the application. Once registered, and ADMIN or MANAGER is required to assign an Assistant to the user.
2. ```private``` which allows registration of usernames that are **whitelisted before registration**. In order to capture a whitelisted user, an Assistant needs to exist in the system.

Once the user has registered, an email will be sent to the user with a temp password. If the registration type is set to ```private```, the database entry under the Whitelist table will be deleted. This table only displays whitelisted users which have not yet registered.

***WARNING*** : Do not put this on the Internet if you do not know what you are doing.

#### Environment Variables

| **Variable**                                                  | **Description**                                                                                                                                                                                                             |
|---------------------------------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| _POSTGRES_HOST_                                               | The ```hostname``` for the database                                                                                                                                                                                         |
| _POSTGRES_PORT_                                               | The ```username``` for the database connection                                                                                                                                                                              |
| _POSTGRES_PASSWORD_                                           | The ```password``` for the database connection                                                                                                                                                                              |
| _ATECH_LOG_LEVEL_                                             | Sets the springboot log level for he application                                                                                                                                                                            |
| _ATECH_LOG_PRINT_PROPERTIES_                                  | A ```boolean``` value to print out all of the environment variabled on application startup                                                                                                                                  |
| _ATECH_JSON_SERIALISE_DATE_FORMAT_                            | Sets the date format for the jackson library, example: ```yyyy-MM-dd HH:mm:ss```                                                                                                                                            |
| _ATECH_SPRING_DATASOURCE_HOST_                                | The database ```hostname```                                                                                                                                                                                                 |
| _ATECH_SPRING_DATASOURCE_UNAME_                               | The database ```username```                                                                                                                                                                                                 |
| _ATECH_SPRING_DATASOURCE_PWD_                                 | The database ```password```                                                                                                                                                                                                 |
| _ATECH_APP_NAME_                                              | The application name                                                                                                                                                                                                        |
| _ATECH_APP_REGISTER_TYPE_                                     | Used to set the registration type (refer to the section: Security)                                                                                                                                                          |
| _ATECH_APP_URL_BASE_                                          | Used resolve links in emails to the hosted server                                                                                                                                                                           |
| _ATECH_APP_EMAIL_SMTP_HOST_                                   | The ```hostname``` required for connecting to the smtp server                                                                                                                                                               |
| _ATECH_APP_EMAIL_SMTP_PORT_                                   | The ```port``` required for connecting to the smtp server                                                                                                                                                                   |
| _ATECH_APP_EMAIL_SMTP_UNAME_                                  | The ```username``` required for connecting to the smtp server                                                                                                                                                               |
| _ATECH_APP_EMAIL_SMTP_PWD_                                    | The smtp ```password``` required for connecting to the smtp server                                                                                                                                                          |
| _ATECH_APP_EMAIL_SMTP_TEST_CONNECTION_                        | A ```boolean``` value used to test the connection on application startup                                                                                                                                                    |
| _ATECH_APP_EMAIL_SMTP_STARTTLS_ENABLE_                        | A ```boolean``` value used to set the encryption level                                                                                                                                                                      |
| _ATECH_APP_EMAIL_FROM_EMAIL_ADDRESS_                          | The email address of the account used to send automated email addresses. This property together with the ```ATECH_APP_NAME``` will set the email display name to  ```ATECH_APP_NAME<ATECH_APP_EMAIL_FROM_EMAIL_ADDRESS> ``` |
| _ATECH_APP_CONFIG_USER_ADMIN_CREATE_                          | A ```boolean``` value to create the initial admin account on application startup. This is required for initial startup                                                                                                      |
| _ATECH_APP_CONFIG_USER_ADMIN_USERNAME_                        | The ```username``` for the initial admin account                                                                                                                                                                            |
| _ATECH_APP_CONFIG_USER_ADMIN_NAME_                            | The ```name``` for the initial admin account                                                                                                                                                                                |
| _ATECH_APP_CONFIG_USER_ADMIN_SURNAME_                         | The ```surname``` for the initial admin account                                                                                                                                                                             |
| _ATECH_APP_CONFIG_USER_ADMIN_EMAIL_                           | The ```email address``` for the initial admin account                                                                                                                                                                       |
| _ATECH_APP_CONFIG_USER_ADMIN_PWD_                             | The ```password``` for the initial admin account                                                                                                                                                                            |
| _ATECH_APP_OPENAI_API_BASE_URL_                               | The base host url for the OpenAi api, example: ```https://api.openai.com/v1```                                                                                                                                              |
| _ATECH_APP_OPENAI_API_USER_RELAXED_HTTPS_                     | A ```boolean``` used to enable relaxed https communication with the OpenAi api                                                                                                                                              |
| _ATECH_APP_OPENAI_API_ASSISTANT_BETA_VERSION_                 | This sets the ```OpenAI-Beta``` header in the outgoing api calls. Allowed value: ```2```. More details can be found [HERE](https://platform.openai.com/docs/assistants/quickstart)                                          |
| _ATECH_APP_OPENAI_API_ASSISTANT_RESPONSE_POLL_SLEEP_DURATION_ | Sets the ```sleep duration``` (in milliseconds) when polling for an answer                                                                                                                                                  |
| _ATECH_APP_OPENAI_API_ASSISTANT_RESPONSE_POLL_MAX_RETRY_      | Sets the ```max amount of retries``` to poll for an answer                                                                                                                                                                  |
| _ATECH_APP_OPENAI_API_ASSISTANT_LOGOUT_CREATE_THREAD_         | A ```boolean``` value to logout the external call to the OpenAi api when ```creating a thread```                                                                                                                            |
| _ATECH_APP_OPENAI_API_ASSISTANT_LOGOUT_ADD_MESSAGE_           | A ```boolean``` value to logout the external call to the OpenAi api when ```adding a message to a thread```                                                                                                                 |
| _ATECH_APP_OPENAI_API_ASSISTANT_LOGOUT_RUN_THREAD_            | A ```boolean``` value to logout the external call to the OpenAi api when ```running a thread```                                                                                                                             |
| _ATECH_APP_OPENAI_API_ASSISTANT_LOGOUT_GET_RUN_STATUS_        | A ```boolean``` value to logout the external call to the OpenAi api when ```requesting a status of a thread currently running```                                                                                            |
| _ATECH_APP_OPENAI_API_ASSISTANT_LOGOUT_GET_MESSAGES_          | A ```boolean``` value to logout the external call to the OpenAi api when ```retrieving a message for a given thread ```                                                                                                     |
| _ATECH_APP_OPENAI_API_ASSISTANT_LOGOUT_GET_STEPS_FOR_RUN_     | A ```boolean``` value to logout the external call to the OpenAi api when ```requesting all the steps for a given run```                                                                                                     |

***INFO*** : When using docker, the application will fail to start if any of the above environment variables do not exist or aren't populated.

#### Docker Compose

The below configuration assumes that you have built the containers using the [build-containers.sh](build-containers.sh) script and that the [.env](docker/compose/.env) file is located in the same directory as the [docker-compose.yml](docker/compose/docker-compose.yml) file

```yaml
services:

  assistant-db:
    image: assistant-postgres:latest
    container_name: assistant-db
    env_file:
      - .env
    environment:
      - TZ=Africa/Johannesburg
    volumes:
      - ./appdata/db:/var/lib/postgresql/data
      - /etc/timezone:/etc/timezone:ro
      - /etc/localtime:/etc/localtime:ro
    ports:
      - "5432:5432"
    security_opt:
      - no-new-privileges:true
    restart: unless-stopped

  assistant-app:
    image: assistant:latest
    container_name: assistant-app
    depends_on:
      - assistant-db
    env_file:
      - .env
    volumes:
      - /etc/timezone:/etc/timezone:ro
      - /etc/localtime:/etc/localtime:ro
    ports:
      - "8080:8080"
    security_opt:
      - no-new-privileges:true
    restart: unless-stopped

networks:
  default:
    name: shared
    external: false
```

```ini
# Postgres database
## Database settings
POSTGRES_HOST=assistant-db
POSTGRES_PORT=5432
POSTGRES_PASSWORD=MY_SUPER_SECRET_PASSWORD

# SPRINGBOOT application
## Framework basics
ATECH_LOG_LEVEL=info
ATECH_LOG_PRINT_PROPERTIES=false
ATECH_JSON_SERIALISE_DATE_FORMAT='yyyy-MM-dd HH:mm:ss'

## Database Connection (aligned to 'Postgres database')
ATECH_SPRING_DATASOURCE_HOST=$POSTGRES_HOST:$POSTGRES_PORT
ATECH_SPRING_DATASOURCE_PWD=$POSTGRES_PASSWORD
### Email Settings
ATECH_APP_EMAIL_SMTP_HOST=smtp.gmail.com
ATECH_APP_EMAIL_SMTP_PORT=587
ATECH_APP_EMAIL_SMTP_UNAME=
ATECH_APP_EMAIL_SMTP_PWD=
ATECH_APP_EMAIL_SMTP_TEST_CONNECTION=false
ATECH_APP_EMAIL_SMTP_STARTTLS_ENABLE=true
ATECH_APP_EMAIL_FROM_EMAIL_ADDRESS=user@email.com

## Application
### App Name that will display on emails and page titles
ATECH_APP_NAME='My App'
# Register types
# 1. public -> anyone can register
# 2. private -> usernames are validated against a list
ATECH_APP_REGISTER_TYPE=private
### The hosting url to populate links in emails
ATECH_APP_URL_BASE=http://localhost:8080

### Initialize ADMIN
### When run for the first time, the user will be created.
### If the user exists, it will not create or update.
### Changing the ATECH_APP_CONFIG_USER_ADMIN_USERNAME will result in a new admin user being created.
ATECH_APP_CONFIG_USER_ADMIN_CREATE=true
ATECH_APP_CONFIG_USER_ADMIN_USERNAME=admin
ATECH_APP_CONFIG_USER_ADMIN_NAME=System
ATECH_APP_CONFIG_USER_ADMIN_SURNAME=Admin
ATECH_APP_CONFIG_USER_ADMIN_EMAIL=admin@email.com
ATECH_APP_CONFIG_USER_ADMIN_PWD=MY_SUPER_SECRET_PASSWORD

### OpenAi HTTP Properties
ATECH_APP_OPENAI_API_BASE_URL=https://api.openai.com/v1/
ATECH_APP_OPENAI_API_USER_RELAXED_HTTPS=true
ATECH_APP_OPENAI_API_ASSISTANT_BETA_VERSION=2
ATECH_APP_OPENAI_API_ASSISTANT_RESPONSE_POLL_SLEEP_DURATION=250
ATECH_APP_OPENAI_API_ASSISTANT_RESPONSE_POLL_MAX_RETRY=25
#### Step detailed logging for the process of asking and getting a response
ATECH_APP_OPENAI_API_ASSISTANT_LOGOUT_CREATE_THREAD=false
ATECH_APP_OPENAI_API_ASSISTANT_LOGOUT_ADD_MESSAGE=false
ATECH_APP_OPENAI_API_ASSISTANT_LOGOUT_RUN_THREAD=false
ATECH_APP_OPENAI_API_ASSISTANT_LOGOUT_GET_RUN_STATUS=false
ATECH_APP_OPENAI_API_ASSISTANT_LOGOUT_GET_MESSAGES=false
ATECH_APP_OPENAI_API_ASSISTANT_LOGOUT_GET_STEPS_FOR_RUN=false
```

#### Parameters

Containers are configured using parameters passed at runtime (such as those above). These parameters are separated by a colon and indicate `<external>:<internal>` respectively. For example, `-p 8080:80` would expose port `80` from inside the container to be accessible from the host's IP on port `8080` outside the container.
