
package com.smartwallet.security;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

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
            .cors(cors -> {
            })

            .csrf(csrf -> csrf.disable())

            .exceptionHandling(exception -> exception
                .authenticationEntryPoint((request, response, authException) -> {
                    response.setStatus(HttpStatus.UNAUTHORIZED.value());
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    response.setCharacterEncoding("UTF-8");
                    response.getWriter().write(
                        "{\"success\":false,\"message\":\"Authentication required\"}"
                    );
                })
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    response.setStatus(HttpStatus.FORBIDDEN.value());
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    response.setCharacterEncoding("UTF-8");
                    response.getWriter().write(
                        "{\"success\":false,\"message\":\"Access denied\"}"
                    );
                })
            )

            .authorizeHttpRequests(auth -> auth

                // Authentication APIs
                .requestMatchers(
                    "/api/auth/login",
                    "/api/auth/refresh",
                    "/api/auth/logout",
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

                // Actuator
                .requestMatchers(
                    "/actuator/health",
                    "/actuator/info"
                ).permitAll()

                .requestMatchers(
                    "/actuator/metrics/**"
                ).hasAuthority("ROLE_ADMIN")

                // Swagger / OpenAPI
                .requestMatchers(
                    "/swagger-ui/**",
                    "/swagger-ui.html",
                    "/v3/api-docs/**"
                ).permitAll()

                // Current user's profile
                .requestMatchers(
                    "/api/users/me"
                ).authenticated()

                // Protected wallet APIs
                .requestMatchers(
                    "/api/wallet/**"
                ).authenticated()

                // Protected transaction APIs
                .requestMatchers(
                    "/api/transactions/**"
                ).authenticated()

                // Protected dashboard APIs
                .requestMatchers(
                    "/api/dashboard/**"
                ).authenticated()

                // Admin-only user listing
                .requestMatchers(
                    "/api/users"
                ).hasAuthority("ROLE_ADMIN")

                // All remaining endpoints require authentication
                .anyRequest().authenticated()
            )

            .addFilterBefore(
                jwtFilter,
                UsernamePasswordAuthenticationFilter.class
            );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowCredentials(true);

        configuration.setAllowedOrigins(
            List.of("http://localhost:3000")
        );

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

        configuration.setAllowedHeaders(
            List.of(
                "Authorization",
                "Content-Type",
                "Accept",
                "X-Request-ID"
            )
        );

        configuration.setExposedHeaders(
            List.of("X-Request-ID")
        );

        UrlBasedCorsConfigurationSource source =
            new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration(
            "/**",
            configuration
        );

        return source;
    }
}
