-- liquibase formatted sql
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

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