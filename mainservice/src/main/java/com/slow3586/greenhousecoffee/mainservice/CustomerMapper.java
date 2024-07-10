package com.slow3586.drinkshop.mainservice;

import com.slow3586.drinkshop.api.CustomerDto;
import com.slow3586.drinkshop.api.IMapStructConfig;
import com.slow3586.drinkshop.api.IMapStructMapper;
import org.mapstruct.Mapper;

@Mapper(config = IMapStructConfig.class)
public interface CustomerMapper extends IMapStructMapper<CustomerDto, CustomerEntity> {
}
