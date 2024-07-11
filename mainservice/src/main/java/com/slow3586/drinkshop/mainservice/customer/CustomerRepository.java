package com.slow3586.drinkshop.mainservice.customer;

import com.slow3586.drinkshop.mainservice.promo.PromoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Repository
@Transactional
public interface CustomerRepository extends JpaRepository<CustomerEntity, UUID> {
    CustomerEntity getByTelegramId(String telegramId);

    @Query("SELECT c FROM customer c WHERE c.phoneNumber <> NULL AND c.blockedReason IS NULL")
    List<CustomerEntity> findValidForPromos();
}
