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