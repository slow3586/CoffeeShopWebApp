package com.slow3586.drinkshop.mainservice.service;

import com.slow3586.drinkshop.api.mainservice.TelegramProcessUpdateResponse;
import com.slow3586.drinkshop.api.mainservice.entity.Customer;
import com.slow3586.drinkshop.api.telegrambot.TelegramBotClient;
import com.slow3586.drinkshop.api.telegrambot.TelegramBotPublishRequest;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.objects.Contact;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoField;
import java.util.List;
import java.util.UUID;

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
    public TelegramProcessUpdateResponse process(@RequestBody Update update) {
        final TelegramProcessUpdateResponse response = new TelegramProcessUpdateResponse();
        if (!update.hasMessage()) {return response;}

        final User telegramUser = update.getMessage().getFrom();
        final String messageText = update.getMessage().getText();

        Customer customer = customerRepository.getByTelegramId(telegramUser.getId().toString());

        //region NEW
        if (customer == null) {
            customer = customerRepository.save(
                new Customer()
                    .telegramId(telegramUser.getId().toString()));
        }
        //endregion

        //region REGISTRATION
        if (customer.phoneNumber() == null) {
            if (update.getMessage().hasContact()) {
                final Contact contact = update.getMessage().getContact();
                final String phoneNumber = contact.getPhoneNumber();
                final String name = contact.getFirstName();
                customer.phoneNumber(phoneNumber);
                customer.name(name);

                if (phoneNumber.startsWith("+7") && phoneNumber.length() == 12) {
                    response.setSendText("Приятно познакомиться, " + name + "! Регистрация прошла успешно.");
                } else {
                    customer.blockedReason("BAD_PHONE_NUMBER");
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
        if (customer.phoneNumber() != null && customer.blockedReason() == null) {
            if (GET_MY_VIRTUAL_CARD.equals(messageText)) {
                GetQrCodeResponse qrCode = this.getQrCode(customer);

                response.setSendImageName("qrcode.png");
                response.setSendImageBytes(qrCode.getImage());
                response.setSendText("Временный код - "
                    + qrCode.getCode().substring(0, 2)
                    + " " + qrCode.getCode().substring(2, 4)
                    + " " + qrCode.getCode().substring(4, 6));
            } else if (GET_MY_POINTS.equals(messageText)) {
                double points = customer.points();
                if (points == 0) {
                    response.setSendText("У тебя пока ещё нет баллов.");
                } else {
                    response.setSendText("Твои баллы - " + points);
                }
            } else if (GET_ALL_DEALS.equals(messageText)) {
                response.setSendText("В данный момент нет акций.");
            } else if ("/start".equals(messageText)) {
                response.setSendText("Добро пожаловать обратно, " + customer.name() + "!");
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
                            .chatIds(List.of(entity.telegramId()))
                            .text(entity.text())
                            .build());
                    entity.sentAt(Instant.now());
                } catch (Exception e) {
                    entity.attempts(entity.attempts() + 1);
                    entity.error(entity.error() + "; " + e.getMessage());
                    entity.lastAttemptAt(Instant.now());
                }
                telegramPublishRepository.save(entity);
            });
    }

    protected GetQrCodeResponse getQrCode(Customer customer) {
        if (customer.qrCode() == null || customer.qrCodeExpiresAt().isBefore(Instant.now())) {
            final String code = String.valueOf(
                    LocalTime.now(ZoneId.of("UTC"))
                        .get(ChronoField.MILLI_OF_DAY) + 10_000_000)
                .substring(0, 6);

            customer.qrCode(code);
            customer.qrCodeExpiresAt(Instant.now().plusSeconds(300));
            customer = customerRepository.save(customer);
        }

        final byte[] image = qrCodeUtils.generateQRCodeImage(customer.qrCode());

        return GetQrCodeResponse.builder()
            .code(customer.qrCode())
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
