package com.slow3586.drinkshop.mainservice.repository;

import com.slow3586.drinkshop.api.VavrRepository;
import com.slow3586.drinkshop.api.mainservice.entity.CustomerOrder;
import com.slow3586.drinkshop.api.mainservice.entity.ProductType;
import io.vavr.collection.List;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Repository
@Transactional(transactionManager = "transactionManager")
public interface ProductTypeRepository extends VavrRepository<ProductType> {
    @Query("select * from product_type p order by p.created_at offset :offset limit 10")
    List<ProductType> query(int offset);
}
