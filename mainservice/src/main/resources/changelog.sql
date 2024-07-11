-- liquibase formatted sql

-- changeset lia:1720702806986-1
CREATE TABLE customer
(
    id                 UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    telegram_id        TEXT,
    name               TEXT,
    points             DOUBLE PRECISION,
    phone_number       TEXT,
    qr_code            TEXT,
    qr_code_expires_at TIMESTAMP WITHOUT TIME ZONE,
    blocked_reason     TEXT,
    created_at         TIMESTAMP WITHOUT TIME ZONE,
    last_modified_at   TIMESTAMP WITHOUT TIME ZONE
);

-- changeset lia:1720702806986-2
CREATE TABLE customer_order
(
    id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    customer_id      UUID,
    shop_id          UUID,
    status           TEXT,
    rating           INTEGER,
    created_at       TIMESTAMP WITHOUT TIME ZONE,
    last_modified_at TIMESTAMP WITHOUT TIME ZONE
);

-- changeset lia:1720702806986-3
CREATE TABLE customer_order_item
(
    id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_id         UUID,
    product_type_id  UUID,
    quantity         INTEGER,
    created_at       TIMESTAMP WITHOUT TIME ZONE,
    last_modified_at TIMESTAMP WITHOUT TIME ZONE
);

-- changeset lia:1720702806986-4
CREATE TABLE inventory_type
(
    id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name             TEXT,
    created_at       TIMESTAMP WITHOUT TIME ZONE,
    last_modified_at TIMESTAMP WITHOUT TIME ZONE
);

-- changeset lia:1720702806986-5
CREATE TABLE product
(
    id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    product_type_id  TEXT,
    label            TEXT,
    price            DOUBLE PRECISION,
    created_at       TIMESTAMP WITHOUT TIME ZONE,
    last_modified_at TIMESTAMP WITHOUT TIME ZONE
);

-- changeset lia:1720702806986-6
CREATE TABLE product_inventory
(
    id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    product_id       UUID,
    inventory_id     UUID,
    quantity         DOUBLE PRECISION,
    created_at       TIMESTAMP WITHOUT TIME ZONE,
    last_modified_at TIMESTAMP WITHOUT TIME ZONE
);

-- changeset lia:1720702806986-7
CREATE TABLE product_type
(
    id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name             TEXT,
    color            TEXT,
    shop_type_id     TEXT,
    created_at       TIMESTAMP WITHOUT TIME ZONE,
    last_modified_at TIMESTAMP WITHOUT TIME ZONE
);

-- changeset lia:1720702806986-8
CREATE TABLE promo
(
    id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code             TEXT,
    name             TEXT,
    text             TEXT,
    image            BYTEA,
    shop_type_id     TEXT,
    product_type_id  TEXT,
    status           TEXT             DEFAULT 'NEW',
    starts_at        TIMESTAMP WITHOUT TIME ZONE,
    ends_at          TIMESTAMP WITHOUT TIME ZONE,
    created_at       TIMESTAMP WITHOUT TIME ZONE,
    last_modified_at TIMESTAMP WITHOUT TIME ZONE
);

-- changeset lia:1720702806986-9
CREATE TABLE shop
(
    id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    shop_type_id     TEXT,
    name             TEXT,
    location         TEXT,
    status           TEXT,
    created_at       TIMESTAMP WITHOUT TIME ZONE,
    last_modified_at TIMESTAMP WITHOUT TIME ZONE
);

-- changeset lia:1720702806986-10
CREATE TABLE shop_inventory
(
    id                UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    inventory_type_id UUID,
    shop_id           UUID,
    created_at        TIMESTAMP WITHOUT TIME ZONE,
    last_modified_at  TIMESTAMP WITHOUT TIME ZONE
);

-- changeset lia:1720702806986-11
CREATE TABLE shop_shift
(
    id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    shop_id          UUID,
    worker_id        UUID,
    created_at       TIMESTAMP WITHOUT TIME ZONE,
    last_modified_at TIMESTAMP WITHOUT TIME ZONE
);

-- changeset lia:1720702806986-12
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

-- changeset lia:1720702806986-13
CREATE TABLE worker
(
    id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name             TEXT,
    status           TEXT,
    created_at       TIMESTAMP WITHOUT TIME ZONE,
    last_modified_at TIMESTAMP WITHOUT TIME ZONE
);

