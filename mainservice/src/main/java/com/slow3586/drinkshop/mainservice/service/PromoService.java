package com.slow3586.drinkshop.mainservice.service;


import com.slow3586.drinkshop.api.mainservice.dto.PromoRequest;
import com.slow3586.drinkshop.api.mainservice.entity.Promo;
import com.slow3586.drinkshop.api.mainservice.topic.PromoTopics;
import com.slow3586.drinkshop.mainservice.repository.PromoRepository;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.access.annotation.Secured;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public class PromoService {
    PromoRepository promoRepository;
    KafkaTemplate<UUID, Object> kafkaTemplate;

    @Transactional(transactionManager = "transactionManager")
    @Secured({"ADMIN"})
    @KafkaListener(topics = PromoTopics.CREATE_REQUEST)
    public void create(@Valid @RequestBody PromoRequest promoRequest) {
        Promo promo = promoRepository.save(
            new Promo()
                .setName(promoRequest.getName())
                .setCode(promoRequest.getCode())
                .setText(promoRequest.getText())
                .setImage(promoRequest.getImage())
                .setStartsAt(promoRequest.getStartsAt())
                .setEndsAt(promoRequest.getEndsAt()));

        kafkaTemplate.send( PromoTopics.TRANSACTION_CREATED,
            promo.getId(),
            promo);
    }
}
