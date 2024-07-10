package com.slow3586.drinkshop.mainservice.customerorderitem;

import com.slow3586.drinkshop.api.IMapStructConfig;
import com.slow3586.drinkshop.api.IMapStructMapper;
import com.slow3586.drinkshop.api.ProductDto;
import org.mapstruct.Mapper;

@Mapper(config = IMapStructConfig.class)
public interface CustomerOrderItemMapper extends IMapStructMapper<ProductDto, CustomerOrderItemEntity> {
}
