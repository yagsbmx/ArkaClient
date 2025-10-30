package com.example.arkaorder.infraestructure.adapter.out.userclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
    name = "user-service",
    configuration = UserFeignConfig.class // 🔹 Enlaza la configuración personalizada
)
public interface UserFeignClient {

    @GetMapping("/users/{id}")
    UserFeignResponseDto getUserById(@PathVariable("id") Long id);
}

