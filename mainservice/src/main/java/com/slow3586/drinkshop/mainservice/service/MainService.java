package com.slow3586.drinkshop.mainservice.service;


import com.slow3586.drinkshop.mainservice.repository.CustomerRepository;
import com.slow3586.drinkshop.mainservice.repository.PromoRepository;
import com.slow3586.drinkshop.mainservice.repository.TelegramPublishRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/main")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public class MainService {
    CustomerRepository customerRepository;
    PromoRepository promoRepository;
    TelegramPublishRepository telegramPublishRepository;
}
