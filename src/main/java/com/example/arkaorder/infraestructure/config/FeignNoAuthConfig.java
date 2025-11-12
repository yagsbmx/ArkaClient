package com.example.arkaorder.infraestructure.config;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignNoAuthConfig {
    @Bean
    public RequestInterceptor noAuthInterceptor() {
        return requestTemplate -> {}; // no añade headers de autenticación
    }
}
