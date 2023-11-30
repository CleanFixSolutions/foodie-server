package com.foodie.server.config;

import com.foodie.server.exception.custom.UserNotFoundClientException;
import com.foodie.server.repository.UserRepository;
import com.google.gson.Gson;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;

@Configuration
public class ApplicationConfig {

    @Bean
    public Gson gson(){
        return new Gson();
    }

    @Bean
    public UserDetailsService userDetailsService(UserRepository userRepository) {
        return username -> userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundClientException(username));
    }
}
