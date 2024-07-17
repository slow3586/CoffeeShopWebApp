-- liquibase formatted sql
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- changeset lia:1
CREATE TABLE "order"
(
    id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    customer_id      UUID,
    shop_id          UUID,
    rating           INTEGER,
    status           TEXT,
    use_points       BOOLEAN,
    paid_at          TIMESTAMP WITHOUT TIME ZONE,
    completed_at     TIMESTAMP WITHOUT TIME ZONE,
    created_at       TIMESTAMP WITHOUT TIME ZONE,
    last_modified_at TIMESTAMP WITHOUT TIME ZONE
);

-- changeset lia:2
CREATE TABLE order_item
(
    id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_id         UUID NOT NULL,
    product_id       UUID NOT NULL,
    quantity         INTEGER NOT NULL,
    created_at       TIMESTAMP WITHOUT TIME ZONE,
    last_modified_at TIMESTAMP WITHOUT TIME ZONE
);