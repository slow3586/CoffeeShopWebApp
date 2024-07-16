package com.slow3586.drinkshop.api.mainservice;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class LoginRequest {
    String phone;
    String password;
    String code;
}
