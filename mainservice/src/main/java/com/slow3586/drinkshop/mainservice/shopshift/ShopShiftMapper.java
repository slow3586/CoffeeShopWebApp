package com.slow3586.drinkshop.mainservice.shopshift;

import com.slow3586.drinkshop.api.IMapStructConfig;
import com.slow3586.drinkshop.api.IMapStructMapper;
import com.slow3586.drinkshop.api.CustomerOrderDto;
import org.mapstruct.Mapper;

@Mapper(config = IMapStructConfig.class)
public interface ShopShiftMapper extends IMapStructMapper<CustomerOrderDto, ShopShiftEntity> {
}
