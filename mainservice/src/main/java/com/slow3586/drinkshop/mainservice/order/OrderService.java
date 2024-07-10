package com.slow3586.drinkshop.mainservice.order;

import com.slow3586.drinkshop.api.OrderDto;
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
public class OrderService {
    OrderRepository orderRepository;
    OrderMapper orderMapper;

    @GetMapping
    public List<OrderDto> getOrders() {
        return orderRepository.findAll()
            .stream()
            .map(orderMapper::toDto)
            .toList();
    }

    @PostMapping
    public OrderDto create(OrderDto orderDto) {
        OrderEntity save = orderRepository.save(OrderEntity.builder()
            .userId(UUID.fromString(orderDto.getPhoneNumber()))
            .createdAt(Instant.now())
            .status("NEW")
            .product("tea")
            .build());

        return orderMapper.toDto(save);
    }
}
