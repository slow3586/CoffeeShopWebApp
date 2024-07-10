package com.slow3586.drinkshop.mainservice.customerorder;

import com.slow3586.drinkshop.api.CustomerOrderDto;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/order")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public class CustomerOrderService {
    CustomerOrderRepository customerOrderRepository;
    CustomerOrderMapper customerOrderMapper;

    @GetMapping
    public List<CustomerOrderDto> getOrders() {
        return customerOrderRepository.findAll()
            .stream()
            .map(customerOrderMapper::toDto)
            .toList();
    }

    @PostMapping
    public CustomerOrderDto create(CustomerOrderDto customerOrderDto) {
        CustomerOrderEntity save = customerOrderRepository.save(CustomerOrderEntity.builder()
            .userId(UUID.fromString(customerOrderDto.getPhoneNumber()))
            .createdAt(Instant.now())
            .status("NEW")
            .product("tea")
            .build());

        return customerOrderMapper.toDto(save);
    }
}
