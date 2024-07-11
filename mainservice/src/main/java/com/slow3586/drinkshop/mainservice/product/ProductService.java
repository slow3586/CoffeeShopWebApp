package com.slow3586.drinkshop.mainservice.product;

import com.slow3586.drinkshop.api.ProductEntityDto;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/product")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public class ProductService {
    ProductRepository productRepository;
    ProductMapper productMapper;

    @GetMapping
    public List<ProductEntityDto> getProducts() {
        return productRepository.findAll()
            .stream()
            .map(productMapper::toDto)
            .toList();
    }
}
