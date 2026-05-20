package com.smartwallet.controller;

import com.smartwallet.dto.RegisterRequest;
import com.smartwallet.model.User;
import com.smartwallet.model.Wallet;
import com.smartwallet.service.UserService;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @PostMapping("/users/register")
    public String registerUser(@RequestBody RegisterRequest request) {
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
}