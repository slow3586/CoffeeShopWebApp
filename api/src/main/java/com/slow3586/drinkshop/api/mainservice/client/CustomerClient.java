package com.slow3586.drinkshop.api.mainservice.client;

import com.slow3586.drinkshop.api.DefaultClient;
import com.slow3586.drinkshop.api.mainservice.dto.GetQrCodeResponse;
import com.slow3586.drinkshop.api.mainservice.entity.Customer;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

@FeignClient(
    value = "customer",
    url = "${app.client.customer:http://localhost:8080/api/customer}")
public interface CustomerClient extends DefaultClient<Customer> {
    @GetMapping("findOrCreateByTelegramId/{id}")
    Customer findOrCreateByTelegramId(@PathVariable String id);

    @GetMapping("findByQrCode/{qrCode}")
    Customer findByQrCode(@PathVariable String qrCode);

    @GetMapping("getQrCode/{telegramId}")
    GetQrCodeResponse getQrCode(@PathVariable String telegramId);

    @PostMapping("updateContact")
    Customer updateContact(@RequestBody Customer customer);
}
