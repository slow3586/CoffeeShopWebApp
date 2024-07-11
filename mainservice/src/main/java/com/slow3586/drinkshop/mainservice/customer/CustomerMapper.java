package com.slow3586.drinkshop.mainservice.customer;

import com.slow3586.drinkshop.api.CustomerEntityDto;
import com.slow3586.drinkshop.api.IMapStructConfig;
import com.slow3586.drinkshop.api.IMapStructMapper;
import org.mapstruct.Mapper;

@Mapper(config = IMapStructConfig.class)
public interface CustomerMapper extends IMapStructMapper<CustomerEntityDto, CustomerEntity> {
}
