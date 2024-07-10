package com.slow3586.drinkshop.mainservice.producttype;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/producttype")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public class ProductTypeService {
    ProductTypeRepository productTypeRepository;
    ProductTypeMapper productTypeMapper;
}
