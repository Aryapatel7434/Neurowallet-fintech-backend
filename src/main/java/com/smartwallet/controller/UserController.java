package com.smartwallet.controller;

import com.smartwallet.dto.RegisterRequest;
import com.smartwallet.dto.UpdateProfileRequest;
import com.smartwallet.model.User;
import com.smartwallet.model.Wallet;
import com.smartwallet.service.UserService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.*;
import com.smartwallet.dto.UserProfileResponse;
import org.springframework.security.core.Authentication;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
@RestController
@RequestMapping("/api")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @PostMapping("/users/register")
    public String registerUser(@Valid @RequestBody RegisterRequest request) {
        return service.registerUser(request);
    }

    @GetMapping("/wallet/{email}")
    public Wallet getWallet(@PathVariable String email) {
        return service.getWalletByEmail(email);
    }

    @GetMapping("/users")
    public List<User> getAllUsers() {
        return service.getAllUsers();
    }
    
@GetMapping("/users/me")
public UserProfileResponse getMyProfile(
        Authentication authentication
) {
    if (authentication == null
            || !authentication.isAuthenticated()
            || "anonymousUser".equals(authentication.getName())) {
        throw new ResponseStatusException(
                HttpStatus.UNAUTHORIZED,
                "Authentication required"
        );
    }

    return service.getMyProfile(authentication.getName());
}

@PutMapping("/users/me")
public UserProfileResponse updateMyProfile(
        @Valid @RequestBody UpdateProfileRequest request,
        Authentication authentication
) {
    if (authentication == null
            || !authentication.isAuthenticated()
            || "anonymousUser".equals(authentication.getName())) {
        throw new ResponseStatusException(
                HttpStatus.UNAUTHORIZED,
                "Authentication required"
        );
    }

    return service.updateMyProfile(
            authentication.getName(),
            request.getName()
    );
}

}