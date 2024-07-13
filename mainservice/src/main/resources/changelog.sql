-- liquibase formatted sql
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- changeset lia:1
CREATE TABLE customer
(
    id                 UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    telegram_id        TEXT,
    name               TEXT,
    points             INT,
    phone_number       TEXT,
    qr_code            TEXT,
    qr_code_expires_at TIMESTAMP WITHOUT TIME ZONE,
    blocked_reason     TEXT,
    points_reserved    INT,
    created_at         TIMESTAMP WITHOUT TIME ZONE,
    last_modified_at   TIMESTAMP WITHOUT TIME ZONE
);

-- changeset lia:2
CREATE TABLE customer_order
(
    id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    customer_id      UUID,
    shop_id          UUID,
    rating           INTEGER,
    status           TEXT,
    paid_at          TIMESTAMP WITHOUT TIME ZONE,
    completed_at     TIMESTAMP WITHOUT TIME ZONE,
    created_at       TIMESTAMP WITHOUT TIME ZONE,
    last_modified_at TIMESTAMP WITHOUT TIME ZONE
);

-- changeset lia:3
CREATE TABLE customer_order_item
(
    id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_id         UUID,
    product_id       UUID,
    quantity         INTEGER,
    created_at       TIMESTAMP WITHOUT TIME ZONE,
    last_modified_at TIMESTAMP WITHOUT TIME ZONE
);

-- changeset lia:4
CREATE TABLE inventory_type
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
    product_type_id  uuid,
    label            TEXT,
    price            INT,
    created_at       TIMESTAMP WITHOUT TIME ZONE,
    last_modified_at TIMESTAMP WITHOUT TIME ZONE
);

-- changeset lia:6
CREATE TABLE product_inventory
(
    id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    product_id       UUID,
    inventory_id     UUID,
    quantity         INT,
    created_at       TIMESTAMP WITHOUT TIME ZONE,
    last_modified_at TIMESTAMP WITHOUT TIME ZONE
);

-- changeset lia:7
CREATE TABLE product_type
(
    id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name             TEXT,
    color            TEXT,
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
    id                UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    shop_id           UUID,
    inventory_type_id UUID,
    quantity          int,
    reserved          int,
    created_at        TIMESTAMP WITHOUT TIME ZONE,
    last_modified_at  TIMESTAMP WITHOUT TIME ZONE
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
    id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    telegram_id      TEXT,
    text             TEXT,
    sent_at          TIMESTAMP WITHOUT TIME ZONE,
    attempts         INTEGER          DEFAULT 0,
    error            TEXT,
    last_attempt_at  TIMESTAMP WITHOUT TIME ZONE,
    created_at       TIMESTAMP WITHOUT TIME ZONE,
    last_modified_at TIMESTAMP WITHOUT TIME ZONE
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
    order_id                  UUID NOT NULL,
    value                     INT,
    payment_system_id         TEXT,
    sent_to_payment_system    BOOLEAN,
    sent_to_payment_system_at TIMESTAMP,
    denied_reason             TEXT,
    denied_at                 TIMESTAMP
);

-- changeset lia:15
CREATE TABLE payment_check
(
    id                    UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    payment_id            UUID NOT NULL,
    payment_system_status TEXT,
    status                TEXT
);

