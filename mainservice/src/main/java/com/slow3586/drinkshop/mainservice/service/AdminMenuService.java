package com.slow3586.drinkshop.mainservice.service;


import com.slow3586.drinkshop.mainservice.entity.Customer;
import com.slow3586.drinkshop.mainservice.entity.Promo;
import com.slow3586.drinkshop.mainservice.entity.TelegramPublish;
import com.slow3586.drinkshop.mainservice.repository.CustomerRepository;
import com.slow3586.drinkshop.mainservice.repository.PromoRepository;
import com.slow3586.drinkshop.mainservice.repository.TelegramPublishRepository;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.QueryParam;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/admin_menu")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public class AdminMenuService {
    CustomerRepository customerRepository;
    PromoRepository promoRepository;
    TelegramPublishRepository telegramPublishRepository;

    @GetMapping("get_promos")
    public List<Promo> getPromoList(@QueryParam("page") Integer page) {
        return promoRepository.getPromoList(Optional.ofNullable(page).orElse(0) * 10);
    }

    @Transactional
    @PostMapping("create_promo")
    public void createPromo(@RequestBody @NonNull Promo promo) {
        Promo saved = promoRepository.save(promo);

        List<Customer> validForPromos = customerRepository.findValidForPromos();

        telegramPublishRepository.saveAll(
            validForPromos
                .stream()
                .map(customer ->
                    TelegramPublish.builder()
                        .telegramId(customer.getTelegramId())
                        .text(saved.getText())
                        .build())
                .toList());
    }
}
