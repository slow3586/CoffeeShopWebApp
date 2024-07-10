CREATE TABLE customer
(
    id                 uuid PRIMARY KEY DEFAULT gen_random_uuid() NOT NULL,
    telegram_id        varchar(1000) UNIQUE                       NOT NULL,
    phone_number       varchar(16) UNIQUE,
    qr_code            varchar(16),
    qr_code_expires_at timestamp,
    name               varchar(1000),
    blocked_reason     varchar(1000),
    points             double precision,
    created_at         timestamp,
    registered_at      timestamp
);

CREATE TABLE inventory
(
    id         uuid PRIMARY KEY DEFAULT gen_random_uuid() NOT NULL,
    name       varchar(1000)                              NOT NULL,
    count      double precision                           NOT NULL,
    updated_at timestamp                                  NOT NULL
);

CREATE TABLE "order"
(
    id            uuid PRIMARY KEY DEFAULT gen_random_uuid() NOT NULL,
    user_id       uuid                                       NOT NULL,
    shop_id       uuid                                       NOT NULL,
    product       varchar(1000)                              NOT NULL,
    status        varchar(1000)                              NOT NULL,
    created_at    timestamp                                  NOT NULL,
    approved_at   timestamp                                  NOT NULL,
    denied_at     timestamp                                  NOT NULL,
    denied_reason varchar                                    NOT NULL,
    payment_at    timestamp,
    updated_at    timestamp,
    completed_at  timestamp
);

CREATE TABLE product
(
    id    uuid PRIMARY KEY DEFAULT gen_random_uuid() NOT NULL,
    name  varchar(1000)                              NOT NULL,
    price double precision                           NOT NULL,
    color varchar(16)
);

CREATE TABLE shop
(
    id       uuid PRIMARY KEY DEFAULT gen_random_uuid() NOT NULL,
    name     varchar(1000)                              NOT NULL,
    location varchar                                    NOT NULL
);

CREATE TABLE worker
(
    id       uuid PRIMARY KEY DEFAULT gen_random_uuid() NOT NULL,
    shop_id  uuid,
    name     varchar(1000)                              NOT NULL,
    position varchar
);