package com.slow3586.drinkshop.utilservice.service;

import com.slow3586.drinkshop.api.QrCodeUtils;
import com.slow3586.drinkshop.api.mainservice.dto.GetQrCodeResponse;
import com.slow3586.drinkshop.api.mainservice.dto.LoginRequest;
import com.slow3586.drinkshop.api.mainservice.entity.Worker;
import com.slow3586.drinkshop.api.mainservice.topic.WorkerTopics;
import com.slow3586.drinkshop.utilservice.repository.WorkerRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoField;
import java.util.Date;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public class WorkerService {
    WorkerRepository workerRepository;
    PasswordEncoder passwordEncoder;
    SecretKey secretKey;
    QrCodeUtils qrCodeUtils;

    @SendTo(WorkerTopics.REQUEST_LOGIN_RESPONSE)
    @KafkaListener(topics = WorkerTopics.REQUEST_LOGIN, errorHandler = "defaultKafkaListenerErrorHandler")
    public String login(LoginRequest loginRequest) {
        Worker worker = workerRepository.findByPhoneNumber(loginRequest.getPhone())
            .orElseThrow();

        if (!passwordEncoder.matches(
            loginRequest.getPassword(),
            worker.getPassword())
        ) {
            throw new IllegalArgumentException("Incorrect login or password!");
        }

        return Jwts.builder()
            .subject(worker.getPhoneNumber())
            .expiration(Date.from(
                Instant.now().plus(
                    Duration.ofMinutes(600))))
            .signWith(secretKey)
            .compact();
    }

    @SendTo(WorkerTopics.REQUEST_TOKEN_RESPONSE)
    @KafkaListener(topics = WorkerTopics.REQUEST_TOKEN, errorHandler = "defaultKafkaListenerErrorHandler")
    public Worker token(String token) {
        Claims claims = Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(token)
            .getPayload();

        Worker worker = workerRepository.findByPhoneNumber(claims.getSubject())
            .orElseThrow();

        if (Instant.now().isAfter(claims.getExpiration().toInstant())) {
            throw new IllegalArgumentException("Token expired!");
        }

        return new Worker()
            .setId(worker.getId())
            .setRole(worker.getRole());
    }

    public GetQrCodeResponse getQrCode(String telegramId) {
        Worker worker = workerRepository.findByTelegramId(telegramId)
            .orElseThrow();

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
}
