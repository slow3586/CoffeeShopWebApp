package com.slow3586.drinkshop.api;

import io.vavr.collection.List;
import io.vavr.control.Option;
import org.springframework.data.repository.Repository;

import java.util.UUID;

public interface VavrRepository<CL> extends Repository<UUID, CL> {
    CL save(CL entity);
    Option<CL> findById(UUID id);
    List<CL> findAll();
    List<CL> saveAll(List<CL> entities);
}
