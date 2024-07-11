package com.slow3586.drinkshop.telegrambot;

import com.slow3586.drinkshop.api.TelegramBotPublishRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
@RequestMapping("api/telegrambot")
public class Controller {
    TelegramLongPollingBot bot;

    @PostMapping("publish")
    public void publish(@RequestBody TelegramBotPublishRequest request) {
        request.getChatIds().forEach(chatId -> {
            try {
                bot.execute(SendMessage.builder()
                    .chatId(chatId)
                    .text(request.getText())
                    .build());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
}