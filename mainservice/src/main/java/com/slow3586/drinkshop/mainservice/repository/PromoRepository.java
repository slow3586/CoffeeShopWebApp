package com.slow3586.drinkshop.mainservice.repository;

import com.slow3586.drinkshop.api.mainservice.entity.Promo;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Repository
@Transactional(transactionManager = "transactionManager")
public interface PromoRepository extends ListCrudRepository<Promo, UUID> {
    @Query("select * from promo p order by p.created_at offset :offset limit 10")
    List<Promo> getPromoList(int offset);

    @Query("select * from promo p where p.status = 'NEW'")
    List<Promo> findToSend();
}
