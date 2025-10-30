package com.example.arkaorder.infraestructure.adapter.out.userclient;

import org.springframework.stereotype.Component;
import com.example.arkaorder.domain.ports.out.UserPort;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserFeignAdapter implements UserPort {

    private final UserFeignClient userClient;

    @Override
    public boolean existsAndActive(Long userId) {
        try {
            var user = userClient.getUserById(userId);
            return user != null && user.isActive();
        } catch (feign.FeignException.NotFound e) {
            return false;
        } catch (feign.FeignException e) {
            throw new IllegalStateException("Error calling user-service: " + e.status(), e);
        }
    }
}



