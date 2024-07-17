package com.slow3586.drinkshop.api.mainservice;


import com.slow3586.drinkshop.api.mainservice.entity.Order;
import com.slow3586.drinkshop.api.mainservice.topic.OrderTopics;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ConsumerAwareListenerErrorHandler;
import org.springframework.kafka.listener.ListenerExecutionFailedException;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
public class KafkaOrderTransactionErrorHandler implements ConsumerAwareListenerErrorHandler {
    KafkaTemplate<UUID, Order> kafkaTemplate;

    @Override
    public Object handleError(
        Message<?> message,
        ListenerExecutionFailedException exception,
        Consumer<?, ?> consumer
    ) {
        log.error("#handle", exception);
        return MessageBuilder.fromMessage(message)
            .setHeader(KafkaHeaders.TOPIC, OrderTopics.Transaction.ERROR)
            .setHeader(KafkaHeaders.EXCEPTION_FQCN, exception.getCause().getClass().getSimpleName())
            .setHeader(KafkaHeaders.EXCEPTION_MESSAGE, exception.getCause().getMessage())
            .build();
    }
}
