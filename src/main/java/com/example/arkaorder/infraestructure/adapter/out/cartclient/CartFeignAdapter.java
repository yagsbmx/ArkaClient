package com.example.arkaorder.infraestructure.adapter.out.cartclient;

import com.example.arkaorder.domain.ports.out.CartPort;
import com.example.arkaorder.infraestructure.dto.CartResponseDto;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CartFeignAdapter implements CartPort {

    private final CartFeignClient client;

    @Override
    public CartPort.CartData getCartByUserId(Long userId) {
        try {
            CartResponseDto dto = client.getActiveCartByUser(userId);
            if (dto == null || dto.getItems() == null)
                return null;

            List<CartPort.CartItem> items = dto.getItems().stream()
                    .map(i -> new CartPort.CartItem(
                            i.getProductId(),
                            i.getName(),
                            i.getQuantity(),
                            i.getPrice()
                    ))
                    .toList();

            return new CartPort.CartData(
                    dto.getId(),
                    dto.getUserId(),
                    dto.getStatus(),
                    items,
                    dto.getTotalPrice()
            );

        } catch (FeignException.NotFound e) {
            return null;
        } catch (FeignException.Unauthorized | FeignException.Forbidden e) {
            return null;
        } catch (FeignException e) {
            return null;
        }
    }
}
