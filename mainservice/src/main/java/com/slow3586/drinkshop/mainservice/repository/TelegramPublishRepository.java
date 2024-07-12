package com.slow3586.drinkshop.mainservice.repository;

import com.slow3586.drinkshop.api.mainservice.entity.TelegramPublish;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Repository
@Transactional(transactionManager = "transactionManager")
public interface TelegramPublishRepository extends ListCrudRepository<TelegramPublish, UUID> {
    @Query("select * from telegram_publish t where t.attempts < 3 and t.sent_at is null order by t.last_attempt_at DESC, t.created_at DESC limit 10")
    List<TelegramPublish> findToSend();
}
