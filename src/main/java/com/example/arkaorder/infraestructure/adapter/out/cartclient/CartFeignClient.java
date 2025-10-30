package com.example.arkaorder.infraestructure.adapter.out.cartclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "shoppingcart-service")
public interface CartFeignClient {
    
    @PostMapping("/api/carts/complete/user/{userId}")
    void completeCartByUserId(@PathVariable("userId") Long userId);
}