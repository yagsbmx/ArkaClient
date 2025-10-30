package com.example.arkaorder.infraestructure.adapter.out.cartclient;

import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import com.example.arkaorder.domain.ports.out.CartPort;

@Component
@RequiredArgsConstructor
public class CartFeignAdapter implements CartPort {

    private final CartFeignClient client;

    @Override
    public void completeCartByUserId(Long userId) {
        client.completeCartByUserId(userId);
    }
}
