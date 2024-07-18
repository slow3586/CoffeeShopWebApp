package com.slow3586.drinkshop.shopservice;

import com.slow3586.drinkshop.api.DefaultKafkaReplyErrorChecker;
import com.slow3586.drinkshop.api.mainservice.topic.CustomerTelegramTopics;
import com.slow3586.drinkshop.api.mainservice.topic.OrderTopics;
import com.slow3586.drinkshop.api.mainservice.topic.PaymentTopics;
import com.slow3586.drinkshop.api.mainservice.topic.PromoTopics;
import com.slow3586.drinkshop.api.mainservice.topic.WorkerTopics;
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
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.transaction.annotation.EnableTransactionManagement;

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
@EnableMethodSecurity
@EnableScheduling
@EnableAsync
public class ShopServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ShopServiceApplication.class, args);
    }

    @NonFinal
    @Value("${SECRET_KEY}")
    String secretKey;
    DefaultKafkaReplyErrorChecker defaultKafkaReplyErrorChecker;

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
                    OrderTopics.Request.REQUEST_COMPLETED_RESPONSE,
                    OrderTopics.Request.REQUEST_CREATE_RESPONSE);

        container.getContainerProperties().setGroupId("orderservice");

        ReplyingKafkaTemplate<UUID, Object, Object> template = new ReplyingKafkaTemplate<>(
            defaultKafkaProducerFactory,
            container);
        template.setSharedReplyTopic(true);
        template.setDefaultReplyTimeout(Duration.ofSeconds(10));
        template.setReplyErrorChecker(defaultKafkaReplyErrorChecker);

        return template;
    }

    @Bean
    public KafkaAdmin.NewTopics orderTopics() {
        return new KafkaAdmin.NewTopics(Stream.of(
                OrderTopics.Transaction.CREATED,
                OrderTopics.Transaction.PUBLISH,
                OrderTopics.Transaction.CUSTOMER,
                OrderTopics.Transaction.SHOP,
                OrderTopics.Transaction.INVENTORY,
                OrderTopics.Transaction.PRODUCT,
                OrderTopics.Transaction.PAYMENT,
                OrderTopics.Transaction.PAID,
                OrderTopics.Transaction.COMPLETED,
                OrderTopics.Transaction.ERROR,
                OrderTopics.Request.REQUEST_CREATE,
                OrderTopics.Request.REQUEST_COMPLETED,
                OrderTopics.Request.REQUEST_CANCEL,
                OrderTopics.Request.REQUEST_CREATE_RESPONSE,
                OrderTopics.Request.REQUEST_COMPLETED_RESPONSE,
                OrderTopics.Request.REQUEST_CANCEL_RESPONSE,
                OrderTopics.Status.STATUS_COMPLETED,
                OrderTopics.Status.STATUS_CANCELLED
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
                PromoTopics.Transaction.CREATED
            ).map(t -> TopicBuilder.name(t).build())
            .toList()
            .toArray(new NewTopic[0]));
    }

    @Bean
    public KafkaAdmin.NewTopics telegramTopics() {
        return new KafkaAdmin.NewTopics(Stream.of(
                CustomerTelegramTopics.PROCESS_REQUEST,
                CustomerTelegramTopics.PROCESS_RESPONSE,
                CustomerTelegramTopics.Transaction.WITH_CUSTOMERS,
                CustomerTelegramTopics.PUBLISH_REQUEST,
                CustomerTelegramTopics.Transaction.CREATED
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
