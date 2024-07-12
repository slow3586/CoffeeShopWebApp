package com.slow3586.drinkshop.mainservice.service;

import com.slow3586.drinkshop.api.mainservice.PromoTransaction;
import com.slow3586.drinkshop.api.mainservice.entity.Promo;
import com.slow3586.drinkshop.mainservice.repository.CustomerRepository;
import com.slow3586.drinkshop.mainservice.repository.PromoRepository;
import jakarta.annotation.PostConstruct;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
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

    @PostConstruct
    public void promoTransactionStream() {
        promoTransactionKStream
            .filter((k, v) -> v != null && v.getValidCustomers() == null)
            .mapValues((k, v) -> v.setValidCustomers(customerRepository.findValidForPromos()));
    }
}
