package com.slow3586.drinkshop.mainservice.worker;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Repository
@Transactional
public interface WorkerRepository extends JpaRepository<WorkerEntity, UUID> {
    @Override
    List<WorkerEntity> findAll();
}
