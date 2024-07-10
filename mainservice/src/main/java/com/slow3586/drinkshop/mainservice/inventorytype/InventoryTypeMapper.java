package com.slow3586.drinkshop.mainservice.inventorytype;

import com.slow3586.drinkshop.api.IMapStructConfig;
import com.slow3586.drinkshop.api.IMapStructMapper;
import com.slow3586.drinkshop.api.CustomerOrderDto;
import org.mapstruct.Mapper;

@Mapper(config = IMapStructConfig.class)
public interface InventoryTypeMapper extends IMapStructMapper<CustomerOrderDto, InventoryTypeEntity> {
}
