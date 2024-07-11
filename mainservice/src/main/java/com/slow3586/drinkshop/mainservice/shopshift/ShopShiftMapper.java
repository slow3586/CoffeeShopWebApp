package com.slow3586.drinkshop.mainservice.shopshift;

import com.slow3586.drinkshop.api.CustomerOrderEntityDto;
import com.slow3586.drinkshop.api.IMapStructConfig;
import com.slow3586.drinkshop.api.IMapStructMapper;
import com.slow3586.drinkshop.api.ShopShiftEntityDto;
import org.mapstruct.Mapper;

@Mapper(config = IMapStructConfig.class)
public interface ShopShiftMapper extends IMapStructMapper<ShopShiftEntityDto, ShopShiftEntity> {
}
