CREATE TABLE customer
(
    id                 uuid PRIMARY KEY DEFAULT gen_random_uuid() NOT NULL,
    telegram_id        varchar(1000) UNIQUE                       NOT NULL,
    phone_number       varchar(16) UNIQUE,
    qr_code            varchar(16),
    qr_code_expires_at timestamp,
    name               varchar(1000),
    blocked_reason     varchar(1000),
    points             double precision                           NOT NULL
);