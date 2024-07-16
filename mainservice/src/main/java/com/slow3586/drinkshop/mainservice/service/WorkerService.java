package com.slow3586.drinkshop.mainservice.service;

import com.slow3586.drinkshop.api.mainservice.LoginRequest;
import com.slow3586.drinkshop.api.mainservice.entity.Worker;
import com.slow3586.drinkshop.mainservice.repository.WorkerRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

@Slf4j
@Service
@RequiredArgsConstructor
@RestController("api/worker")
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public class WorkerService {
    WorkerRepository workerRepository;
    PasswordEncoder passwordEncoder;
    SecretKey secretKey;

    @PostMapping("login")
    public String login(LoginRequest loginRequest) {
        Worker worker = workerRepository.findByPhoneNumber(loginRequest.getPhone());

        if (worker == null
            || !passwordEncoder.matches(
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

    @PostMapping("token")
    public String token(String token) {
        Claims claims = Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(token)
            .getPayload();

        Worker worker = workerRepository.findByPhoneNumber(claims.getSubject());

        if (worker == null) {
            throw new IllegalArgumentException("Unknown user!");
        }

        if (Instant.now().isAfter(claims.getExpiration().toInstant())) {
            throw new IllegalArgumentException("Token expired!");
        }

        return claims.getSubject();
    }
}
