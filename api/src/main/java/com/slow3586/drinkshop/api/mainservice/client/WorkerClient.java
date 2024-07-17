package com.slow3586.drinkshop.api.mainservice.client;

import com.slow3586.drinkshop.api.DefaultClient;
import com.slow3586.drinkshop.api.mainservice.entity.Worker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(
    value = "worker",
    url = "${app.client.shop:http://localhost:8080/api/worker}")
public interface WorkerClient extends DefaultClient<Worker> {
    @PostMapping("token")
    Worker token(String token);
}
