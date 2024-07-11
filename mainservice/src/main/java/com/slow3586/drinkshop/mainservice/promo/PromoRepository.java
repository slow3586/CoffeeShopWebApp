package com.slow3586.drinkshop.mainservice.promo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Repository
@Transactional
public interface PromoRepository extends JpaRepository<PromoEntity, UUID> {


    List<PromoEntity> findByStatus(@NonNull String status);

    @Query("select p from promo p where p.status = 'NEW'")
    List<PromoEntity> findToSend();
}
