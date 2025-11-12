package com.example.arkaorder.infraestructure.adapter.out.cartclient;

import com.example.arkaorder.infraestructure.config.FeignAuthForwardConfig;
import com.example.arkaorder.infraestructure.config.FeignNoAuthConfig;
import com.example.arkaorder.infraestructure.dto.CartResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "shoppingcart-service",
        path = "/api/carts",
        configuration = FeignNoAuthConfig.class
)
public interface CartFeignClient {

    @GetMapping("/active/user/{userId}")
    CartResponseDto getActiveCartByUser(@PathVariable("userId") Long userId);
}
