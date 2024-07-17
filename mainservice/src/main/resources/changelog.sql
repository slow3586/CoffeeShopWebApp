-- liquibase formatted sql
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- changeset lia:4
CREATE TABLE product_inventory_type
(
    id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name             TEXT,
    created_at       TIMESTAMP WITHOUT TIME ZONE,
    last_modified_at TIMESTAMP WITHOUT TIME ZONE
);

-- changeset lia:5
CREATE TABLE product
(
    id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name             TEXT,
    price            INT,
    created_at       TIMESTAMP WITHOUT TIME ZONE,
    last_modified_at TIMESTAMP WITHOUT TIME ZONE
);

-- changeset lia:6
CREATE TABLE product_inventory
(
    id                        UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    product_id                UUID,
    product_inventory_type_id UUID,
    quantity                  INT,
    created_at                TIMESTAMP WITHOUT TIME ZONE,
    last_modified_at          TIMESTAMP WITHOUT TIME ZONE
);

-- changeset lia:7
CREATE TABLE product_group
(
    id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name             TEXT,
    created_at       TIMESTAMP WITHOUT TIME ZONE,
    last_modified_at TIMESTAMP WITHOUT TIME ZONE
);

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

-- changeset lia:9
CREATE TABLE shop
(
    id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name             TEXT,
    location         TEXT,
    status           TEXT,
    created_at       TIMESTAMP WITHOUT TIME ZONE,
    last_modified_at TIMESTAMP WITHOUT TIME ZONE
);

-- changeset lia:10
CREATE TABLE shop_inventory
(
    id                        UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    shop_id                   UUID,
    product_inventory_type_id UUID,
    quantity                  int,
    reserved                  int,
    created_at                TIMESTAMP WITHOUT TIME ZONE,
    last_modified_at          TIMESTAMP WITHOUT TIME ZONE
);

-- changeset lia:11
CREATE TABLE shop_shift
(
    id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    shop_id          UUID,
    worker_id        UUID,
    created_at       TIMESTAMP WITHOUT TIME ZONE,
    last_modified_at TIMESTAMP WITHOUT TIME ZONE
);

-- changeset lia:12
CREATE TABLE telegram_publish
(
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    telegram_bot_id TEXT,
    customer_id     UUID,
    text            TEXT,
    status          TEXT,
    sent_at         TIMESTAMP WITHOUT TIME ZONE
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

-- changeset lia:14
CREATE TABLE payment
(
    id                        UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_id                  UUID UNIQUE NOT NULL,
    status                    TEXT,
    points                    INTEGER,
    value                     INTEGER,
    sent_to_payment_system_at TIMESTAMP,
    check_id                  TEXT,
    check_received_at         TIMESTAMP,
    check_note                TEXT
);

