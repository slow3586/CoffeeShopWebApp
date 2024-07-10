package com.slow3586.drinkshop.mainservice.worker;

import com.slow3586.drinkshop.api.IMapStructConfig;
import com.slow3586.drinkshop.api.IMapStructMapper;
import com.slow3586.drinkshop.api.CustomerOrderDto;
import org.mapstruct.Mapper;

@Mapper(config = IMapStructConfig.class)
public interface WorkerMapper extends IMapStructMapper<CustomerOrderDto, WorkerEntity> {
}
