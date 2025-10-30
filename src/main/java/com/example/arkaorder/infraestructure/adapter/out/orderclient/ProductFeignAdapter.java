package com.example.arkaorder.infraestructure.adapter.out.orderclient;

import com.example.arkaorder.domain.exceptions.InsufficientStockException;
import com.example.arkaorder.domain.exceptions.ProductNotFoundException;
import com.example.arkaorder.domain.exceptions.ProductServiceUnavailableException;
import com.example.arkaorder.domain.ports.out.ProductPort;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class ProductFeignAdapter implements ProductPort {

    private final ProductFeignClient client;

    @Override
    public boolean isAvailable(Long productId, int quantity) {
        try { return client.isAvailable(productId, quantity); }
        catch (FeignException.NotFound e) { throw new ProductNotFoundException(productId); }
        catch (FeignException e) { throw new ProductServiceUnavailableException("Error checking availability", e); }
    }

    @Override
    public String reserve(Long productId, int quantity, long ttlSeconds) {
        try { return client.reserve(productId, quantity, ttlSeconds); }
        catch (FeignException.NotFound e) { throw new ProductNotFoundException(productId); }
        catch (FeignException.Conflict e) { throw new InsufficientStockException(productId, quantity); }
        catch (FeignException e) { throw new ProductServiceUnavailableException("Error reserving stock", e); }
    }

    @Override
    public void commitReservation(String reservationId) {
        try { client.commitReservation(reservationId); }
        catch (FeignException e) { throw new ProductServiceUnavailableException("Error committing reservation", e); }
    }

    @Override
    public void releaseReservation(String reservationId) {
        try { client.releaseReservation(reservationId); }
        catch (FeignException e) { throw new ProductServiceUnavailableException("Error releasing reservation", e); }
    }

    @Override
    public void decrementStock(Long productId, int quantity) {
        try { client.decrementStock(productId, quantity); }
        catch (FeignException.NotFound e) { throw new ProductNotFoundException(productId); }
        catch (FeignException.Conflict e) { throw new InsufficientStockException(productId, quantity); }
        catch (FeignException e) { throw new ProductServiceUnavailableException("Error decrementing stock", e); }
    }

    @Override
    public void incrementStock(Long productId, int quantity) {
        try { client.incrementStock(productId, quantity); }
        catch (FeignException.NotFound e) { throw new ProductNotFoundException(productId); }
        catch (FeignException e) { throw new ProductServiceUnavailableException("Error incrementing stock", e); }
    }

    @Override
    public BigDecimal getPrice(Long productId) {
        try {
            var p = client.getById(productId);
            if (p == null || p.price() == null) throw new ProductServiceUnavailableException("Price not available");
            return p.price();
        } catch (FeignException.NotFound e) {
            throw new ProductNotFoundException(productId);
        } catch (FeignException e) {
            throw new ProductServiceUnavailableException("Error getting price", e);
        }
    }
}
