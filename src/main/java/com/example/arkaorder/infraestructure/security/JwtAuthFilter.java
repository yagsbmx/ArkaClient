package com.example.arkaorder.infraestructure.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    @Value("${security.jwt.secret}")
    private String jwtSecret;

    @Value("${security.jwt.issuer:arka-auth}")
    private String issuer;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            chain.doFilter(request, response);
            return;
        }

        String auth = request.getHeader("Authorization");
        if (auth == null || !auth.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        String token = auth.substring(7);

        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8)))
                    .requireIssuer(issuer)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            String username = Optional.ofNullable(claims.get("preferred_username", String.class))
                    .orElseGet(claims::getSubject);

            Set<String> raw = new LinkedHashSet<>();
            addListClaim(claims, "roles", raw);
            addListClaim(claims, "authorities", raw);
            addListClaim(claims, "scope", raw);
            addListClaim(claims, "scopes", raw);

            Object realmAccess = claims.get("realm_access");
            if (realmAccess instanceof Map<?, ?> realm) {
                Object rolesObj = realm.get("roles");
                if (rolesObj instanceof Collection<?> c) {
                    c.forEach(v -> { if (v != null) raw.add(String.valueOf(v)); });
                }
            }

            Set<String> expanded = new LinkedHashSet<>();
            for (String s : raw) {
                if (s == null) continue;
                String t = s.trim();
                if (t.contains(",")) {
                    expanded.addAll(Arrays.stream(t.split(","))
                            .map(String::trim)
                            .filter(x -> !x.isEmpty())
                            .toList());
                } else if (t.contains(" ")) {
                    expanded.addAll(Arrays.stream(t.split("\\s+"))
                            .map(String::trim)
                            .filter(x -> !x.isEmpty())
                            .toList());
                } else {
                    expanded.add(t);
                }
            }

            Set<SimpleGrantedAuthority> authorities = expanded.stream()
                    .map(r -> r.replaceFirst("^ROLE_", ""))
                    .map(String::toUpperCase)
                    .map(r -> "ROLE_" + r)
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toCollection(LinkedHashSet::new));

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(username, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (JwtException e) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token JWT inválido o expirado");
            return;
        }

        chain.doFilter(request, response);
    }

    @SuppressWarnings("unchecked")
    private static void addListClaim(Claims claims, String key, Set<String> sink) {
        Object o = claims.get(key);
        if (o == null) return;
        if (o instanceof Collection<?> c) {
            c.forEach(v -> { if (v != null) sink.add(String.valueOf(v)); });
        } else if (o instanceof String s) {
            sink.add(s);
        }
    }
}
