package com.slow3586.drinkshop.cashiermenu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@SpringBootApplication(exclude = {
    org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class}
)
public class CashierMenuApplication implements WebMvcConfigurer {

    public static void main(String[] args) {
        SpringApplication.run(CashierMenuApplication.class, args);
    }

}
