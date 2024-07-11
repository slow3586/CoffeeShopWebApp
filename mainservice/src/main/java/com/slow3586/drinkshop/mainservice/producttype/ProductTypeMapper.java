package com.slow3586.drinkshop.mainservice.producttype;

import com.slow3586.drinkshop.api.IMapStructConfig;
import com.slow3586.drinkshop.api.IMapStructMapper;
import com.slow3586.drinkshop.api.ProductEntityDto;
import com.slow3586.drinkshop.api.ProductTypeEntityDto;
import org.mapstruct.Mapper;

@Mapper(config = IMapStructConfig.class)
public interface ProductTypeMapper extends IMapStructMapper<ProductTypeEntityDto, ProductTypeEntity> {
}
