package com.smartwallet.controller;

import com.smartwallet.dto.LoginRequest;
import com.smartwallet.service.AuthService;
import com.smartwallet.security.RateLimiterService;
import com.smartwallet.exception.TooManyRequestsException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final RateLimiterService rateLimiterService;

    public AuthController(
            AuthService authService,
            RateLimiterService rateLimiterService) {

        this.authService = authService;
        this.rateLimiterService = rateLimiterService;
    }

    @PostMapping("/login")
    public String login(@RequestBody LoginRequest request) {

        String key = "login:" + request.getEmail();

        if (!rateLimiterService.isAllowed(key)) {
            throw new TooManyRequestsException(
                    "Too many login requests. Try again after 1 minute."
            );
        }

        return authService.login(request);
    }
}