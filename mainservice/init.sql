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

CREATE TABLE "order"
(
    id         uuid PRIMARY KEY DEFAULT gen_random_uuid() NOT NULL,
    user_id    uuid                                       NOT NULL,
    product    varchar(1000)                              NOT NULL,
    created_at timestamp                                  NOT NULL,
    status     varchar(1000)                              NOT NULL
);

CREATE TABLE product
(
    id    varchar(1000) PRIMARY KEY NOT NULL,
    name  varchar(1000)             NOT NULL,
    price double precision          NOT NULL,
    color varchar(16)
);