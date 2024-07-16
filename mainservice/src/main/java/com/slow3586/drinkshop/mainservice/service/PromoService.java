package com.slow3586.drinkshop.mainservice.service;


import com.slow3586.drinkshop.api.mainservice.PromoRequest;
import com.slow3586.drinkshop.api.mainservice.entity.Promo;
import com.slow3586.drinkshop.api.mainservice.entity.TelegramPublish;
import com.slow3586.drinkshop.mainservice.repository.PromoRepository;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.annotation.Secured;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("api/promo")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public class PromoService {
    PromoRepository promoRepository;

    @Transactional
    @PostMapping("create")
    @Secured({"ADMIN"})
    public UUID create(@Valid @RequestBody PromoRequest promoRequest) {
        Promo promo = promoRepository.save(
            new Promo()
                .setName(promoRequest.getName())
                .setCode(promoRequest.getCode())
                .setText(promoRequest.getText())
                .setImage(promoRequest.getImage())
                .setStartsAt(promoRequest.getStartsAt())
                .setEndsAt(promoRequest.getEndsAt()));

        telegramPublishRepository.saveAll(
            customerRepository.findValidForPromos()
                .map(c -> new TelegramPublish()
                    .setTelegramId(c.getTelegramId())
                    .setText(promo.getText())));

        return promo.getId();
    }
}
