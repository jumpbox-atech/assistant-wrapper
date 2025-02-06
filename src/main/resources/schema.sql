-- -------------------------------------------
--               Users
-- DROP SCHEMA public CASCADE;
-- CREATE SCHEMA public;
-- -------------------------------------------
CREATE TABLE public.organisation
(
    id                   serial,
    masked_id            character varying           NOT NULL,
    created_datetime     timestamp without time zone NOT NULL,
    created_by           character varying           NOT NULL,
    name                 character varying           NOT NULL,
    disabled             boolean                     NOT NULL,
    updated_datetime     timestamp without time zone         ,
    updated_by           character varying                   ,
    PRIMARY KEY (id)
);

ALTER TABLE IF EXISTS public.organization
    OWNER to "postgres_admin";

CREATE TABLE public.department
(
    id                   serial,
    masked_id            character varying           NOT NULL,
    organisation_id      integer                     NOT NULL,
    created_datetime     timestamp without time zone NOT NULL,
    created_by           character varying           NOT NULL,
    name                 character varying           NOT NULL,
    disabled             boolean                     NOT NULL,
    updated_datetime     timestamp without time zone         ,
    updated_by           character varying                   ,
    PRIMARY KEY (id)
);

ALTER TABLE IF EXISTS public.department
    OWNER to "postgres_admin";


CREATE TABLE public.users
(
    id                   serial,
    masked_id            character varying           NOT NULL,
    created_datetime     timestamp without time zone NOT NULL,
    username             character varying           NOT NULL,
    name                 character varying           NOT NULL,
    surname              character varying           NOT NULL,
    email_address        character varying           NOT NULL,
    role                 character varying           NOT NULL,
    password             character varying           NOT NULL,
    disabled             boolean                     NOT NULL,
    mfa_disabled         boolean                     NOT NULL,
    mfa_secret           character varying           NOT NULL,
    custom_property_a    character varying           NOT NULL,
    custom_property_b    character varying           NOT NULL,
    custom_property_c    character varying           NOT NULL,
    custom_property_d    character varying           NOT NULL,
    inserted_by_username character varying           NOT NULL,
    PRIMARY KEY (id)
);

ALTER TABLE IF EXISTS public.users
    OWNER to "postgres_admin";

CREATE TABLE public.registration_whitelist
(
    id                   serial,
    created_datetime     timestamp without time zone NOT NULL,
    username             character varying           NOT NULL,
    name                 character varying           NOT NULL,
    surname              character varying           NOT NULL,
    email_address        character varying           NOT NULL,
    custom_property_a    character varying           NOT NULL,
    custom_property_b    character varying           NOT NULL,
    custom_property_c    character varying           NOT NULL,
    custom_property_d    character varying           NOT NULL,
    inserted_by_username character varying           NOT NULL,
    registered           boolean                     NOT NULL,
    PRIMARY KEY (id)
);

ALTER TABLE IF EXISTS public.registration_whitelist
    OWNER to "postgres_admin";

-- -------------------------------------------
--               app specific
-- -------------------------------------------

CREATE TABLE public.assistants
(
    id                      serial                      NOT NULL,
    created_datetime        timestamp without time zone NOT NULL,
    created_by              character varying           NOT NULL,
    name                    character varying           NOT NULL,
    unique_name             character varying           NOT NULL,
    description             character varying           NOT NULL,
    additional_instructions character varying                   ,
    openai_organisation_id  character varying           NOT NULL,
    openai_assistant_id     character varying           NOT NULL,
    api_key                 character varying           NOT NULL,
    disabled                boolean                     NOT NULL,
    update_datetime         timestamp without time zone,
    update_by               character varying,
    disabled_by             character varying,
    disabled_datetime       timestamp without time zone,
    PRIMARY KEY (id)
);

ALTER TABLE IF EXISTS public.assistants
    OWNER to "postgres_admin";

CREATE TABLE public.chats
(
    id                      serial                      NOT NULL,
    created_datetime        timestamp without time zone NOT NULL,
    masked_id               character varying           NOT NULL,
    created_by              character varying           NOT NULL,
    description             character varying           NOT NULL,
    update_datetime         timestamp without time zone NOT NULL,
    openai_assistant_id     character varying           NOT NULL,
    openai_thread_id        character varying           NOT NULL,
    openai_first_message_id character varying           NOT NULL,
    openai_last_message_id  character varying           NOT NULL,
    purge                   boolean                     NOT NULL,
    PRIMARY KEY (id)
);

ALTER TABLE IF EXISTS public.chats
    OWNER to "postgres_admin";

CREATE TABLE public.chats_history
(
    id       serial            NOT NULL,
    chats_id integer           NOT NULL,
    history  character varying NOT NULL,
    PRIMARY KEY (id)
);

ALTER TABLE IF EXISTS public.chats_history
    OWNER to "postgres_admin";

CREATE TABLE public.chats_meta
(
    id                               serial  NOT NULL,
    chats_id                         integer NOT NULL,
    openai_message_collection_object character varying,
    PRIMARY KEY (id)
);

ALTER TABLE IF EXISTS public.chats_meta
    OWNER to "postgres_admin";