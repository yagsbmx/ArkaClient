package com.example.arkaorder.domain.ports.out;

public interface CartPort {
    void completeCartByUserId(Long userId);
}