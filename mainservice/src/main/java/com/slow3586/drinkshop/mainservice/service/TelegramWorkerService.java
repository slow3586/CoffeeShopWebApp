package com.slow3586.drinkshop.mainservice.service;

import com.slow3586.drinkshop.api.mainservice.TelegramProcessResponse;
import com.slow3586.drinkshop.api.mainservice.entity.Worker;
import com.slow3586.drinkshop.api.telegrambot.TelegramBotClient;
import com.slow3586.drinkshop.api.telegrambot.TelegramBotPublishRequest;
import com.slow3586.drinkshop.api.telegrambot.TelegramProcessRequest;
import com.slow3586.drinkshop.mainservice.repository.PromoRepository;
import com.slow3586.drinkshop.mainservice.repository.TelegramPublishRepository;
import com.slow3586.drinkshop.mainservice.repository.WorkerRepository;
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
@RequestMapping("api/telegram/worker")
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public class TelegramWorkerService {
    static String GET_ALL_DEALS = "Акции";
    static String GET_QR_CODE = "QR код";
    static String GET_MY_POINTS = "Баллы";
    TelegramPublishRepository telegramPublishRepository;
    TelegramBotClient telegramBotClient;
    QrCodeUtils qrCodeUtils;
    PromoRepository promoRepository;
    WorkerRepository workerRepository;

    @PostMapping
    @Secured({"SYSTEM"})
    public TelegramProcessResponse process(@RequestBody TelegramProcessRequest request) {
        final TelegramProcessResponse response = new TelegramProcessResponse();

        final String telegramId = request.getTelegramId();
        final String messageText = request.getText();

        Worker worker = workerRepository.findByTelegramId(telegramId);
        if (worker == null) {
            if (request.getPhone() != null) {
                worker = workerRepository.findByPhoneNumber(request.getPhone());
                if (worker != null && worker.getTelegramId() == null) {
                    worker.setTelegramId(telegramId);
                    worker = workerRepository.save(worker);
                }
            } else {
                response.setSendText(".");
                response.getTags().add("REQUEST_CONTACT");
            }
        }

        if (worker != null && worker.getPassword() == null) {
            if (messageText.length() <= 7) {
                response.setSendText("Привет, " + worker.getName() + "! Пожалуйста, придумай пароль и напиши его");
            } else {
                worker.setPassword(messageText);
                worker = workerRepository.save(worker);
                response.setSendText("Регистрация успешно завершена!");
            }
        }

        //region LOGIC
        if (worker != null && worker.getPhoneNumber() != null && worker.getBlockedReason() == null) {
            if (GET_QR_CODE.equals(messageText)) {
                GetQrCodeResponse qrCode = this.getQrCode(worker);

                response.setSendImageName("qrcode.png");
                response.setSendImageBytes(qrCode.getImage());
                response.setSendText("Временный код - "
                    + qrCode.getCode().substring(0, 2)
                    + " " + qrCode.getCode().substring(2, 4)
                    + " " + qrCode.getCode().substring(4, 6));
            } else if ("/start".equals(messageText)) {
                response.setSendText("Добро пожаловать обратно, " + worker.getName() + "!");
            }

            if (response.getSendText() == null) {
                response.setSendText("Перевожу тебя в главное меню.");
            }
            response.setSendTextKeyboard(List.of(List.of(
                GET_QR_CODE, GET_MY_POINTS, GET_ALL_DEALS)));
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

    protected GetQrCodeResponse getQrCode(Worker worker) {
        if (worker.getQrCode() == null || worker.getQrCodeExpiresAt().isBefore(Instant.now())) {
            final String code = String.valueOf(
                    LocalTime.now(ZoneId.of("UTC"))
                        .get(ChronoField.MILLI_OF_DAY) + 10_000_000)
                .substring(0, 6);

            worker.setQrCode(code);
            worker.setQrCodeExpiresAt(Instant.now().plusSeconds(300));
            worker = workerRepository.save(worker);
        }

        final byte[] image = qrCodeUtils.generateQRCodeImage(worker.getQrCode());

        return GetQrCodeResponse.builder()
            .code(worker.getQrCode())
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
