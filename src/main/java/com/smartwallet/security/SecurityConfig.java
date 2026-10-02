package com.smartwallet.security;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http
                // =========================
                // CORS
                // =========================
                .cors(cors -> {
                })

                // =========================
                // CSRF
                // JWT-based API
                // =========================
                .csrf(csrf -> csrf.disable())

                // =========================
                // Authorization Rules
                // =========================
                .authorizeHttpRequests(auth -> auth

                        // Authentication APIs
                        .requestMatchers(
                                "/api/auth/login",
                                "/api/auth/refresh",
                                "/api/auth/forgot-password",
                                "/api/auth/reset-password"
                        ).permitAll()

                        // Registration
                        .requestMatchers(
                                "/api/users/register"
                        ).permitAll()

                        // OTP
                        .requestMatchers(
                                "/api/otp/**"
                        ).permitAll()

                        // Audit APIs
                        .requestMatchers(
                                "/api/audit/**"
                        ).authenticated()

                        // =========================
                        // Actuator
                        // =========================
                        .requestMatchers(
                                "/actuator/health"
                        ).permitAll()

                        .requestMatchers(
                                "/actuator/info"
                        ).permitAll()

                        .requestMatchers(
                                "/actuator/metrics/**"
                        ).hasAuthority("ROLE_ADMIN")

                        // =========================
                        // Swagger / OpenAPI
                        // =========================
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**"
                        ).permitAll()

                        // =========================
                        // Protected APIs
                        // =========================
                        .requestMatchers(
                                "/api/wallet/**"
                        ).authenticated()

                        .requestMatchers(
                                "/api/transactions/**"
                        ).authenticated()

                        .requestMatchers(
                                "/api/dashboard/**"
                        ).authenticated()

                        // =========================
                        // Admin APIs
                        // =========================
                        .requestMatchers(
                                "/api/users"
                        ).hasAuthority("ROLE_ADMIN")

                        // =========================
                        // Everything Else
                        // =========================
                        .anyRequest().authenticated()
                )

                // =========================
                // JWT Filter
                // =========================
                .addFilterBefore(
                        jwtFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration =
                new CorsConfiguration();

        // =========================
        // Trusted Frontend
        // =========================
        configuration.setAllowCredentials(true);

        configuration.setAllowedOrigins(
                List.of(
                        "http://localhost:3000"
                )
        );

        // =========================
        // Allowed HTTP Methods
        // =========================
        configuration.setAllowedMethods(
                List.of(
                        "GET",
                        "POST",
                        "PUT",
                        "PATCH",
                        "DELETE",
                        "OPTIONS"
                )
        );

        // =========================
        // Allowed Request Headers
        // =========================
        configuration.setAllowedHeaders(
                List.of(
                        "Authorization",
                        "Content-Type",
                        "Accept",
                        "X-Request-ID"
                )
        );

        // =========================
        // Exposed Response Headers
        // =========================
        configuration.setExposedHeaders(
                List.of(
                        "X-Request-ID"
                )
        );

        // =========================
        // Register CORS Configuration
        // =========================
        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration(
                "/**",
                configuration
        );

        return source;
    }
}