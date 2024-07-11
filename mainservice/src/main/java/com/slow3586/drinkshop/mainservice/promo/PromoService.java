package com.slow3586.drinkshop.mainservice.promo;

import com.slow3586.drinkshop.api.PromoEntityDto;
import com.slow3586.drinkshop.api.TelegramBotClient;
import com.slow3586.drinkshop.api.TelegramPublishServicePublishRequest;
import com.slow3586.drinkshop.mainservice.customer.CustomerEntity;
import com.slow3586.drinkshop.mainservice.customer.CustomerRepository;
import com.slow3586.drinkshop.mainservice.telegrampublish.TelegramPublishService;
import jakarta.validation.Valid;
import jakarta.ws.rs.QueryParam;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/promo")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public class PromoService {
    PromoRepository promoRepository;
    PromoMapper promoMapper;
    TelegramBotClient telegramBotClient;
    CustomerRepository customerRepository;
    TelegramPublishService telegramPublishService;
    JdbcTemplate jdbcTemplate;

    @GetMapping
    public List<PromoEntityDto> get(
        @QueryParam("pageNum") int pageNum,
        @QueryParam("pageSize") int pageSize
    ) {
        return promoRepository.findAll(PageRequest.of(pageNum, pageSize))
            .stream()
            .map(promoMapper::toDto)
            .toList();
    }

    @PostMapping("create")
    public UUID create(@RequestBody @Valid PromoEntityDto promoEntityDto) {
        PromoEntity entity = promoMapper.toEntity(promoEntityDto);
        entity.setSentToTelegram(false);

        return promoRepository.save(entity).getId();
    }

    @Scheduled(cron = "*/1 * * * * *")
    @Async
    public void scheduled() {
        promoRepository.findToSend()
            .forEach(promoEntity -> {
                List<CustomerEntity> customerEntities = customerRepository.findAll();

                telegramPublishService.publish(
                    TelegramPublishServicePublishRequest.builder()
                        .chatIds(customerEntities.stream().map(CustomerEntity::getTelegramId).toList())
                        .text(promoEntity.getName() + "! " + promoEntity.getText()
                            + "Акция действует с " + promoEntity.getStartsAt() + " до " + promoEntity.getEndsAt() + ".")
                        .build());

                promoEntity.setSentToTelegram(true);
                promoRepository.save(promoEntity);
            });
    }
}
