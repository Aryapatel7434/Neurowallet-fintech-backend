
package com.smartwallet.service;

import com.smartwallet.dto.AuthResponse;
import com.smartwallet.dto.LoginRequest;
import com.smartwallet.exception.UnauthorizedException;
import com.smartwallet.model.RefreshToken;
import com.smartwallet.model.User;
import com.smartwallet.repository.UserRepository;
import com.smartwallet.security.JwtUtil;
import com.smartwallet.security.LoginAttemptService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private static final Logger logger =
            LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder;
    private final LoginAttemptService loginAttemptService;
    private final AuditService auditService;
    private final RefreshTokenService refreshTokenService;

    public AuthService(
            UserRepository userRepository,
            JwtUtil jwtUtil,
            BCryptPasswordEncoder passwordEncoder,
            LoginAttemptService loginAttemptService,
            AuditService auditService,
            RefreshTokenService refreshTokenService) {

        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
        this.loginAttemptService = loginAttemptService;
        this.auditService = auditService;
        this.refreshTokenService = refreshTokenService;
    }

    // ============================================================
    // LOGIN
    // ============================================================

    public AuthResponse login(LoginRequest request) {

        String email = request.getEmail();

        logger.info("Login request received");

        // Check login attempt lock
        if (loginAttemptService.isBlocked(email)) {

            logger.warn("Login blocked due to too many failed attempts");

            auditService.log(email, "LOGIN", "BLOCKED");

            throw new UnauthorizedException(
                    "Account temporarily locked due to too many failed login attempts"
            );
        }

        // Find user
        User user = userRepository.findByEmail(email);

        if (user == null) {

            logger.warn("Login failed. Invalid credentials");

            loginAttemptService.loginFailed(email);
            auditService.log(email, "LOGIN", "FAILED");

            throw new UnauthorizedException(
                    "Invalid email or password"
            );
        }

        // Verify password
        boolean passwordMatch = passwordEncoder.matches(
                request.getPassword(),
                user.getPassword()
        );

        if (!passwordMatch) {

            logger.warn("Login failed. Invalid credentials");

            loginAttemptService.loginFailed(email);
            auditService.log(email, "LOGIN", "FAILED");

            throw new UnauthorizedException(
                    "Invalid email or password"
            );
        }

        // Successful login
        loginAttemptService.loginSucceeded(email);
        auditService.log(email, "LOGIN", "SUCCESS");

        logger.info("Login successful");

        // Generate access token
        String accessToken = jwtUtil.generateToken(
                user.getEmail(),
                user.getRole()
        );

        // Generate refresh token
        RefreshToken refreshToken =
                refreshTokenService.createRefreshToken(
                        user.getEmail()
                );

        // Return authentication response
        return new AuthResponse(
                accessToken,
                refreshToken.getToken(),
                user.getEmail(),
                user.getName(),
                user.getRole()
        );
    }

    // ============================================================
    // REFRESH ACCESS TOKEN WITH ATOMIC TOKEN ROTATION
    // ============================================================

    @Transactional
    public AuthResponse refreshToken(String refreshTokenValue) {

        // 1. Validate existing refresh token
        RefreshToken oldRefreshToken =
                refreshTokenService.validateRefreshToken(
                        refreshTokenValue
                );

        if (oldRefreshToken == null) {

            logger.warn("Refresh token validation failed");

            throw new UnauthorizedException(
                    "Invalid or expired refresh token"
            );
        }

        // 2. Find associated user
        User user = userRepository.findByEmail(
                oldRefreshToken.getEmail()
        );

        if (user == null) {

            logger.warn("Refresh token user not found");

            throw new UnauthorizedException(
                    "Invalid refresh token"
            );
        }

        // 3. Revoke old refresh token
        int deletedRecords =
                refreshTokenService.revokeRefreshToken(
                        oldRefreshToken.getToken()
                );

        if (deletedRecords != 1) {

            logger.warn("Refresh token revocation failed");

            throw new UnauthorizedException(
                    "Refresh token could not be revoked"
            );
        }

        // 4. Generate new access token
        String newAccessToken = jwtUtil.generateToken(
                user.getEmail(),
                user.getRole()
        );

        // 5. Generate replacement refresh token
        RefreshToken newRefreshToken =
                refreshTokenService.createRefreshToken(
                        user.getEmail()
                );

        logger.info("Refresh token rotated successfully");

        // 6. Return new authentication response
        return new AuthResponse(
                newAccessToken,
                newRefreshToken.getToken(),
                user.getEmail(),
                user.getName(),
                user.getRole()
        );
    }
}
