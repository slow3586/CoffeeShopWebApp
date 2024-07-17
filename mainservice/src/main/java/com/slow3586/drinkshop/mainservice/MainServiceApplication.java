package com.slow3586.drinkshop.mainservice;

import com.slow3586.drinkshop.api.mainservice.KafkaReplyErrorChecker;
import com.slow3586.drinkshop.api.mainservice.topic.OrderTopics;
import com.slow3586.drinkshop.api.mainservice.topic.PaymentTopics;
import com.slow3586.drinkshop.api.mainservice.topic.PromoTopics;
import com.slow3586.drinkshop.api.mainservice.topic.TelegramTopics;
import com.slow3586.drinkshop.api.mainservice.topic.WorkerTopics;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.support.serializer.JsonSerde;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.crypto.SecretKey;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.UUID;
import java.util.stream.Stream;

@SpringBootApplication
@EnableTransactionManagement
@EnableFeignClients(basePackages = "com.slow3586.drinkshop.api")
@ComponentScan(value = {"com.slow3586.drinkshop.*"})
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PROTECTED)
@EnableKafka
@EnableKafkaStreams
@EnableMethodSecurity
public class MainServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(MainServiceApplication.class, args);
    }

    @NonFinal
    @Value("${SECRET_KEY}")
    String secretKey;
    KafkaReplyErrorChecker kafkaReplyErrorChecker;

    @Bean
    public JsonSerde<Object> baseJsonSerde() {
        JsonSerde<Object> jsonSerde = new JsonSerde<>();
        jsonSerde.deserializer().trustedPackages("*");
        return jsonSerde;
    }

    @Bean
    public ReplyingKafkaTemplate<UUID, Object, Object> kafkaReplyingTemplate(
        ConcurrentKafkaListenerContainerFactory<UUID, Object> kafkaListenerContainerFactory,
        DefaultKafkaProducerFactory<UUID, Object> defaultKafkaProducerFactory,
        DefaultKafkaConsumerFactory<UUID, Object> defaultKafkaConsumerFactory
    ) {
        kafkaListenerContainerFactory.setReplyTemplate(
            new KafkaTemplate<>(defaultKafkaProducerFactory));
        kafkaListenerContainerFactory.setConsumerFactory(defaultKafkaConsumerFactory);

        ConcurrentMessageListenerContainer<UUID, Object> container =
            kafkaListenerContainerFactory
                .createContainer(
                    OrderTopics.REQUEST_COMPLETED_RESPONSE,
                    OrderTopics.REQUEST_CREATE_RESPONSE);

        container.getContainerProperties().setGroupId("drinkshop-mainservice");

        ReplyingKafkaTemplate<UUID, Object, Object> template = new ReplyingKafkaTemplate<>(
            defaultKafkaProducerFactory,
            container);
        template.setSharedReplyTopic(true);
        template.setDefaultReplyTimeout(Duration.ofSeconds(10));
        template.setReplyErrorChecker(kafkaReplyErrorChecker);

        return template;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(4, new SecureRandom(new byte[]{1, 2, 3}));
    }

    @Bean
    public SecretKey secretKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
    }

    @Bean
    public KafkaAdmin.NewTopics orderTopics() {
        return new KafkaAdmin.NewTopics(Stream.of(
                OrderTopics.TRANSACTION_CREATED,
                OrderTopics.TRANSACTION_PUBLISH,
                OrderTopics.TRANSACTION_CUSTOMER,
                OrderTopics.TRANSACTION_SHOP,
                OrderTopics.TRANSACTION_INVENTORY,
                OrderTopics.TRANSACTION_PRODUCT,
                OrderTopics.TRANSACTION_PAYMENT,
                OrderTopics.TRANSACTION_PAID,
                OrderTopics.TRANSACTION_COMPLETED,
                OrderTopics.TRANSACTION_ERROR,
                OrderTopics.REQUEST_CREATE,
                OrderTopics.REQUEST_COMPLETED,
                OrderTopics.REQUEST_CANCEL,
                OrderTopics.REQUEST_CREATE_RESPONSE,
                OrderTopics.REQUEST_COMPLETED_RESPONSE,
                OrderTopics.REQUEST_CANCEL_RESPONSE,
                OrderTopics.STATUS_COMPLETED,
                OrderTopics.STATUS_CANCELLED
            ).map(t -> TopicBuilder.name(t).build())
            .toList()
            .toArray(new NewTopic[0]));
    }

    @Bean
    public KafkaAdmin.NewTopics paymentTopics() {
        return new KafkaAdmin.NewTopics(Stream.of(
                PaymentTopics.REQUEST_SYSTEM,
                PaymentTopics.REQUEST_SYSTEM_RESPONSE,
                PaymentTopics.STATUS_PAID
            ).map(t -> TopicBuilder.name(t).build())
            .toList()
            .toArray(new NewTopic[0]));
    }

    @Bean
    public KafkaAdmin.NewTopics promoTopics() {
        return new KafkaAdmin.NewTopics(Stream.of(
                PromoTopics.CREATE_REQUEST,
                PromoTopics.TRANSACTION_CREATED
            ).map(t -> TopicBuilder.name(t).build())
            .toList()
            .toArray(new NewTopic[0]));
    }

    @Bean
    public KafkaAdmin.NewTopics telegramTopics() {
        return new KafkaAdmin.NewTopics(Stream.of(
                TelegramTopics.CUSTOMER_PROCESS_REQUEST,
                TelegramTopics.CUSTOMER_PROCESS_RESPONSE,
                TelegramTopics.CUSTOMER_PUBLISH_BOT,
                TelegramTopics.CUSTOMER_PUBLISH_REQUEST,
                TelegramTopics.CUSTOMER_PUBLISH_CREATED
            ).map(t -> TopicBuilder.name(t).build())
            .toList()
            .toArray(new NewTopic[0]));
    }

    @Bean
    public KafkaAdmin.NewTopics workerTopics() {
        return new KafkaAdmin.NewTopics(Stream.of(
                WorkerTopics.REQUEST_LOGIN,
                WorkerTopics.REQUEST_TOKEN
            ).map(t -> TopicBuilder.name(t).build())
            .toList()
            .toArray(new NewTopic[0]));
    }
}
