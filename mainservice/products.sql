INSERT INTO product_type(name)
VALUES ('Горячий Шоколад'),
       ('Капучино'),
       ('Латте Макиато'),
       ('Английский Завтрак'),
       ('Чарльз Эрл Грей'),
       ('Какао Классический'),
       ('Какао Солёная Карамель'),
       ('Айс Крим Латте Классический'),
       ('Айс Крим Латте Солёная Карамель'),
       ('Милкшейк Классический');

INSERT INTO product(product_type_id, label, price)
VALUES ((select id from product_type t where t.name = 'Горячий Шоколад'), '200мл', 250),
       ((select id from product_type t where t.name = 'Капучино'), '350мл', 350),
       ((select id from product_type t where t.name = 'Капучино'), '450мл', 250),
       ((select id from product_type t where t.name = 'Латте Макиато'), '350мл', 350),
       ((select id from product_type t where t.name = 'Латте Макиато'), '450мл', 500),
       ((select id from product_type t where t.name = 'Английский Завтрак'), '350мл', 500),
       ((select id from product_type t where t.name = 'Английский Завтрак'), '450мл', 500),
       ((select id from product_type t where t.name = 'Чарльз Эрл Грей'), '350мл', 500),
       ((select id from product_type t where t.name = 'Чарльз Эрл Грей'), '450мл', 500),
       ((select id from product_type t where t.name = 'Какао Классический'), '350мл', 500),
       ((select id from product_type t where t.name = 'Какао Классический'), '450мл', 500),
       ((select id from product_type t where t.name = 'Какао Солёная Карамель'), '350мл', 500),
       ((select id from product_type t where t.name = 'Какао Солёная Карамель'), '450мл', 500),
       ((select id from product_type t where t.name = 'Айс Крим Латте Классический'), '350мл', 500),
       ((select id from product_type t where t.name = 'Айс Крим Латте Классический'), '450мл', 500),
       ((select id from product_type t where t.name = 'Айс Крим Латте Солёная Карамель'), '350мл', 500),
       ((select id from product_type t where t.name = 'Айс Крим Латте Солёная Карамель'), '450мл', 500),
       ((select id from product_type t where t.name = 'Милкшейк Классический'), '350мл', 500),
       ((select id from product_type t where t.name = 'Милкшейк Классический'), '450мл', 500);

INSERT INTO product_inventory_type(name)
VALUES ('Молоко'),
       ('Чай'),
       ('Шоколад');

INSERT INTO product_inventory(product_id, inventory_id, quantity)
VALUES ((select id from product t where t.product_type_id = (select id from product_type t where t.name = 'Горячий Шоколад') and t.label = '200мл'), (select id from product_inventory_type t where t.name = 'Молоко'), 100),
       ((select id from product t where t.product_type_id = (select id from product_type t where t.name = 'Горячий Шоколад') and t.label = '200мл'), (select id from product_inventory_type t where t.name = 'Чай'), 100),
       ((select id from product t where t.product_type_id = (select id from product_type t where t.name = 'Капучино') and t.label = '350мл'), (select id from product_inventory_type t where t.name = 'Молоко'), 100),
       ((select id from product t where t.product_type_id = (select id from product_type t where t.name = 'Капучино') and t.label = '450мл'), (select id from product_inventory_type t where t.name = 'Молоко'), 100),
       ((select id from product t where t.product_type_id = (select id from product_type t where t.name = 'Латте Макиато') and t.label = '350мл'), (select id from product_inventory_type t where t.name = 'Молоко'), 100),
       ((select id from product t where t.product_type_id = (select id from product_type t where t.name = 'Латте Макиато') and t.label = '450мл'), (select id from product_inventory_type t where t.name = 'Молоко'), 100),
       ((select id from product t where t.product_type_id = (select id from product_type t where t.name = 'Английский Завтрак') and t.label = '350мл'), (select id from product_inventory_type t where t.name = 'Молоко'), 100),
       ((select id from product t where t.product_type_id = (select id from product_type t where t.name = 'Английский Завтрак') and t.label = '450мл'), (select id from product_inventory_type t where t.name = 'Молоко'), 100),
       ((select id from product t where t.product_type_id = (select id from product_type t where t.name = 'Чарльз Эрл Грей') and t.label = '350мл'), (select id from product_inventory_type t where t.name = 'Молоко'), 100),
       ((select id from product t where t.product_type_id = (select id from product_type t where t.name = 'Чарльз Эрл Грей') and t.label = '450мл'), (select id from product_inventory_type t where t.name = 'Молоко'), 100),
       ((select id from product t where t.product_type_id = (select id from product_type t where t.name = 'Какао Классический') and t.label = '350мл'), (select id from product_inventory_type t where t.name = 'Молоко'), 100),
       ((select id from product t where t.product_type_id = (select id from product_type t where t.name = 'Какао Классический') and t.label = '450мл'), (select id from product_inventory_type t where t.name = 'Молоко'), 100),
       ((select id from product t where t.product_type_id = (select id from product_type t where t.name = 'Какао Солёная Карамель') and t.label = '350мл'), (select id from product_inventory_type t where t.name = 'Молоко'), 100),
       ((select id from product t where t.product_type_id = (select id from product_type t where t.name = 'Какао Солёная Карамель') and t.label = '450мл'), (select id from product_inventory_type t where t.name = 'Молоко'), 100),
       ((select id from product t where t.product_type_id = (select id from product_type t where t.name = 'Айс Крим Латте Классический') and t.label = '350мл'), (select id from product_inventory_type t where t.name = 'Молоко'), 100),
       ((select id from product t where t.product_type_id = (select id from product_type t where t.name = 'Айс Крим Латте Классический') and t.label = '450мл'), (select id from product_inventory_type t where t.name = 'Молоко'), 100),
       ((select id from product t where t.product_type_id = (select id from product_type t where t.name = 'Айс Крим Латте Солёная Карамель') and t.label = '350мл'), (select id from product_inventory_type t where t.name = 'Молоко'), 100),
       ((select id from product t where t.product_type_id = (select id from product_type t where t.name = 'Айс Крим Латте Солёная Карамель') and t.label = '450мл'), (select id from product_inventory_type t where t.name = 'Молоко'), 100),
       ((select id from product t where t.product_type_id = (select id from product_type t where t.name = 'Милкшейк Классический') and t.label = '350мл'), (select id from product_inventory_type t where t.name = 'Молоко'), 100),
       ((select id from product t where t.product_type_id = (select id from product_type t where t.name = 'Милкшейк Классический') and t.label = '450мл'), (select id from product_inventory_type t where t.name = 'Молоко'), 100);

INSERT INTO shop(name, location, status)
VALUES ('Грин хауc №1', 'Улица №1', 'OK');

INSERT INTO shop_inventory(shop_id, product_inventory_type_id, quantity)
VALUES ((select id from shop t where t.name = 'Грин хауc №1'), (select id from product_inventory_type t where t.name = 'Молоко'), 1000),
    ((select id from shop t where t.name = 'Грин хауc №1'), (select id from product_inventory_type t where t.name = 'Чай'), 1000),
    ((select id from shop t where t.name = 'Грин хауc №1'), (select id from product_inventory_type t where t.name = 'Шоколад'), 1000);