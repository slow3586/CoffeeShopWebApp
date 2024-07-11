package com.slow3586.drinkshop.mainservice.telegrampublish;

import com.slow3586.drinkshop.api.IMapStructConfig;
import com.slow3586.drinkshop.api.IMapStructMapper;
import com.slow3586.drinkshop.api.TelegramPublishEntityDto;
import org.mapstruct.Mapper;

@Mapper(config = IMapStructConfig.class)
public interface TelegramPublishMapper extends IMapStructMapper<TelegramPublishEntityDto, TelegramPublishEntity> {
}
