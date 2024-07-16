package com.slow3586.drinkshop.api.mainservice;

import com.slow3586.drinkshop.api.mainservice.dto.TelegramProcessResponse;
import com.slow3586.drinkshop.api.telegrambot.TelegramProcessRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
    value = "telegramservice",
    url = "${app.client.telegramservice:http://127.0.0.1:8080/api/telegram}")
public interface TelegramServiceClient {
    @PostMapping
    TelegramProcessResponse process(@RequestBody TelegramProcessRequest telegramProcessRequest);
}
