package com.example.arkaorder.infraestructure.adapter.out.userclient;

import com.example.arkaorder.infraestructure.config.FeignAuthForwardConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "user-service",
        path = "/api/users",
        configuration = FeignAuthForwardConfig.class
)
public interface UserFeignClient {
    @GetMapping("/{id}")
    ApiResponse<UserFeignResponseDto> getUserById(@PathVariable("id") Long id);

    @GetMapping("/{id}/email")
    String getEmail(@PathVariable("id") Long id);
}


