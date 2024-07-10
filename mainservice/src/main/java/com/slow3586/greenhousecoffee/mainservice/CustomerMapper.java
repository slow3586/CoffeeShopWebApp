package com.slow3586.greenhousecoffee.mainservice;

import com.slow3586.greenhousecoffee.api.CustomerDto;
import com.slow3586.greenhousecoffee.api.IMapStructConfig;
import com.slow3586.greenhousecoffee.api.IMapStructMapper;
import org.mapstruct.Mapper;

@Mapper(config = IMapStructConfig.class)
public interface CustomerMapper extends IMapStructMapper<CustomerDto, CustomerEntity> {
}
