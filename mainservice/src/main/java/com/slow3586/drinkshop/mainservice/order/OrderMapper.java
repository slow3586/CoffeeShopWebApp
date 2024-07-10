package com.slow3586.drinkshop.mainservice.order;

import com.slow3586.drinkshop.api.IMapStructConfig;
import com.slow3586.drinkshop.api.IMapStructMapper;
import com.slow3586.drinkshop.api.OrderDto;
import org.mapstruct.Mapper;

@Mapper(config = IMapStructConfig.class)
public interface OrderMapper extends IMapStructMapper<OrderDto, OrderEntity> {
}
