package com.slow3586.drinkshop.mainservice.shoptype;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/shoptype")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public class ShopTypeService {
    ShopTypeRepository shopTypeRepository;
    ShopTypeMapper shopTypeMapper;
}
