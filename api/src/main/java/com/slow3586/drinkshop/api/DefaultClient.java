package com.slow3586.drinkshop.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

public interface DefaultClient<T> {
    @GetMapping("findById/{id}")
    T findById(@PathVariable UUID id);
}
