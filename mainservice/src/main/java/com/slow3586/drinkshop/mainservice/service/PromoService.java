package com.slow3586.drinkshop.mainservice.service;


import com.slow3586.drinkshop.api.mainservice.PromoRequest;
import com.slow3586.drinkshop.api.mainservice.entity.Promo;
import com.slow3586.drinkshop.api.mainservice.entity.TelegramPublish;
import com.slow3586.drinkshop.mainservice.repository.CustomerRepository;
import com.slow3586.drinkshop.mainservice.repository.PromoRepository;
import com.slow3586.drinkshop.mainservice.repository.TelegramPublishRepository;
import jakarta.validation.Valid;
import jakarta.ws.rs.QueryParam;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("api/promo")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public class PromoService {
    CustomerRepository customerRepository;
    PromoRepository promoRepository;
    TelegramPublishRepository telegramPublishRepository;

    @GetMapping("query")
    public List<Promo> query(@QueryParam("page") Integer page) {
        return promoRepository.query(Optional.ofNullable(page).orElse(0) * 10);
    }

    @PostMapping("create")
    public UUID create(@Valid @RequestBody PromoRequest promoRequest) {
        return promoRepository.save(
            new Promo()
                .name(promoRequest.getName())
                .code(promoRequest.getCode())
                .text(promoRequest.getText())
                .startsAt(promoRequest.getStartsAt())
                .endsAt(promoRequest.getEndsAt())).id();
    }

    @Scheduled(fixedDelay = 1000)
    @Transactional
    @Async
    public void send() {
        promoRepository.findToQueueForTelegram()
            .forEach(promo -> {
                telegramPublishRepository.saveAll(
                    customerRepository.findValidForPromos()
                        .stream()
                        .map(c -> new TelegramPublish()
                            .telegramId(c.telegramId())
                            .text(promo.text()))
                        .toList());
                promoRepository.save(promo.queuedForTelegram(true));
            });
    }
}
