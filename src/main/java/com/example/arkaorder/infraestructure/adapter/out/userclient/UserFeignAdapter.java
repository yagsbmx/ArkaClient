package com.example.arkaorder.infraestructure.adapter.out.userclient;

import com.example.arkaorder.domain.ports.out.UserPort;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserFeignAdapter implements UserPort {

    private final UserFeignClient userClient;

    @Override
    public boolean existsAndActive(Long userId) {
        try {
            ApiResponse<UserFeignResponseDto> resp = userClient.getUserById(userId);
            UserFeignResponseDto user = resp != null ? resp.getData() : null;
            return user != null && user.isActive();
        } catch (FeignException.NotFound | FeignException.BadRequest e) {
            return false;
        } catch (FeignException.Unauthorized e) {
            return false;
        } catch (FeignException e) {
            return false;
        }
    }

    @Override
    public String getEmailById(Long id) {
        try {
            return userClient.getEmail(id);
        } catch (FeignException.Unauthorized e) {
            return null;
        } catch (FeignException e) {
            return null;
        }
    }
}
