package com.slow3586.drinkshop.mainservice.telegrampublish;

import com.slow3586.drinkshop.api.TelegramBotClient;
import com.slow3586.drinkshop.api.TelegramBotPublishRequest;
import com.slow3586.drinkshop.api.TelegramPublishServicePublishRequest;
import com.slow3586.drinkshop.mainservice.customer.CustomerRepository;
import com.slow3586.drinkshop.mainservice.promo.PromoRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("api/telegrampublish")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public class TelegramPublishService {
    TelegramPublishRepository telegramPublishRepository;
    TelegramPublishMapper telegramPublishMapper;
    TelegramBotClient telegramBotClient;
    PromoRepository promoRepository;
    CustomerRepository customerRepository;

    @PostMapping
    public void publish(@RequestBody TelegramPublishServicePublishRequest request) {
        telegramPublishRepository.saveAll(
            request.getChatIds().stream()
                .map(c -> TelegramPublishEntity.builder()
                    .telegramId(c)
                    .text(request.getText())
                    .attempts(0)
                    .build())
                .toList());
    }

    @Scheduled(fixedRate = 1000)
    @Async
    public void sendPublishRequestsToBot() {
        telegramPublishRepository.findToSend()
            .forEach(entity -> {
                try {
                    telegramBotClient.publish(
                        TelegramBotPublishRequest.builder()
                            .chatIds(List.of(entity.getTelegramId()))
                            .text(entity.getText())
                            .build());
                    entity.setSentAt(Instant.now());
                } catch (Exception e) {
                    entity.setAttempts(entity.getAttempts() + 1);
                    entity.setError(entity.getError() + "; " + e.getMessage());
                    entity.setLastAttemptAt(Instant.now());
                }
                telegramPublishRepository.save(entity);
            });
    }
}
