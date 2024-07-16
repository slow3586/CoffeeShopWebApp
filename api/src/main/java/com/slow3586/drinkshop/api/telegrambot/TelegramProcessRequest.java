package com.slow3586.drinkshop.api.telegrambot;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class TelegramProcessRequest {
    String telegramId;
    String text;

    String name;
    String phone;
}
