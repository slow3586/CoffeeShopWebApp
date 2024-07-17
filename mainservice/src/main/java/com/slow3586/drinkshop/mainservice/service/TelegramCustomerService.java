package com.slow3586.drinkshop.mainservice.service;

import com.slow3586.drinkshop.api.mainservice.TelegramPublishMessage;
import com.slow3586.drinkshop.api.mainservice.dto.TelegramProcessResponse;
import com.slow3586.drinkshop.api.mainservice.entity.Customer;
import com.slow3586.drinkshop.api.mainservice.entity.Order;
import com.slow3586.drinkshop.api.mainservice.entity.Promo;
import com.slow3586.drinkshop.api.mainservice.entity.TelegramPublish;
import com.slow3586.drinkshop.api.mainservice.topic.OrderTopics;
import com.slow3586.drinkshop.api.mainservice.topic.PromoTopics;
import com.slow3586.drinkshop.api.mainservice.topic.TelegramTopics;
import com.slow3586.drinkshop.api.telegrambot.TelegramBotClient;
import com.slow3586.drinkshop.api.telegrambot.TelegramBotPublishRequest;
import com.slow3586.drinkshop.api.telegrambot.TelegramProcessRequest;
import com.slow3586.drinkshop.mainservice.repository.CustomerRepository;
import com.slow3586.drinkshop.mainservice.repository.TelegramPublishRepository;
import com.slow3586.drinkshop.mainservice.utils.QrCodeUtils;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoField;
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
    QrCodeUtils qrCodeUtils;
    KafkaTemplate<UUID, Object> kafkaTemplate;
    CustomerService customerService;
    CustomerRepository customerRepository;

    @KafkaListener(topics = TelegramTopics.CUSTOMER_PROCESS_REQUEST)
    public TelegramProcessResponse process(@RequestBody TelegramProcessRequest request) {
        final TelegramProcessResponse response = new TelegramProcessResponse();

        final String telegramUserId = request.getTelegramId();
        final String messageText = request.getText();

        Customer customer = customerService.findByTelegramId(telegramUserId);

        //region NEW
        if (customer == null) {
            customer = customerService.save(
                new Customer()
                    .setTelegramId(telegramUserId));
        }
        //endregion

        //region REGISTRATION
        if (customer.getPhoneNumber() == null) {
            if (request.getPhone() != null) {
                final String phoneNumber = request.getPhone();
                final String name = request.getName();
                customer.setPhoneNumber(phoneNumber);
                customer.setName(name);

                if (phoneNumber.startsWith("+7") && phoneNumber.length() == 12) {
                    response.setSendText("Приятно познакомиться, " + name + "! Регистрация прошла успешно.");
                } else {
                    customer.setBlockedReason("BAD_PHONE_NUMBER");
                    response.setSendText("Извини, но для использования приложения необходим российский номер телефона.");
                }

                customer = customerService.save(customer);
            } else {
                response.setSendText("Привет! Для завершения регистрации необходимо поделиться своим контактом.");
                response.getTags().add("REQUEST_CONTACT");
            }
        }
        //endregion

        //region LOGIC
        if (customer.getPhoneNumber() != null && customer.getBlockedReason() == null) {
            if (GET_MY_VIRTUAL_CARD.equals(messageText)) {
                GetQrCodeResponse qrCode = this.getQrCode(customer);

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

    @KafkaListener(topics = TelegramTopics.CUSTOMER_PUBLISH_REQUEST)
    public void processPublishRequest(TelegramBotPublishRequest publishRequest) {
        TelegramPublish created = telegramPublishRepository.save(new TelegramPublish()
            .setText(publishRequest.getText())
            .setCustomerId(publishRequest.getCustomerId())
            //.setCustomerGroupId(publishRequest.getCustomerGroupId())
            .setStatus("CREATED"));

        kafkaTemplate.send(TelegramTopics.CUSTOMER_PUBLISH_CREATED,
            created.getId(),
            created);
    }

    @KafkaListener(topics = OrderTopics.TRANSACTION_PAYMENT)
    public void processOrder(Order order) {
        this.processPublishRequest(new TelegramBotPublishRequest()
            .setCustomerId(order.getCustomerId())
            .setText("Оформлен заказ в магазине " + order.getShop().getName()
                + ", " + order.getShop() + ":\n"
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

        kafkaTemplate.send(OrderTopics.TRANSACTION_PUBLISH,
            order.getId(),
            order);
    }

    @KafkaListener(topics = PromoTopics.TRANSACTION_CREATED)
    public void processPromo(Promo promo) {
        this.processPublishRequest(new TelegramBotPublishRequest()
            .setCustomerGroupId(UUID.randomUUID())
            .setText(promo.getText()));
    }

    @Transactional(transactionManager = "kafkaTransactionManager")
    @KafkaListener(topics = TelegramTopics.CUSTOMER_PUBLISH_CREATED)
    public void processPublishCreated(TelegramPublish telegramPublish) {
        customerRepository.findAll().forEach(customer ->
            kafkaTemplate.send(TelegramTopics.CUSTOMER_PUBLISH_BOT,
                telegramPublish.getId(),
                new TelegramPublishMessage()
                    .setId(UUID.randomUUID())
                    .setText(telegramPublish.getText())
                    .setStatus("CREATED")
                    .setTelegramId(customer.getTelegramId())
                    .setTelegramPublishId(telegramPublish.getId())));
    }

    protected GetQrCodeResponse getQrCode(Customer customer) {
        if (customer.getQrCode() == null || customer.getQrCodeExpiresAt().isBefore(Instant.now())) {
            final String code = String.valueOf(
                    LocalTime.now(ZoneId.of("UTC"))
                        .get(ChronoField.MILLI_OF_DAY) + 10_000_000)
                .substring(0, 6);

            customer.setQrCode(code);
            customer.setQrCodeExpiresAt(Instant.now().plusSeconds(300));
            customer = customerService.save(customer);
        }

        final byte[] image = qrCodeUtils.generateQRCodeImage(customer.getQrCode());

        return GetQrCodeResponse.builder()
            .code(customer.getQrCode())
            .duration(Duration.ofMinutes(5))
            .image(image)
            .build();
    }

    @Data
    @Builder
    public static class GetQrCodeResponse {
        String code;
        byte[] image;
        Duration duration;
    }
}
