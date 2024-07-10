package com.slow3586.drinkshop.mainservice.inventorytype;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/inventorytype")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public class InventoryTypeService {
    InventoryTypeRepository inventoryTypeRepository;
    InventoryTypeMapper inventoryTypeMapper;
}
