package com.slow3586.drinkshop.mainservice.service;

import com.slow3586.drinkshop.api.mainservice.PromoTransaction;
import com.slow3586.drinkshop.api.mainservice.entity.Promo;
import com.slow3586.drinkshop.mainservice.repository.CustomerRepository;
import com.slow3586.drinkshop.mainservice.repository.PromoRepository;
import jakarta.annotation.PostConstruct;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonSerde;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("api/customer")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public class CustomerService {
    CustomerRepository customerRepository;
    KafkaTemplate<UUID, Object> kafkaTemplate;
    PromoRepository promoRepository;
    StreamsBuilder streamsBuilder;
    KStream<String, PromoTransaction> promoTransactionKStream;
    JsonSerde<Object> baseJsonSerde;

    @PostConstruct
    public void promoTransactionStream() {
        streamsBuilder.stream("promo_transaction", Consumed.with(Serdes.String(), baseJsonSerde.copyWithType(PromoTransaction.class)))
            .filter((k, v) -> v != null && v.getValidCustomers() == null)
            .mapValues((k, v) -> new PromoTransaction().setValidCustomers(customerRepository.findValidForPromos()))
            .to("promo_transaction_customer");
    }

    @Bean
    public KafkaAdmin.NewTopics promoTransactionCustomerTopics() {
        return new KafkaAdmin.NewTopics(
            TopicBuilder.name("promo_transaction")
                .compact()
                .build(),
            TopicBuilder.name("promo_transaction_customer")
                .compact()
                .build());
    }
}
