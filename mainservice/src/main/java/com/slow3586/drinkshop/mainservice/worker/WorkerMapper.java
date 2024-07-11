package com.slow3586.drinkshop.mainservice.worker;

import com.slow3586.drinkshop.api.CustomerOrderEntityDto;
import com.slow3586.drinkshop.api.IMapStructConfig;
import com.slow3586.drinkshop.api.IMapStructMapper;
import com.slow3586.drinkshop.api.WorkerEntityDto;
import org.mapstruct.Mapper;

@Mapper(config = IMapStructConfig.class)
public interface WorkerMapper extends IMapStructMapper<WorkerEntityDto, WorkerEntity> {
}
