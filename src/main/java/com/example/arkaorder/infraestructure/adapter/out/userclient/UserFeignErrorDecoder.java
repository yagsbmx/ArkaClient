package com.example.arkaorder.infraestructure.adapter.out.userclient;

import feign.Response;
import feign.codec.ErrorDecoder;

public class UserFeignErrorDecoder implements ErrorDecoder {

    private final ErrorDecoder defaultDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        if (response.status() == 401) {
            return new RuntimeException(" Unauthorized al llamar a user-service");
        }
        if (response.status() == 404) {
            return new RuntimeException(" Usuario no encontrado en user-service");
        }
        return defaultDecoder.decode(methodKey, response);
    }
}

