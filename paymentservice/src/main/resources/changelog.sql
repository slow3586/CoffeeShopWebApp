-- liquibase formatted sql
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

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
