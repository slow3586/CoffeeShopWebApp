package com.slow3586.drinkshop.mainservice.service;


import com.slow3586.drinkshop.api.mainservice.PromoRequest;
import com.slow3586.drinkshop.api.mainservice.PromoTransaction;
import com.slow3586.drinkshop.api.mainservice.entity.Promo;
import com.slow3586.drinkshop.mainservice.repository.CustomerRepository;
import com.slow3586.drinkshop.mainservice.repository.PromoRepository;
import com.slow3586.drinkshop.mainservice.repository.TelegramPublishRepository;
import jakarta.ws.rs.QueryParam;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.streams.StreamsBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaTemplate;
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
@RequestMapping("api/admin")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public class AdminService {
    CustomerRepository customerRepository;
    PromoRepository promoRepository;
    TelegramPublishRepository telegramPublishRepository;
    KafkaTemplate<UUID, Object> kafkaTemplate;
    StreamsBuilder streamsBuilder;

    @GetMapping("get_promos")
    public List<Promo> getPromoList(@QueryParam("page") Integer page) {
        return promoRepository.getPromoList(Optional.ofNullable(page).orElse(0) * 10);
    }

    @Transactional(transactionManager = "kafkaTransactionManager")
    @PostMapping("create_promo")
    public void createPromo(@RequestBody @NonNull PromoRequest promoRequest) {
        kafkaTemplate.send(
            "promo_transaction",
            UUID.randomUUID(),
            new PromoTransaction().setPromoRequest(promoRequest));
    }

    @Bean
    public NewTopic promoTransactionTopic() {
        return TopicBuilder.name("promo_transaction")
            .replicas(1)
            .partitions(1)
            .compact()
            .build();
    }
}
