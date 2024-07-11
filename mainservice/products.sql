INSERT INTO product_type(id, name)
VALUES ("drink_hot_chocolate", "Горячий Шоколад"),
       ("drink_cappuccino", "Капучино"),
       ("drink_latte_makiato", "Латте Макиато"),
       ("drink_english_tea", "Английский Завтрак"),
       ("drink_charles_earl_grey", "Чарльз Эрл Грей"),
       ("drink_cacao_classic", "Какао Классический"),
       ("drink_cacao_caramel", "Какао Солёная Карамель"),
       ("drink_ice_cream_latte_classic", "Айс Крим Латте Классический"),
       ("drink_ice_cream_latte_caramel", "Айс Крим Латте Солёная Карамель"),
       ("drink_milkshake_classic", "Милкшейк Классический");

INSERT INTO product(product_type_id, label, price)
VALUES ("drink_hot_chocolate_200", "200мл", 250),
       ("drink_cappucino_350", "350мл", 350),
       ("drink_cappucino_450", "450мл", 250),
       ("drink_latte_makiato_350", "350мл", 350),
       ("drink_latte_makiato_450", "450мл", 500),
       ("drink_english_tea_350", "350мл", 500),
       ("drink_english_tea_450", "450мл", 500),
       ("drink_charles_earl_grey_350", "350мл", 500),
       ("drink_charles_earl_grey_450", "450мл", 500),
       ("drink_cacao_classic_350", "350мл", 500),
       ("drink_cacao_classic_450", "450мл", 500),
       ("drink_cacao_caramel_350", "350мл", 500),
       ("drink_cacao_caramel_450", "450мл", 500),
       ("drink_ice_cream_latte_classic_350", "350мл", 500),
       ("drink_ice_cream_latte_classic_450", "450мл", 500),
       ("drink_ice_cream_latte_caramel_350", "350мл", 500),
       ("drink_ice_cream_latte_caramel_450", "450мл", 500),
       ("drink_milkshake_classic_350", "350мл", 500),
       ("drink_milkshake_classic_450", "450мл", 500);

INSERT INTO inventory_type(id, name)
VALUES ("milk", "Молоко"),
       ("tea", "Чай"),
       ("chocolate", "Шоколад");

INSERT INTO product_inventory(product_id, inventory_id, quantity)
VALUES ("drink_hot_chocolate_200", "milk", 100),
       ("drink_hot_chocolate_200", "chocolate", 100),
       ("drink_cappucino_350", "milk", 100),
       ("drink_cappucino_450", "milk", 100),
       ("drink_latte_makiato_350", "milk", 100),
       ("drink_latte_makiato_450", "milk", 100),
       ("drink_english_tea_350", "milk", 100),
       ("drink_english_tea_450", "milk", 100),
       ("drink_charles_earl_grey_350", "milk", 100),
       ("drink_charles_earl_grey_450", "milk", 100),
       ("drink_cacao_classic_350", "milk", 100),
       ("drink_cacao_classic_450", "milk", 100),
       ("drink_cacao_caramel_350", "milk", 100),
       ("drink_cacao_caramel_450", "milk", 100),
       ("drink_ice_cream_latte_classic_350", "milk", 100),
       ("drink_ice_cream_latte_classic_450", "milk", 100),
       ("drink_ice_cream_latte_caramel_350", "milk", 100),
       ("drink_ice_cream_latte_caramel_450", "milk", 100),
       ("drink_milkshake_classic_350", "milk", 100),
       ("drink_milkshake_classic_450", "milk", 100);

INSERT INTO shop_type(id, name)
VALUES ("greenhouse", "Грин хаус"),
       ("bubbletea", "Бабл ти");

INSERT INTO shop(id, shop_type_id, name, location, status)
VALUES ("greenhouse_ulica", "greenhouse", "Грин хаус", "Улица", "OK");
