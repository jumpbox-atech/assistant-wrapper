FROM ubuntu:24.04

ENV DEBIAN_FRONTEND=noninteractive

USER root
# Maintenance upgrade
RUN apt update && apt upgrade -y

# Package installation
RUN apt update && apt install curl nano jq openjdk-17-jre -y
RUN apt clean && apt autoremove && apt autoclean -y && rm -rf /var/lib/apt/lists/*

# Setup JAVA_HOME
RUN echo 'JAVA_HOME="/usr/lib/jvm/java-17-openjdk-amd64"' | tee -a /etc/environment

# Define environment variables for user and group IDs
ARG USER_ID=1001
ARG GROUP_ID=1001

# Create the group with the specified GID
RUN groupadd -g $GROUP_ID dummygroup
# Create the user with the specified UID and add them to the newly created group
RUN useradd -l -u $USER_ID -g $GROUP_ID -ms /usr/bin/bash dummy

# Directories
# Set umask to ensure new files and directories have the desired permissions
# Set umask to 002 to ensure that new files and directories created
# have read and write permissions for the user and group, and read
# permissions for others. This results in default permissions of 775
# for directories and 664 for files.
ENV UMASK=002
RUN echo "umask $UMASK" >> /etc/profile

# Create app and data directories
RUN mkdir -vp /home/dummy/app && mkdir -vp /home/dummy/config && mkdir -vp /home/dummy/data && mkdir -vp /home/dummy/log

# Declare the directory as a mount
VOLUME /home/dummy/app
VOLUME /home/dummy/config
VOLUME /home/dummy/data
VOLUME /home/dummy/log

# Set the timezone to South Africa/Johannesburg
ENV TZ=Africa/Johannesburg

# Create environment variables for run time
ENV ATECH_LOG_LEVEL=''
ENV ATECH_LOG_PRINT_PROPERTIES=''
ENV ATECH_JSON_SERIALISE_DATE_FORMAT=''
ENV ATECH_SPRING_DATASOURCE_HOST=''
ENV ATECH_SPRING_DATASOURCE_PWD=''
ENV ATECH_APP_NAME=''
ENV ATECH_APP_REGISTER_TYPE=''
ENV ATECH_APP_URL_BASE=''
ENV ATECH_APP_EMAIL_SMTP_HOST=''
ENV ATECH_APP_EMAIL_SMTP_PORT=''
ENV ATECH_APP_EMAIL_SMTP_UNAME=''
ENV ATECH_APP_EMAIL_SMTP_PWD=''
ENV ATECH_APP_EMAIL_SMTP_TEST_CONNECTION=''
ENV ATECH_APP_EMAIL_SMTP_STARTTLS_ENABLE=''
ENV ATECH_APP_EMAIL_FROM_EMAIL_ADDRESS=''
ENV ATECH_APP_CONFIG_USER_ADMIN_CREATE=''
ENV ATECH_APP_CONFIG_USER_ADMIN_USERNAME=''
ENV ATECH_APP_CONFIG_USER_ADMIN_NAME=''
ENV ATECH_APP_CONFIG_USER_ADMIN_SURNAME=''
ENV ATECH_APP_CONFIG_USER_ADMIN_EMAIL=''
ENV ATECH_APP_CONFIG_USER_ADMIN_PWD=''
ENV ATECH_APP_OPENAI_API_BASE_URL=''
ENV ATECH_APP_OPENAI_API_USER_RELAXED_HTTPS=''
ENV ATECH_APP_OPENAI_API_ASSISTANT_BETA_VERSION=''
ENV ATECH_APP_OPENAI_API_ASSISTANT_RESPONSE_POLL_SLEEP_DURATION=''
ENV ATECH_APP_OPENAI_API_ASSISTANT_RESPONSE_POLL_MAX_RETRY=''
ENV ATECH_APP_OPENAI_API_ASSISTANT_LOGOUT_CREATE_THREAD=''
ENV ATECH_APP_OPENAI_API_ASSISTANT_LOGOUT_ADD_MESSAGE=''
ENV ATECH_APP_OPENAI_API_ASSISTANT_LOGOUT_RUN_THREAD=''
ENV ATECH_APP_OPENAI_API_ASSISTANT_LOGOUT_GET_RUN_STATUS=''
ENV ATECH_APP_OPENAI_API_ASSISTANT_LOGOUT_GET_MESSAGES=''
ENV ATECH_APP_OPENAI_API_ASSISTANT_LOGOUT_GET_STEPS_FOR_RUN=''

# Copy config
WORKDIR /home/dummy/config
COPY /config /home/dummy/config
# Copy app
WORKDIR /home/dummy/app
COPY /app /home/dummy/app

# Set permissions and owner for the directories
RUN chown -vR $USER_ID:$GROUP_ID /home/dummy/app
RUN chown -vR $USER_ID:$GROUP_ID /home/dummy/config
RUN chown -vR $USER_ID:$GROUP_ID /home/dummy/data
RUN chown -vR $USER_ID:$GROUP_ID /home/dummy/log

# Entry point added to set application properties
RUN chmod +x entry.sh
ENTRYPOINT ["./entry.sh"]

# Switch to the user
USER dummy

# Expose the application port
EXPOSE 8080

LABEL africa.za.atech.app.name="Assistant Wrapper"
LABEL africa.za.atech.app.author.name=""
LABEL africa.za.atech.app.author.email=""

ARG APP_VERSION=2025.01
LABEL africa.za.atech.app.version=${APP_VERSION}
