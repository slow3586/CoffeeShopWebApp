package com.slow3586.drinkshop.api;

import io.vavr.collection.List;
import io.vavr.control.Option;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.Repository;

import java.util.UUID;

@NoRepositoryBean
public interface VavrRepository<CL> extends Repository<CL, UUID> {
    Option<CL> findById(UUID id);

    boolean existsById(UUID id);

    List<CL> findAll();

    <S extends CL> S save(CL entity);

    <S extends CL> List<S> saveAll(Iterable<CL> entities);
}
