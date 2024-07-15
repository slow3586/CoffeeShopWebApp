package com.slow3586.drinkshop.mainservice.service;

import com.slow3586.drinkshop.api.mainservice.TelegramProcessResponse;
import com.slow3586.drinkshop.api.mainservice.entity.Customer;
import com.slow3586.drinkshop.api.telegrambot.TelegramBotClient;
import com.slow3586.drinkshop.api.telegrambot.TelegramBotPublishRequest;
import com.slow3586.drinkshop.api.telegrambot.TelegramProcessRequest;
import com.slow3586.drinkshop.mainservice.repository.CustomerRepository;
import com.slow3586.drinkshop.mainservice.repository.PromoRepository;
import com.slow3586.drinkshop.mainservice.repository.TelegramPublishRepository;
import com.slow3586.drinkshop.mainservice.utils.QrCodeUtils;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoField;
import java.util.List;

@RestController
@RequestMapping("api/telegram")
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public class TelegramService {
    static String GET_ALL_DEALS = "Акции";
    static String GET_MY_VIRTUAL_CARD = "Виртуальная карта";
    static String GET_MY_POINTS = "Баллы";
    CustomerRepository customerRepository;
    TelegramPublishRepository telegramPublishRepository;
    TelegramBotClient telegramBotClient;
    QrCodeUtils qrCodeUtils;
    PromoRepository promoRepository;

    @PostMapping
    @Secured({"SYSTEM"})
    public TelegramProcessResponse process(@RequestBody TelegramProcessRequest request) {
        final TelegramProcessResponse response = new TelegramProcessResponse();

        final String telegramUserId = request.getCustomerId();
        final String messageText = request.getText();

        Customer customer = customerRepository.getByTelegramId(telegramUserId);

        //region NEW
        if (customer == null) {
            customer = customerRepository.save(
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

                customer = customerRepository.save(customer);
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

    @Scheduled(fixedRate = 1000)
    @Async
    public void sendPublishRequestsToBot() {
        telegramPublishRepository.findToSend()
            .forEach(entity -> {
                try {
                    telegramBotClient.publish(
                        TelegramBotPublishRequest.builder()
                            .chatIds(List.of(entity.getTelegramId()))
                            .text(entity.getText())
                            .build());
                    entity.setSentAt(Instant.now());
                } catch (Exception e) {
                    entity.setAttempts(entity.getAttempts() + 1);
                    entity.setError(entity.getError() + "; " + e.getMessage());
                    entity.setLastAttemptAt(Instant.now());
                }
                telegramPublishRepository.save(entity);
            });
    }

    protected GetQrCodeResponse getQrCode(Customer customer) {
        if (customer.getQrCode() == null || customer.getQrCodeExpiresAt().isBefore(Instant.now())) {
            final String code = String.valueOf(
                    LocalTime.now(ZoneId.of("UTC"))
                        .get(ChronoField.MILLI_OF_DAY) + 10_000_000)
                .substring(0, 6);

            customer.setQrCode(code);
            customer.setQrCodeExpiresAt(Instant.now().plusSeconds(300));
            customer = customerRepository.save(customer);
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
