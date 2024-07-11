package com.slow3586.drinkshop.mainservice.customerorderitem;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/customerorderitem")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public class CustomerOrderItemService {
    CustomerOrderItemRepository customerOrderItemRepository;
    CustomerOrderItemMapper customerOrderItemMapper;
}
