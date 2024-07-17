package com.slow3586.drinkshop.api.mainservice;

import lombok.Builder;
import lombok.Data;

import java.time.Duration;

@Data
@Builder
public class GetQrCodeResponse {
    String code;
    byte[] image;
    Duration duration;
}