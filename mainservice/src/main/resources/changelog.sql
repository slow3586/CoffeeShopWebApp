-- liquibase formatted sql

-- changeset lia:1720639199748-1
ALTER TABLE customer
    RENAME TO customer;

-- changeset lia:1720639199748-2
CREATE TABLE customer_order
(
    id               UUID    NOT NULL,
    customer_id      UUID,
    shop_id          UUID,
    status           VARCHAR(255),
    rating           INTEGER NOT NULL,
    created_at       TIMESTAMP WITHOUT TIME ZONE,
    last_modified_at TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_customerorderentity PRIMARY KEY (id)
);

-- changeset lia:1720639199748-3
CREATE TABLE customer_order_item
(
    id               UUID    NOT NULL,
    order_id         UUID,
    product_type_id  UUID,
    quantity         INTEGER NOT NULL,
    created_at       TIMESTAMP WITHOUT TIME ZONE,
    last_modified_at TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_customerorderitementity PRIMARY KEY (id)
);

-- changeset lia:1720639199748-4
CREATE TABLE inventory_type
(
    id               UUID NOT NULL,
    name             VARCHAR(255),
    created_at       TIMESTAMP WITHOUT TIME ZONE,
    last_modified_at TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_inventorytypeentity PRIMARY KEY (id)
);

-- changeset lia:1720639199748-5
CREATE TABLE product
(
    id               UUID             NOT NULL,
    product_type_id  UUID,
    label            VARCHAR(255),
    price            DOUBLE PRECISION NOT NULL,
    created_at       TIMESTAMP WITHOUT TIME ZONE,
    last_modified_at TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_productentity PRIMARY KEY (id)
);

-- changeset lia:1720639199748-6
CREATE TABLE product_inventory
(
    id               UUID             NOT NULL,
    product_id       UUID,
    inventory_id     UUID,
    quantity         DOUBLE PRECISION NOT NULL,
    created_at       TIMESTAMP WITHOUT TIME ZONE,
    last_modified_at TIMESTAMP WITHOUT TIME ZONE,
    order_id         UUID,
    CONSTRAINT pk_productinventoryentity PRIMARY KEY (id)
);

-- changeset lia:1720639199748-7
CREATE TABLE product_type
(
    id               UUID NOT NULL,
    name             VARCHAR(255),
    color            VARCHAR(255),
    created_at       TIMESTAMP WITHOUT TIME ZONE,
    last_modified_at TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_producttypeentity PRIMARY KEY (id)
);

-- changeset lia:1720639199748-8
CREATE TABLE shop
(
    id               UUID NOT NULL,
    name             VARCHAR(255),
    location         VARCHAR(255),
    status           VARCHAR(255),
    created_at       TIMESTAMP WITHOUT TIME ZONE,
    last_modified_at TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_shopentity PRIMARY KEY (id)
);

-- changeset lia:1720639199748-9
CREATE TABLE shop_inventory
(
    id                UUID NOT NULL,
    inventory_type_id UUID,
    shop_id           UUID,
    created_at        TIMESTAMP WITHOUT TIME ZONE,
    last_modified_at  TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_shopinventoryentity PRIMARY KEY (id)
);

-- changeset lia:1720639199748-10
CREATE TABLE shop_shift
(
    id               UUID NOT NULL,
    shop_id          UUID,
    worker_id        UUID,
    created_at       TIMESTAMP WITHOUT TIME ZONE,
    last_modified_at TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_shopshiftentity PRIMARY KEY (id)
);

-- changeset lia:1720639199748-11
CREATE TABLE shop_type
(
    id               UUID NOT NULL,
    name             VARCHAR(255),
    location         VARCHAR(255),
    status           VARCHAR(255),
    created_at       TIMESTAMP WITHOUT TIME ZONE,
    last_modified_at TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_shoptypeentity PRIMARY KEY (id)
);

-- changeset lia:1720639199748-12
CREATE TABLE worker
(
    id               UUID NOT NULL,
    name             VARCHAR(255),
    status           VARCHAR(255),
    created_at       TIMESTAMP WITHOUT TIME ZONE,
    last_modified_at TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_workerentity PRIMARY KEY (id)
);

-- changeset lia:1720639199748-13
ALTER TABLE customer_order
    ADD CONSTRAINT uc_customerorderentity_customer UNIQUE (customer_id);

-- changeset lia:1720639199748-14
ALTER TABLE customer_order
    ADD CONSTRAINT uc_customerorderentity_shop UNIQUE (shop_id);

-- changeset lia:1720639199748-15
ALTER TABLE customer_order_item
    ADD CONSTRAINT uc_customerorderitementity_order UNIQUE (order_id);

-- changeset lia:1720639199748-16
ALTER TABLE customer_order_item
    ADD CONSTRAINT uc_customerorderitementity_product_type UNIQUE (product_type_id);

-- changeset lia:1720639199748-17
ALTER TABLE product
    ADD CONSTRAINT uc_productentity_product_type UNIQUE (product_type_id);

-- changeset lia:1720639199748-18
ALTER TABLE shop_inventory
    ADD CONSTRAINT uc_shopinventoryentity_inventory_type UNIQUE (inventory_type_id);

-- changeset lia:1720639199748-19
ALTER TABLE shop_inventory
    ADD CONSTRAINT uc_shopinventoryentity_shop UNIQUE (shop_id);

-- changeset lia:1720639199748-21
ALTER TABLE customer_order
    ADD CONSTRAINT FK_CUSTOMERORDERENTITY_ON_SHOP FOREIGN KEY (shop_id) REFERENCES shop (id);

-- changeset lia:1720639199748-22
ALTER TABLE customer_order_item
    ADD CONSTRAINT FK_CUSTOMERORDERITEMENTITY_ON_ORDER FOREIGN KEY (order_id) REFERENCES customer_order (id);

-- changeset lia:1720639199748-23
ALTER TABLE customer_order_item
    ADD CONSTRAINT FK_CUSTOMERORDERITEMENTITY_ON_PRODUCT_TYPE FOREIGN KEY (product_type_id) REFERENCES product_type (id);

-- changeset lia:1720639199748-24
ALTER TABLE product
    ADD CONSTRAINT FK_PRODUCTENTITY_ON_PRODUCT_TYPE FOREIGN KEY (product_type_id) REFERENCES product_type (id);

-- changeset lia:1720639199748-25
ALTER TABLE product_inventory
    ADD CONSTRAINT FK_PRODUCTINVENTORYENTITY_ON_ORDER FOREIGN KEY (order_id) REFERENCES customer_order (id);

-- changeset lia:1720639199748-26
ALTER TABLE product_inventory
    ADD CONSTRAINT FK_PRODUCTINVENTORYENTITY_ON_PRODUCT FOREIGN KEY (product_id) REFERENCES product (id);

-- changeset lia:1720639199748-27
ALTER TABLE shop_inventory
    ADD CONSTRAINT FK_SHOPINVENTORYENTITY_ON_INVENTORY_TYPE FOREIGN KEY (inventory_type_id) REFERENCES inventory_type (id);

-- changeset lia:1720639199748-28
ALTER TABLE shop_inventory
    ADD CONSTRAINT FK_SHOPINVENTORYENTITY_ON_SHOP FOREIGN KEY (shop_id) REFERENCES shop (id);

