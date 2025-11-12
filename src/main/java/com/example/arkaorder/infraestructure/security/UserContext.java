package com.example.arkaorder.infraestructure.security;


import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import jakarta.servlet.http.HttpServletRequest;

@Component
public class UserContext {
    public Long currentUserIdOrNull() {
        var attrs = RequestContextHolder.getRequestAttributes();
        if (attrs == null) return null;
        var req = (HttpServletRequest) attrs.resolveReference(RequestAttributes.REFERENCE_REQUEST);
        if (req == null) return null;
        var h = req.getHeader("X-User-Id");
        if (h == null || h.isBlank()) return null;
        try { return Long.parseLong(h.trim()); } catch (NumberFormatException e) { return null; }
    }
}