package com.example.arkaorder.infraestructure.config;

import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class FeignGenericErrorDecoder implements ErrorDecoder {
    private final ErrorDecoder def = new Default();

    @Override
    public Exception decode(String methodKey, Response r) {
        return switch (r.status()) {
            case 401 -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No autorizado en servicio remoto");
            case 404 -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Recurso no encontrado en servicio remoto");
            default -> def.decode(methodKey, r);
        };
    }
}
