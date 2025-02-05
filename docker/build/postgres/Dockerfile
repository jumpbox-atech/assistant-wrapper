FROM postgres:14.2-bullseye

COPY config/schema.sql /docker-entrypoint-initdb.d/01_schema.sql

# Do not change
ENV POSTGRES_USER='postgres_admin'
ENV POSTGRES_DB='framework'

LABEL africa.za.atech.app.name="Assistant Wrapper"
LABEL africa.za.atech.app.version="2025.01"
LABEL africa.za.atech.app.author.name=""
LABEL africa.za.atech.app.author.email=""
LABEL africa.za.atech.app.base="postgres:14.2-bullseye"
