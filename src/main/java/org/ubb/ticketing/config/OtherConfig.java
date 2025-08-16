package org.ubb.ticketing.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OtherConfig {

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();

    }

}
