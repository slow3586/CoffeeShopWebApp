package com.slow3586.drinkshop.mainservice.repository;


import com.slow3586.drinkshop.api.mainservice.entity.ProductGroup;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Repository
@Transactional(transactionManager = "transactionManager")
public interface ProductGroupRepository extends ListCrudRepository<ProductGroup, UUID> {
    @Query("select * from product_group p order by p.created_at offset :offset limit 10")
    List<ProductGroup> query(int offset);
}
