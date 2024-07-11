package com.slow3586.drinkshop.mainservice.telegrampublish;

import com.slow3586.drinkshop.api.TelegramBotClient;
import com.slow3586.drinkshop.api.TelegramBotPublishRequest;
import com.slow3586.drinkshop.api.TelegramPublishEntityDto;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Collections;

@RestController
@RequestMapping("api/telegrampublish")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public class TelegramPublishService {
    TelegramPublishRepository telegramPublishRepository;
    TelegramPublishMapper telegramPublishMapper;
    TelegramBotClient telegramBotClient;

    public void save(TelegramPublishEntityDto telegramPublishEntityDto) {
        telegramPublishRepository.save(
            TelegramPublishEntity.builder()
                .customerId(telegramPublishEntityDto.getCustomerId())
                .text(telegramPublishEntityDto.getText())
                .build());
    }

    @Scheduled(fixedRate = 1000)
    public void scheduled() {
        telegramPublishRepository.findToSend()
            .forEach(entity -> {
                try {
                    telegramBotClient.publish(
                        TelegramBotPublishRequest.builder()
                            .chatIds(Collections.singletonList(entity.getCustomerId()))
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
