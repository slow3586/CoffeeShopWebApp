package com.slow3586.drinkshop.mainservice.service;


import com.slow3586.drinkshop.api.mainservice.entity.Product;
import com.slow3586.drinkshop.api.mainservice.entity.ProductInventory;
import com.slow3586.drinkshop.api.mainservice.entity.ProductInventoryType;
import com.slow3586.drinkshop.api.mainservice.entity.ProductType;
import com.slow3586.drinkshop.mainservice.repository.ProductInventoryRepository;
import com.slow3586.drinkshop.mainservice.repository.ProductInventoryTypeRepository;
import com.slow3586.drinkshop.mainservice.repository.ProductRepository;
import com.slow3586.drinkshop.mainservice.repository.ProductTypeRepository;
import io.vavr.collection.List;
import jakarta.ws.rs.QueryParam;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("api/product")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
@Slf4j
public class ProductService {
    ProductRepository productRepository;
    ProductTypeRepository productTypeRepository;
    ProductInventoryRepository productInventoryRepository;
    ProductInventoryTypeRepository productInventoryTypeRepository;

    @GetMapping("all")
    public List<Product> all() {
        return productRepository.findAll()
            .map(p -> p.setProductInventoryList(
                productInventoryRepository.findByProductId(p.getId())
                    .map(pi -> pi.setProductInventoryType(
                        productInventoryTypeRepository.findById(
                            pi.getProductInventoryTypeId()).get()))));
    }

    @GetMapping("query")
    public List<Product> query(@QueryParam("page") Integer page) {
        return productRepository.query(
            Optional.ofNullable(page).orElse(0) * 10);
    }

    @GetMapping("/type/query")
    public List<ProductType> queryType(@QueryParam("page") Integer page) {
        return productTypeRepository.query(
            Optional.ofNullable(page).orElse(0) * 10);
    }

    @GetMapping("/inventory/query")
    public List<ProductInventory> queryInventory(@QueryParam("page") Integer page) {
        return productInventoryRepository.query(
            Optional.ofNullable(page).orElse(0) * 10);
    }

    @GetMapping("/inventory/type/query")
    public List<ProductInventoryType> queryInventoryType(@QueryParam("page") Integer page) {
        return productInventoryTypeRepository.query(
            Optional.ofNullable(page).orElse(0) * 10);
    }
}
