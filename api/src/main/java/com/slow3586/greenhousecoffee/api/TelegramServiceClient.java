package com.slow3586.greenhousecoffee.api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.telegram.telegrambots.meta.api.objects.Update;

@FeignClient(
    value = "telegramservice",
    url = "${app.client.telegramservice:http://127.0.0.1:8081/api/telegram}")
public interface TelegramServiceClient {
    @PostMapping
    TelegramProcessUpdateResponse process(@RequestBody Update update);
}
