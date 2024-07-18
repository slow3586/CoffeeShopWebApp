-- liquibase formatted sql
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- changeset lia:8
CREATE TABLE promo
(
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code                TEXT,
    name                TEXT,
    text                TEXT,
    image               BYTEA,
    product_type_id     TEXT,
    queued_for_telegram BOOLEAN,
    starts_at           TIMESTAMP WITHOUT TIME ZONE,
    ends_at             TIMESTAMP WITHOUT TIME ZONE,
    created_at          TIMESTAMP WITHOUT TIME ZONE,
    last_modified_at    TIMESTAMP WITHOUT TIME ZONE
);

-- changeset lia:12
CREATE TABLE telegram_publish
(
    id                UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    telegram_bot_id   TEXT,
    customer_id       UUID,
    customer_group_id UUID,
    text              TEXT,
    status            TEXT,
    sent_at           TIMESTAMP WITHOUT TIME ZONE
);

-- changeset lia:13
CREATE TABLE worker
(
    id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name             TEXT,
    status           TEXT,
    created_at       TIMESTAMP WITHOUT TIME ZONE,
    last_modified_at TIMESTAMP WITHOUT TIME ZONE
);
