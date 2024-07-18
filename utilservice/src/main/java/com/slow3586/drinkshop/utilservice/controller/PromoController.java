package com.slow3586.drinkshop.utilservice.controller;


import com.slow3586.drinkshop.api.mainservice.entity.Promo;
import com.slow3586.drinkshop.utilservice.repository.PromoRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/promo")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public class PromoController {
    PromoRepository promoRepository;

    @GetMapping("findAll")
    @Secured({"SYSTEM", "CASHIER", "ADMIN"})
    public List<Promo> findAll() {
        return promoRepository.findAll();
    }
}
