package com.foodie.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

//@SpringBootApplication
@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
public class FoodieServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(FoodieServerApplication.class, args);
    }

}
