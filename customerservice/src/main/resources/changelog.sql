-- liquibase formatted sql
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- changeset lia:1
CREATE TABLE customer
(
    id                 UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    telegram_id        TEXT,
    name               TEXT,
    points             INT,
    points_reserved    INT,
    phone_number       TEXT,
    qr_code            TEXT,
    qr_code_expires_at TIMESTAMP WITHOUT TIME ZONE,
    blocked_reason     TEXT,
    created_at         TIMESTAMP WITHOUT TIME ZONE,
    last_modified_at   TIMESTAMP WITHOUT TIME ZONE
);