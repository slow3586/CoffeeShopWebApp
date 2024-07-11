-- liquibase formatted sql

-- changeset lia:1720693664309-1
CREATE TABLE customer
(
    id                 UUID             NOT NULL,
    telegram_id        VARCHAR(255),
    name               VARCHAR(255),
    points             DOUBLE PRECISION NOT NULL,
    phone_number       VARCHAR(255),
    qr_code            VARCHAR(255),
    qr_code_expires_at TIMESTAMP WITHOUT TIME ZONE,
    blocked_reason     VARCHAR(255),
    created_at         TIMESTAMP WITHOUT TIME ZONE,
    last_modified_at   TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_customer PRIMARY KEY (id)
);

-- changeset lia:1720693664309-2
CREATE TABLE customer_order
(
    id               UUID    NOT NULL,
    customer_id      UUID,
    shop_id          UUID,
    status           VARCHAR(255),
    rating           INTEGER NOT NULL,
    created_at       TIMESTAMP WITHOUT TIME ZONE,
    last_modified_at TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_customer_order PRIMARY KEY (id)
);

-- changeset lia:1720693664309-3
CREATE TABLE customer_order_item
(
    id               UUID    NOT NULL,
    order_id         UUID,
    product_type_id  UUID,
    quantity         INTEGER NOT NULL,
    created_at       TIMESTAMP WITHOUT TIME ZONE,
    last_modified_at TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_customer_order_item PRIMARY KEY (id)
);

-- changeset lia:1720693664309-4
CREATE TABLE inventory_type
(
    id               VARCHAR(255) NOT NULL,
    name             VARCHAR(255),
    created_at       TIMESTAMP WITHOUT TIME ZONE,
    last_modified_at TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_inventory_type PRIMARY KEY (id)
);

-- changeset lia:1720693664309-5
CREATE TABLE product
(
    id               VARCHAR(255)     NOT NULL,
    product_type_id  VARCHAR(255),
    label            VARCHAR(255),
    price            DOUBLE PRECISION NOT NULL,
    created_at       TIMESTAMP WITHOUT TIME ZONE,
    last_modified_at TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_product PRIMARY KEY (id)
);

-- changeset lia:1720693664309-6
CREATE TABLE product_inventory
(
    id               UUID             NOT NULL,
    product_id       UUID,
    inventory_id     UUID,
    quantity         DOUBLE PRECISION NOT NULL,
    created_at       TIMESTAMP WITHOUT TIME ZONE,
    last_modified_at TIMESTAMP WITHOUT TIME ZONE,
    order_id         UUID,
    CONSTRAINT pk_product_inventory PRIMARY KEY (id)
);

-- changeset lia:1720693664309-7
CREATE TABLE product_type
(
    id               VARCHAR(255) NOT NULL,
    name             VARCHAR(255),
    color            VARCHAR(255),
    shop_type_id     VARCHAR(255),
    created_at       TIMESTAMP WITHOUT TIME ZONE,
    last_modified_at TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_product_type PRIMARY KEY (id)
);

-- changeset lia:1720693664309-8
CREATE TABLE promo
(
    id               UUID NOT NULL,
    code             VARCHAR(255),
    name             VARCHAR(255),
    text             VARCHAR(255),
    image            BYTEA,
    shop_type_id     VARCHAR(255),
    product_type_id  VARCHAR(255),
    status           VARCHAR(255),
    starts_at        TIMESTAMP WITHOUT TIME ZONE,
    ends_at          TIMESTAMP WITHOUT TIME ZONE,
    created_at       TIMESTAMP WITHOUT TIME ZONE,
    last_modified_at TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_promo PRIMARY KEY (id)
);

-- changeset lia:1720693664309-9
CREATE TABLE shop
(
    id               UUID NOT NULL,
    shop_type_id     VARCHAR(255),
    name             VARCHAR(255),
    location         VARCHAR(255),
    status           VARCHAR(255),
    created_at       TIMESTAMP WITHOUT TIME ZONE,
    last_modified_at TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_shop PRIMARY KEY (id)
);

-- changeset lia:1720693664309-10
CREATE TABLE shop_inventory
(
    id                UUID NOT NULL,
    inventory_type_id UUID,
    shop_id           UUID,
    created_at        TIMESTAMP WITHOUT TIME ZONE,
    last_modified_at  TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_shop_inventory PRIMARY KEY (id)
);

-- changeset lia:1720693664309-11
CREATE TABLE shop_shift
(
    id               UUID NOT NULL,
    shop_id          UUID,
    worker_id        UUID,
    created_at       TIMESTAMP WITHOUT TIME ZONE,
    last_modified_at TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_shop_shift PRIMARY KEY (id)
);

-- changeset lia:1720693664309-12
CREATE TABLE shop_type
(
    id               VARCHAR(255) NOT NULL,
    name             VARCHAR(255),
    location         VARCHAR(255),
    status           VARCHAR(255),
    created_at       TIMESTAMP WITHOUT TIME ZONE,
    last_modified_at TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_shop_type PRIMARY KEY (id)
);

-- changeset lia:1720693664309-13
CREATE TABLE telegram_publish
(
    id               UUID    NOT NULL,
    customer_id      VARCHAR(255),
    text             VARCHAR(255),
    sent_at          TIMESTAMP WITHOUT TIME ZONE,
    attempts         INTEGER NOT NULL,
    error            VARCHAR(255),
    last_attempt_at  TIMESTAMP WITHOUT TIME ZONE,
    created_at       TIMESTAMP WITHOUT TIME ZONE,
    last_modified_at TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_telegram_publish PRIMARY KEY (id)
);

-- changeset lia:1720693664309-14
CREATE TABLE worker
(
    id               UUID NOT NULL,
    name             VARCHAR(255),
    status           VARCHAR(255),
    created_at       TIMESTAMP WITHOUT TIME ZONE,
    last_modified_at TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_worker PRIMARY KEY (id)
);

-- changeset lia:1720693664309-15
ALTER TABLE customer_order
    ADD CONSTRAINT uc_customer_order_customer UNIQUE (customer_id);

-- changeset lia:1720693664309-16
ALTER TABLE customer_order_item
    ADD CONSTRAINT uc_customer_order_item_order UNIQUE (order_id);

-- changeset lia:1720693664309-17
ALTER TABLE customer_order_item
    ADD CONSTRAINT uc_customer_order_item_product_type UNIQUE (product_type_id);

-- changeset lia:1720693664309-18
ALTER TABLE product
    ADD CONSTRAINT uc_product_product_type UNIQUE (product_type_id);

-- changeset lia:1720693664309-19
ALTER TABLE shop_inventory
    ADD CONSTRAINT uc_shop_inventory_inventory_type UNIQUE (inventory_type_id);

-- changeset lia:1720693664309-20
ALTER TABLE shop_inventory
    ADD CONSTRAINT uc_shop_inventory_shop UNIQUE (shop_id);

-- changeset lia:1720693664309-21
ALTER TABLE customer_order_item
    ADD CONSTRAINT FK_CUSTOMER_ORDER_ITEM_ON_ORDER FOREIGN KEY (order_id) REFERENCES customer_order (id);

-- changeset lia:1720693664309-22
ALTER TABLE customer_order_item
    ADD CONSTRAINT FK_CUSTOMER_ORDER_ITEM_ON_PRODUCT_TYPE FOREIGN KEY (product_type_id) REFERENCES product_type (id);

-- changeset lia:1720693664309-23
ALTER TABLE customer_order
    ADD CONSTRAINT FK_CUSTOMER_ORDER_ON_CUSTOMER FOREIGN KEY (customer_id) REFERENCES customer (id);

-- changeset lia:1720693664309-24
ALTER TABLE customer_order
    ADD CONSTRAINT FK_CUSTOMER_ORDER_ON_SHOP FOREIGN KEY (shop_id) REFERENCES shop (id);

-- changeset lia:1720693664309-25
ALTER TABLE product_inventory
    ADD CONSTRAINT FK_PRODUCT_INVENTORY_ON_ORDER FOREIGN KEY (order_id) REFERENCES customer_order (id);

-- changeset lia:1720693664309-26
ALTER TABLE product_inventory
    ADD CONSTRAINT FK_PRODUCT_INVENTORY_ON_PRODUCT FOREIGN KEY (product_id) REFERENCES product (id);

-- changeset lia:1720693664309-27
ALTER TABLE product
    ADD CONSTRAINT FK_PRODUCT_ON_PRODUCT_TYPE FOREIGN KEY (product_type_id) REFERENCES product_type (id);

-- changeset lia:1720693664309-28
ALTER TABLE shop_inventory
    ADD CONSTRAINT FK_SHOP_INVENTORY_ON_INVENTORY_TYPE FOREIGN KEY (inventory_type_id) REFERENCES inventory_type (id);

-- changeset lia:1720693664309-29
ALTER TABLE shop_inventory
    ADD CONSTRAINT FK_SHOP_INVENTORY_ON_SHOP FOREIGN KEY (shop_id) REFERENCES shop (id);

