
package com.smartwallet.service;

import java.math.BigDecimal;
import java.util.List;

import com.smartwallet.dto.RegisterRequest;
import com.smartwallet.dto.UserProfileResponse;
import com.smartwallet.exception.ResourceNotFoundException;
import com.smartwallet.model.User;
import com.smartwallet.model.Wallet;
import com.smartwallet.repository.UserRepository;
import com.smartwallet.repository.WalletRepository;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRepository,
            WalletRepository walletRepository,
            BCryptPasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.walletRepository = walletRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public String registerUser(RegisterRequest request) {

        String email = request.getEmail().trim();

        User existingUser = userRepository.findByEmail(email);

        if (existingUser != null) {
            return "User already exists";
        }

        String encodedPassword =
                passwordEncoder.encode(request.getPassword());

        User user = new User(
                request.getName().trim(),
                email,
                encodedPassword,
                "ROLE_USER"
        );

        User savedUser = userRepository.save(user);

        Wallet wallet = new Wallet();
        wallet.setUser(savedUser);
        wallet.setBalance(BigDecimal.ZERO);
        wallet.setCurrency("INR");
        wallet.setStatus("ACTIVE");

        walletRepository.save(wallet);

        return "User registered successfully";
    }

    @Transactional(readOnly = true)
    public Wallet getWalletByEmail(String email) {

        User user = userRepository.findByEmail(email);

        if (user == null) {
            throw new ResourceNotFoundException(
                    "User not found"
            );
        }

        return walletRepository.findByUserUserId(
                user.getUserId()
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional(readOnly = true)
    public UserProfileResponse getMyProfile(String email) {

        User user = userRepository.findByEmail(email);

        if (user == null) {
            throw new ResourceNotFoundException(
                    "User not found"
            );
        }

        return toProfileResponse(user);
    }

    @Transactional
    public UserProfileResponse updateMyProfile(
            String email,
            String newName) {

        if (newName == null) {
            throw new IllegalArgumentException(
                    "Name is required"
            );
        }

        String normalizedName = newName.trim();

        if (normalizedName.length() < 2
                || normalizedName.length() > 100) {
            throw new IllegalArgumentException(
                    "Name must contain between 2 and 100 characters"
            );
        }

        User user = userRepository.findByEmail(email);

        if (user == null) {
            throw new ResourceNotFoundException(
                    "User not found"
            );
        }

        user.setName(normalizedName);

        // The entity is managed in this transaction.
        // JPA dirty checking persists the name change.
        return toProfileResponse(user);
    }

    private UserProfileResponse toProfileResponse(User user) {

        return new UserProfileResponse(
                user.getUserId(),
                user.getName(),
                user.getEmail(),
                user.getRole()
        );
    }
}
