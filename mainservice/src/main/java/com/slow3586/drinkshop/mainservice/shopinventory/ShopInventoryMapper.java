package com.slow3586.drinkshop.mainservice.shopinventory;

import com.slow3586.drinkshop.api.IMapStructConfig;
import com.slow3586.drinkshop.api.IMapStructMapper;
import com.slow3586.drinkshop.api.CustomerOrderDto;
import org.mapstruct.Mapper;

@Mapper(config = IMapStructConfig.class)
public interface ShopInventoryMapper extends IMapStructMapper<CustomerOrderDto, ShopInventoryEntity> {
}
