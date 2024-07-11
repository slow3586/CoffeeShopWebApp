package com.slow3586.drinkshop.mainservice.promo;

import com.slow3586.drinkshop.api.PromoEntityDto;
import com.slow3586.drinkshop.api.TelegramBotClient;
import com.slow3586.drinkshop.api.TelegramPublishEntityDto;
import com.slow3586.drinkshop.mainservice.customer.CustomerEntity;
import com.slow3586.drinkshop.mainservice.customer.CustomerRepository;
import com.slow3586.drinkshop.mainservice.telegrampublish.TelegramPublishService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/promo")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public class PromoService {
    PromoRepository promoRepository;
    PromoMapper promoMapper;
    TelegramBotClient telegramBotClient;
    CustomerRepository customerRepository;
    JdbcTemplate jdbcTemplate;
    TelegramPublishService telegramPublishService;

    @GetMapping
    public List<PromoEntityDto> findAll() {
        return promoRepository.findAll()
            .stream()
            .map(promoMapper::toDto)
            .toList();
    }

    @PostMapping("save")
    public void save(PromoEntityDto promoEntityDto) {
        PromoEntity promoEntity = promoRepository.save(promoMapper.toEntity(promoEntityDto));
    }

    @Scheduled(cron = "*/1 * * * *")
    public void scheduled() {
        promoRepository.findToSend()
            .forEach(promoEntity -> {
                List<CustomerEntity> customerEntities = customerRepository.findAll();

                customerEntities.forEach(customer ->
                    telegramPublishService.save(
                        TelegramPublishEntityDto.builder()
                            .text(promoEntity.getText())
                            .customerId(customer.getTelegramId())
                            .build()));
            });
    }
}
