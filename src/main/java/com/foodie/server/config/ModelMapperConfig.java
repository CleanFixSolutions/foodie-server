package com.foodie.server.config;

import com.foodie.server.config.modelmapper.RecipeEntityToRecipeDtoConverter;
import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {
    @Bean
    public ModelMapper modelMapper(Gson gson) {

        ModelMapper modelMapper = new ModelMapper();
        modelMapper.addConverter(new RecipeEntityToRecipeDtoConverter(gson));
        return modelMapper;
    }
}
