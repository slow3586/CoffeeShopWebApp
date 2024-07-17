package com.slow3586.drinkshop.api.mainservice.client;

import com.slow3586.drinkshop.api.DefaultClient;
import com.slow3586.drinkshop.api.mainservice.entity.Shop;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(
    value = "shop",
    url = "${app.client.shop:http://localhost:8080/api/shop}")
public interface ShopClient extends DefaultClient<Shop> {
}
