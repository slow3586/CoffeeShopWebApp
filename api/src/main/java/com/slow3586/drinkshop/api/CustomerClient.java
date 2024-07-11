package com.slow3586.drinkshop.api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

@FeignClient(
    value = "customer",
    url = "${app.client.customer:http://127.0.0.1:8081/api/customer}")
public interface CustomerClient {
    @GetMapping(value = "/{id}")
    CustomerEntityDto get(@PathVariable UUID id);

    @GetMapping(value = "/{telegramId}")
    CustomerEntityDto getByTelegramId(@PathVariable String telegramId);

    @GetMapping("/qr/{telegramId}")
    GetQrCodeResponse getQrCode(@PathVariable String telegramId);

    @PostMapping
    UUID save(@RequestBody CustomerEntityDto customerDto);
}
