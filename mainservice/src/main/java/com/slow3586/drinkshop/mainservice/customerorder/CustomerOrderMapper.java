package com.slow3586.drinkshop.mainservice.customerorder;

import com.slow3586.drinkshop.api.IMapStructConfig;
import com.slow3586.drinkshop.api.IMapStructMapper;
import com.slow3586.drinkshop.api.CustomerOrderDto;
import org.mapstruct.Mapper;

@Mapper(config = IMapStructConfig.class)
public interface CustomerOrderMapper extends IMapStructMapper<CustomerOrderDto, CustomerOrderEntity> {
}
