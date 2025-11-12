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
        try {
            var res = client.isAvailable(productId, quantity);
            if (res == null) throw new ProductServiceUnavailableException("Respuesta nula de inventario");
            if (!res.success()) throw new ProductServiceUnavailableException(res.message() != null ? res.message() : "Error verificando disponibilidad");
            return Boolean.TRUE.equals(res.data());
        } catch (FeignException.NotFound e) {
            throw new ProductNotFoundException(productId);
        } catch (FeignException e) {
            throw new ProductServiceUnavailableException("Error verificando disponibilidad", e);
        }
    }

    @Override
    public void decrementStock(Long productId, int quantity) {
        try {
            client.decrementStock(productId, quantity);
        } catch (FeignException.NotFound e) {
            throw new ProductNotFoundException(productId);
        } catch (FeignException.Conflict e) {
            throw new InsufficientStockException(productId, quantity);
        } catch (FeignException e) {
            throw new ProductServiceUnavailableException("Error decrementando stock", e);
        }
    }

    @Override
    public void incrementStock(Long productId, int quantity) {
        try {
            client.incrementStock(productId, quantity);
        } catch (FeignException.NotFound e) {
            throw new ProductNotFoundException(productId);
        } catch (FeignException e) {
            throw new ProductServiceUnavailableException("Error incrementando stock", e);
        }
    }

    @Override
    public BigDecimal getPrice(Long productId) {
        try {
            var res = client.getById(productId);
            if (res == null) throw new ProductServiceUnavailableException("Respuesta nula al obtener producto");
            if (!res.success()) throw new ProductServiceUnavailableException(res.message() != null ? res.message() : "Error obteniendo producto");
            var dto = res.data();
            if (dto == null || dto.price() == null) throw new ProductServiceUnavailableException("Precio no disponible");
            return dto.price();
        } catch (FeignException.NotFound e) {
            throw new ProductNotFoundException(productId);
        } catch (FeignException e) {
            throw new ProductServiceUnavailableException("Error obteniendo precio", e);
        }
    }

    @Override
    public String reserve(Long productId, int quantity, long ttlSeconds) {
        throw new ProductServiceUnavailableException("Reservas no soportadas por el servicio de productos");
    }

    @Override
    public void commitReservation(String reservationId) {
        throw new ProductServiceUnavailableException("Reservas no soportadas por el servicio de productos");
    }

    @Override
    public void releaseReservation(String reservationId) {
        throw new ProductServiceUnavailableException("Reservas no soportadas por el servicio de productos");
    }
}
