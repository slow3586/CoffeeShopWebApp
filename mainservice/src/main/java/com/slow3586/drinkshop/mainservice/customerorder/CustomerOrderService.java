package com.slow3586.drinkshop.mainservice.customerorder;

import com.slow3586.drinkshop.api.CustomerOrderEntityDto;
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
    public List<CustomerOrderEntityDto> getOrders() {
        return customerOrderRepository.findAll()
            .stream()
            .map(customerOrderMapper::toDto)
            .toList();
    }

    @PostMapping
    public CustomerOrderEntityDto create(CustomerOrderEntityDto customerOrderDto) {
        CustomerOrderEntity save = customerOrderRepository.save(CustomerOrderEntity.builder()
            .customerId(customerOrderDto.getCustomerId())
            .status("NEW")
            .build());

        return customerOrderMapper.toDto(save);
    }
}
