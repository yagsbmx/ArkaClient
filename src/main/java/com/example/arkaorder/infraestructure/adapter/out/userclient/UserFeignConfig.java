package com.example.arkaorder.infraestructure.adapter.out.userclient;

import feign.RequestInterceptor;
import feign.codec.ErrorDecoder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Configuration(proxyBeanMethods = false)
public class UserFeignConfig {

    @Value("${user-client.auth-token:}")
    private String serviceToken;

    @Bean
    public RequestInterceptor authForwardingInterceptor() {
        return template -> {
            String auth = null;
            var attrs = RequestContextHolder.getRequestAttributes();
            if (attrs instanceof ServletRequestAttributes sra && sra.getRequest() != null) {
                auth = sra.getRequest().getHeader("Authorization");
            }
            if (auth != null && !auth.isBlank()) {
                template.header("Authorization", auth);
            } else if (serviceToken != null && !serviceToken.isBlank()) {
                template.header("Authorization", serviceToken);
            }
        };
    }

    @Bean
    public ErrorDecoder feignErrorDecoder() {
        return new UserFeignErrorDecoder();
    }
}
