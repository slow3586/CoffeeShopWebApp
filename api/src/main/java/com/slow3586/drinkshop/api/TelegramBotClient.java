package com.slow3586.drinkshop.api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
    value = "telegrambot",
    url = "${app.client.telegrambot:http://127.0.0.1:8085/api/telegrambot}")
public interface TelegramBotClient {
    @PostMapping(value = "publish")
    void publish(@RequestBody TelegramBotPublishRequest telegramBotPublishRequest);
}
