package com.slow3586.drinkshop.mainservice.productinventory;

import com.slow3586.drinkshop.api.IMapStructConfig;
import com.slow3586.drinkshop.api.IMapStructMapper;
import com.slow3586.drinkshop.api.ProductEntityDto;
import com.slow3586.drinkshop.api.ProductInventoryEntityDto;
import org.mapstruct.Mapper;

@Mapper(config = IMapStructConfig.class)
public interface ProductInventoryMapper extends IMapStructMapper<ProductInventoryEntityDto, ProductInventoryEntity> {
}
