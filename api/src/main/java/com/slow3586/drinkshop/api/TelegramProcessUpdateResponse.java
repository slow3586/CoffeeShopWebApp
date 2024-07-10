package com.slow3586.drinkshop.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class TelegramProcessUpdateResponse {
    byte[] sendImageBytes;
    String sendImageName;
    String sendText;
    @Builder.Default
    List<List<String>> sendTextKeyboard = new ArrayList<>();
    @Builder.Default
    List<String> tags = new ArrayList<>();
}
