package com.slow3586.drinkshop.buymenu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@SpringBootApplication(exclude = {
    org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class}
)
public class BuymenuApplication implements WebMvcConfigurer {

    public static void main(String[] args) {
        SpringApplication.run(BuymenuApplication.class, args);
    }

}
