package com.smartwallet.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import static org.springframework.security.config.Customizer.withDefaults;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

    http
    .csrf(csrf -> csrf.disable())

    .authorizeHttpRequests(auth -> auth

        .requestMatchers("/api/auth/login").permitAll()
        .requestMatchers("/api/users/register").permitAll()
        .requestMatchers("/api/wallet/**").permitAll()

        .requestMatchers("/api/users")
            .hasAuthority("ROLE_ADMIN")

        .requestMatchers("/api/transactions/**")
            .authenticated()

        .anyRequest().authenticated()
    )

    .addFilterBefore(jwtFilter,
            UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}