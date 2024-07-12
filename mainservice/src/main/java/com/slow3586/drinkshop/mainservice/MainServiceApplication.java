package com.slow3586.drinkshop.mainservice;

import com.slow3586.drinkshop.api.mainservice.PromoTransaction;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.support.serializer.JsonSerde;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
@EnableFeignClients(basePackages = "com.slow3586.drinkshop.api")
@EnableScheduling
@EnableKafkaStreams
@EnableKafka
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PROTECTED)
public class MainServiceApplication {
    StreamsBuilder streamsBuilder;

    public static void main(String[] args) {
        SpringApplication.run(MainServiceApplication.class, args);
    }

    @Bean
    public JsonSerde<Object> baseJsonSerde() {
        JsonSerde<Object> jsonSerde = new JsonSerde<>();
        jsonSerde.deserializer().trustedPackages("*");
        return jsonSerde;
    }

    @Bean
    public KStream<String, PromoTransaction> ewt() {
        JsonSerde<PromoTransaction> serde = new JsonSerde<>();
        serde.deserializer().trustedPackages("*");
        return streamsBuilder.stream("promo_transaction", Consumed.with(Serdes.String(), serde));
    }
}
