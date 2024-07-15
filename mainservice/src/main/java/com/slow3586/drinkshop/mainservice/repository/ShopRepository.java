package com.slow3586.drinkshop.mainservice.repository;

import com.slow3586.drinkshop.api.VavrRepository;
import com.slow3586.drinkshop.api.mainservice.entity.Shop;
import io.vavr.collection.List;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(transactionManager = "transactionManager")
public interface ShopRepository extends VavrRepository<Shop> {
    @Query("select * from shop p order by p.created_at offset :offset limit 10")
    List<Shop> query(int offset);
}
