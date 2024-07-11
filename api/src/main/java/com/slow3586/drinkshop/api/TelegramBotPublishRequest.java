package com.slow3586.drinkshop.api;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class TelegramBotPublishRequest {
    List<String> chatIds;
    String text;
}
