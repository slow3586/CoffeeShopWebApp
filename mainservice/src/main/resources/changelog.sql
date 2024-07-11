-- liquibase formatted sql

-- changeset lia:1720702806986-1
CREATE TABLE customer (id UUID NOT NULL, telegram_id VARCHAR(255), name VARCHAR(255), points DOUBLE PRECISION NOT NULL, phone_number VARCHAR(255), qr_code VARCHAR(255), qr_code_expires_at TIMESTAMP WITHOUT TIME ZONE, blocked_reason VARCHAR(255), created_at TIMESTAMP WITHOUT TIME ZONE, last_modified_at TIMESTAMP WITHOUT TIME ZONE, CONSTRAINT pk_customer PRIMARY KEY (id));

-- changeset lia:1720702806986-2
CREATE TABLE customer_order (id UUID NOT NULL, customer_id UUID, shop_id UUID, status VARCHAR(255), rating INTEGER NOT NULL, created_at TIMESTAMP WITHOUT TIME ZONE, last_modified_at TIMESTAMP WITHOUT TIME ZONE, CONSTRAINT pk_customer_order PRIMARY KEY (id));

-- changeset lia:1720702806986-3
CREATE TABLE customer_order_item (id UUID NOT NULL, order_id UUID, product_type_id UUID, quantity INTEGER NOT NULL, created_at TIMESTAMP WITHOUT TIME ZONE, last_modified_at TIMESTAMP WITHOUT TIME ZONE, CONSTRAINT pk_customer_order_item PRIMARY KEY (id));

-- changeset lia:1720702806986-4
CREATE TABLE inventory_type (id VARCHAR(255) NOT NULL, name VARCHAR(255), created_at TIMESTAMP WITHOUT TIME ZONE, last_modified_at TIMESTAMP WITHOUT TIME ZONE, CONSTRAINT pk_inventory_type PRIMARY KEY (id));

-- changeset lia:1720702806986-5
CREATE TABLE product (id VARCHAR(255) NOT NULL, product_type_id VARCHAR(255), label VARCHAR(255), price DOUBLE PRECISION NOT NULL, created_at TIMESTAMP WITHOUT TIME ZONE, last_modified_at TIMESTAMP WITHOUT TIME ZONE, CONSTRAINT pk_product PRIMARY KEY (id));

-- changeset lia:1720702806986-6
CREATE TABLE product_inventory (id UUID NOT NULL, product_id UUID, inventory_id UUID, quantity DOUBLE PRECISION NOT NULL, created_at TIMESTAMP WITHOUT TIME ZONE, last_modified_at TIMESTAMP WITHOUT TIME ZONE, CONSTRAINT pk_product_inventory PRIMARY KEY (id));

-- changeset lia:1720702806986-7
CREATE TABLE product_type (id VARCHAR(255) NOT NULL, name VARCHAR(255), color VARCHAR(255), shop_type_id VARCHAR(255), created_at TIMESTAMP WITHOUT TIME ZONE, last_modified_at TIMESTAMP WITHOUT TIME ZONE, CONSTRAINT pk_product_type PRIMARY KEY (id));

-- changeset lia:1720702806986-8
CREATE TABLE promo (id UUID NOT NULL, code VARCHAR(255), name VARCHAR(255), text VARCHAR(4000), image BYTEA, shop_type_id VARCHAR(255), product_type_id VARCHAR(255), status VARCHAR(255) DEFAULT 'NEW' NOT NULL, starts_at TIMESTAMP WITHOUT TIME ZONE, ends_at TIMESTAMP WITHOUT TIME ZONE, created_at TIMESTAMP WITHOUT TIME ZONE, last_modified_at TIMESTAMP WITHOUT TIME ZONE, CONSTRAINT pk_promo PRIMARY KEY (id));

-- changeset lia:1720702806986-9
CREATE TABLE shop (id UUID NOT NULL, shop_type_id VARCHAR(255), name VARCHAR(255), location VARCHAR(255), status VARCHAR(255), created_at TIMESTAMP WITHOUT TIME ZONE, last_modified_at TIMESTAMP WITHOUT TIME ZONE, CONSTRAINT pk_shop PRIMARY KEY (id));

-- changeset lia:1720702806986-10
CREATE TABLE shop_inventory (id UUID NOT NULL, inventory_type_id UUID, shop_id UUID, created_at TIMESTAMP WITHOUT TIME ZONE, last_modified_at TIMESTAMP WITHOUT TIME ZONE, CONSTRAINT pk_shop_inventory PRIMARY KEY (id));

-- changeset lia:1720702806986-11
CREATE TABLE shop_shift (id UUID NOT NULL, shop_id UUID, worker_id UUID, created_at TIMESTAMP WITHOUT TIME ZONE, last_modified_at TIMESTAMP WITHOUT TIME ZONE, CONSTRAINT pk_shop_shift PRIMARY KEY (id));

-- changeset lia:1720702806986-12
CREATE TABLE shop_type (id VARCHAR(255) NOT NULL, name VARCHAR(255), location VARCHAR(255), status VARCHAR(255), created_at TIMESTAMP WITHOUT TIME ZONE, last_modified_at TIMESTAMP WITHOUT TIME ZONE, CONSTRAINT pk_shop_type PRIMARY KEY (id));

-- changeset lia:1720702806986-13
CREATE TABLE telegram_publish (id UUID NOT NULL, telegram_id VARCHAR(255), text VARCHAR(4000), sent_at TIMESTAMP WITHOUT TIME ZONE, attempts INTEGER DEFAULT 0 NOT NULL, error VARCHAR(4000), last_attempt_at TIMESTAMP WITHOUT TIME ZONE, created_at TIMESTAMP WITHOUT TIME ZONE, last_modified_at TIMESTAMP WITHOUT TIME ZONE, CONSTRAINT pk_telegram_publish PRIMARY KEY (id));

-- changeset lia:1720702806986-14
CREATE TABLE worker (id UUID NOT NULL, name VARCHAR(255), status VARCHAR(255), created_at TIMESTAMP WITHOUT TIME ZONE, last_modified_at TIMESTAMP WITHOUT TIME ZONE, CONSTRAINT pk_worker PRIMARY KEY (id));

