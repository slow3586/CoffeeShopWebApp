package com.slow3586.drinkshop.mainservice.shop;

import com.slow3586.drinkshop.api.IMapStructConfig;
import com.slow3586.drinkshop.api.IMapStructMapper;
import com.slow3586.drinkshop.api.CustomerOrderDto;
import org.mapstruct.Mapper;

@Mapper(config = IMapStructConfig.class)
public interface ShopMapper extends IMapStructMapper<CustomerOrderDto, ShopEntity> {
}
