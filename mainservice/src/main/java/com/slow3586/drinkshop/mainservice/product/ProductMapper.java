package com.slow3586.drinkshop.mainservice.product;

import com.slow3586.drinkshop.api.IMapStructConfig;
import com.slow3586.drinkshop.api.IMapStructMapper;
import com.slow3586.drinkshop.api.ProductEntityDto;
import org.mapstruct.Mapper;

@Mapper(config = IMapStructConfig.class)
public interface ProductMapper extends IMapStructMapper<ProductEntityDto, ProductEntity> {
}
