package com.slow3586.drinkshop.utilservice.service;

import com.slow3586.drinkshop.api.mainservice.client.CustomerClient;
import com.slow3586.drinkshop.api.mainservice.dto.GetQrCodeResponse;
import com.slow3586.drinkshop.api.mainservice.dto.TelegramProcessResponse;
import com.slow3586.drinkshop.api.mainservice.entity.Customer;
import com.slow3586.drinkshop.api.mainservice.entity.Order;
import com.slow3586.drinkshop.api.mainservice.entity.Promo;
import com.slow3586.drinkshop.api.mainservice.entity.TelegramPublish;
import com.slow3586.drinkshop.api.mainservice.entity.TelegramPublishEntry;
import com.slow3586.drinkshop.api.mainservice.topic.CustomerTelegramTopics;
import com.slow3586.drinkshop.api.mainservice.topic.OrderTopics;
import com.slow3586.drinkshop.api.mainservice.topic.PromoTopics;
import com.slow3586.drinkshop.api.telegrambot.TelegramBotClient;
import com.slow3586.drinkshop.api.telegrambot.TelegramProcessRequest;
import com.slow3586.drinkshop.utilservice.repository.TelegramPublishRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public class TelegramCustomerService {
    static String GET_ALL_DEALS = "Акции";
    static String GET_MY_VIRTUAL_CARD = "Виртуальная карта";
    static String GET_MY_POINTS = "Баллы";
    TelegramPublishRepository telegramPublishRepository;
    TelegramBotClient telegramBotClient;
    KafkaTemplate<UUID, Object> kafkaTemplate;
    CustomerClient customerClient;

    @KafkaListener(topics = CustomerTelegramTopics.PROCESS_REQUEST)
    public TelegramProcessResponse process(@RequestBody TelegramProcessRequest request) {
        final TelegramProcessResponse response = new TelegramProcessResponse();

        final String telegramUserId = request.getTelegramId();
        final String messageText = request.getText();

        Customer customer = customerClient.findOrCreateByTelegramId(telegramUserId);

        //region REGISTRATION
        if (customer.getPhoneNumber() == null) {
            if (request.getPhone() != null) {
                final String phoneNumber = request.getPhone();
                final String name = request.getName();

                try {
                    customer = customerClient.updateContact(customer
                        .setPhoneNumber(phoneNumber)
                        .setName(name));
                    response.setSendText("Приятно познакомиться, " + name + "! Регистрация прошла успешно.");
                } catch (Exception e) {
                    response.setSendText("Извини, но для использования приложения необходим российский номер телефона.");
                }
            } else {
                response.setSendText("Привет! Для завершения регистрации необходимо поделиться своим контактом.");
                response.getTags().add("REQUEST_CONTACT");
            }
        }
        //endregion

        //region LOGIC
        if (customer.getPhoneNumber() != null && customer.getBlockedReason() == null) {
            if (GET_MY_VIRTUAL_CARD.equals(messageText)) {
                GetQrCodeResponse qrCode = customerClient.getQrCode(customer.getTelegramId());

                response.setSendImageName("qrcode.png");
                response.setSendImageBytes(qrCode.getImage());
                response.setSendText("Временный код - "
                    + qrCode.getCode().substring(0, 2)
                    + " " + qrCode.getCode().substring(2, 4)
                    + " " + qrCode.getCode().substring(4, 6));
            } else if (GET_MY_POINTS.equals(messageText)) {
                double points = customer.getPoints();
                if (points == 0) {
                    response.setSendText("У тебя пока ещё нет баллов.");
                } else {
                    response.setSendText("Твои баллы - " + points);
                }
            } else if (GET_ALL_DEALS.equals(messageText)) {
                response.setSendText("В данный момент нет акций.");
            } else if ("/start".equals(messageText)) {
                response.setSendText("Добро пожаловать обратно, " + customer.getName() + "!");
            }

            if (response.getSendText() == null) {
                response.setSendText("Перевожу тебя в главное меню.");
            }
            response.setSendTextKeyboard(List.of(List.of(
                GET_MY_VIRTUAL_CARD, GET_MY_POINTS, GET_ALL_DEALS)));
        }
        //endregion

        return response;
    }

    @KafkaListener(topics = OrderTopics.Transaction.PAYMENT, errorHandler = "loggingKafkaListenerErrorHandler")
    public void processOrderCreated(Order order) {
        if (order.getCustomer() == null) {
            log.info("Order {}: No customer", order.getId());
            return;
        }

        TelegramPublish telegramPublish = this.create(
            new TelegramPublish()
                .setCustomerId(order.getCustomerId())
                .setText("Оформлен заказ в магазине " + order.getShop().getName()
                    + ", " + order.getShop().getLocation() + ":\n"
                    + order.getOrderItemList().stream().map((t) ->
                        t.getProduct().getName()
                            + " - "
                            + t.getQuantity()
                            + "шт. - "
                            + t.getQuantity() * t.getProduct().getPrice()
                            + "Р")
                    .collect(Collectors.joining("\n"))
                    + "\nИтого: " + order.getPayment().getValue() + "Р\n"
                    + (order.getPayment().getPoints() > 0
                    ? ("Оплачено баллами: " + order.getPayment().getPoints()
                    + "/"
                    + order.getPayment().getValue() + order.getPayment().getPoints())
                    : "")));

        kafkaTemplate.send(OrderTopics.Transaction.PUBLISH,
            order.getId(),
            order.setTelegramPublishCreated(telegramPublish));
    }

    @KafkaListener(topics = OrderTopics.Transaction.COMPLETED, errorHandler = "loggingKafkaListenerErrorHandler")
    public void processOrderReady(Order order) {
        if (order.getCustomer() == null) {
            log.info("Order {}: No customer", order.getId());
            return;
        }

        TelegramPublish telegramPublish = this.create(
            new TelegramPublish()
                .setCustomerId(order.getCustomerId())
                .setText("Заказ готов."));

        kafkaTemplate.send(OrderTopics.Transaction.PUBLISH,
            order.getId(),
            order.setTelegramPublishCreated(telegramPublish));
    }

    @KafkaListener(topics = PromoTopics.Transaction.CREATED, errorHandler = "promoTransactionListenerErrorHandler")
    public void processPromo(Promo promo) {
        TelegramPublish telegramPublish = this.create(
            new TelegramPublish()
                .setCustomerGroupId(UUID.randomUUID())
                .setText(promo.getText()));

        kafkaTemplate.send(PromoTopics.Transaction.PUBLISH,
            promo.getId(),
            promo.setTelegramPublish(telegramPublish));
    }

    @Transactional(transactionManager = "kafkaTransactionManager")
    @KafkaListener(topics = CustomerTelegramTopics.Transaction.WITH_CUSTOMERS)
    public void processPublishCreated(TelegramPublish telegramPublish) {
        try {
            telegramPublish.getCustomerList().forEach(customer -> {
                TelegramPublishEntry telegramPublishEntry = new TelegramPublishEntry()
                    .setText(telegramPublish.getText())
                    .setTelegramId(customer.getTelegramId())
                    .setTelegramPublishId(telegramPublish.getId());

                kafkaTemplate.send(CustomerTelegramTopics.Transaction.ENTRY,
                    telegramPublish.getId(),
                    telegramPublishEntry);
            });
        } catch (Exception e) {
            log.error("Error processing publish created", e);
        }
    }

    protected TelegramPublish create(TelegramPublish telegramPublish) {
        telegramPublish = telegramPublishRepository.save(
            telegramPublish.setStatus("CREATED"));

        kafkaTemplate.send(CustomerTelegramTopics.Transaction.CREATED,
            telegramPublish.getId(),
            telegramPublish);

        return telegramPublish;
    }

}
