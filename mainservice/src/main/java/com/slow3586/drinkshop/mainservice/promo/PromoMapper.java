package com.slow3586.drinkshop.mainservice.promo;

import com.slow3586.drinkshop.api.IMapStructConfig;
import com.slow3586.drinkshop.api.IMapStructMapper;
import com.slow3586.drinkshop.api.PromoEntityDto;
import org.mapstruct.Mapper;

@Mapper(config = IMapStructConfig.class)
public interface PromoMapper extends IMapStructMapper<PromoEntityDto, PromoEntity> {
}
