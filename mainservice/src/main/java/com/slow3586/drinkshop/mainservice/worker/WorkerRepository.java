package com.slow3586.drinkshop.mainservice.worker;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface WorkerRepository extends JpaRepository<WorkerEntity, UUID> {
    @Override
    List<WorkerEntity> findAll();
}
