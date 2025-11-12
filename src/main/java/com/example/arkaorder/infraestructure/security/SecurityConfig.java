package com.example.arkaorder.infraestructure.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/swagger-ui.html", "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/actuator/**", "/eureka/**"
                        ).permitAll()

                        .requestMatchers(HttpMethod.GET, "/api/orders/ping").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/orders/{id}").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/orders").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/orders/filter/status/{status}").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.POST, "/api/orders/from-cart/{userId}").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/orders/create").permitAll()

                        .requestMatchers(HttpMethod.PUT, "/api/orders/update/{id}").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/api/orders/{id}/status").permitAll()

                        .requestMatchers(HttpMethod.DELETE, "/api/orders/delete/{id}").permitAll()

                        .anyRequest().denyAll()
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }
}
