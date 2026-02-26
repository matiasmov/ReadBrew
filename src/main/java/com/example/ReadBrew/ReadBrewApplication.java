package com.example.ReadBrew;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication

@EnableFeignClients(basePackages = "com.example.ReadBrew.client") 
public class ReadBrewApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReadBrewApplication.class, args);
    }
}