package com.slow3586.drinkshop.mainservice.shopshift;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/shop")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public class ShopShiftService {
    ShopShiftRepository shopShiftRepository;
    ShopShiftMapper shopShiftMapper;
}
