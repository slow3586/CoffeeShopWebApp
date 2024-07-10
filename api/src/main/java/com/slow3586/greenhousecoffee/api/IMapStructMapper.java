package com.slow3586.greenhousecoffee.api;

public interface IMapStructMapper<DTO, ENT> {
    DTO toDto(ENT entity);

    ENT toEntity(DTO dto);
}
