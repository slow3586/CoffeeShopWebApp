package com.slow3586.drinkshop.productservice.repository;


import com.slow3586.drinkshop.api.mainservice.entity.Product;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Repository
@Transactional(transactionManager = "transactionManager")
public interface ProductRepository extends ListCrudRepository<Product, UUID> {
    @Query("select * from product p order by p.created_at offset :offset limit 10")
    List<Product> query(int offset);
}
