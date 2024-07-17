package com.slow3586.drinkshop.api.mainservice.client;

import com.slow3586.drinkshop.api.DefaultClient;
import com.slow3586.drinkshop.api.mainservice.entity.Product;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(
    value = "product",
    url = "${app.client.shop:http://localhost:8080/api/product}")
public interface ProductClient extends DefaultClient<Product> {
}
