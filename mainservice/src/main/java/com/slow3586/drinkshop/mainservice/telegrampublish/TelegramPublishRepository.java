package com.slow3586.drinkshop.mainservice.telegrampublish;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Repository
@Transactional
public interface TelegramPublishRepository extends JpaRepository<TelegramPublishEntity, UUID> {
    @Override
    List<TelegramPublishEntity> findAll();

    @Query("select t from telegram_publish t where t.attempts < 3 and t.sentAt is null order by t.lastAttemptAt DESC, t.createdAt DESC")
    List<TelegramPublishEntity> findToSend();
}
