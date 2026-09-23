package com.smartwallet.service;

import com.smartwallet.dto.AuthResponse;
import com.smartwallet.dto.LoginRequest;
import com.smartwallet.model.RefreshToken;
import com.smartwallet.model.User;
import com.smartwallet.repository.UserRepository;
import com.smartwallet.security.JwtUtil;
import com.smartwallet.security.LoginAttemptService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

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

        logger.info(
                "Login request received for email: {}",
                email
        );

        // --------------------------------------------------------
        // Check login attempt lock
        // --------------------------------------------------------

        if (loginAttemptService.isBlocked(email)) {

            logger.warn(
                    "Login blocked due to too many failed attempts for email: {}",
                    email
            );

            auditService.log(
                    email,
                    "LOGIN",
                    "BLOCKED"
            );

            throw new RuntimeException(
                    "Account temporarily locked due to too many failed login attempts"
            );
        }

        // --------------------------------------------------------
        // Find user
        // --------------------------------------------------------

        User user =
                userRepository.findByEmail(email);

        if (user == null) {

            logger.warn(
                    "Login failed. Invalid email: {}",
                    email
            );

            loginAttemptService.loginFailed(email);

            auditService.log(
                    email,
                    "LOGIN",
                    "FAILED"
            );

            throw new RuntimeException(
                    "Invalid email"
            );
        }

        // --------------------------------------------------------
        // Verify password
        // --------------------------------------------------------

        boolean passwordMatch =
                passwordEncoder.matches(
                        request.getPassword(),
                        user.getPassword()
                );

        if (!passwordMatch) {

            logger.warn(
                    "Login failed. Invalid password for email: {}",
                    email
            );

            loginAttemptService.loginFailed(email);

            auditService.log(
                    email,
                    "LOGIN",
                    "FAILED"
            );

            throw new RuntimeException(
                    "Invalid password"
            );
        }

        // --------------------------------------------------------
        // Successful login
        // --------------------------------------------------------

        loginAttemptService.loginSucceeded(email);

        auditService.log(
                email,
                "LOGIN",
                "SUCCESS"
        );

        logger.info(
                "Login successful for email: {}",
                email
        );

        // --------------------------------------------------------
        // Generate access token
        // --------------------------------------------------------

        String accessToken =
                jwtUtil.generateToken(
                        user.getEmail(),
                        user.getRole()
                );

        // --------------------------------------------------------
        // Generate refresh token
        // --------------------------------------------------------

        RefreshToken refreshToken =
                refreshTokenService.createRefreshToken(
                        user.getEmail()
                );

        // --------------------------------------------------------
        // Return authentication response
        // --------------------------------------------------------

        return new AuthResponse(
                accessToken,
                refreshToken.getToken(),
                user.getEmail(),
                user.getName()
        );
    }

    // ============================================================
    // REFRESH ACCESS TOKEN
    // ============================================================

    public AuthResponse refreshToken(
            String refreshTokenValue) {

        RefreshToken refreshToken =
                refreshTokenService.validateRefreshToken(
                        refreshTokenValue
                );

        if (refreshToken == null) {

            logger.warn(
                    "Refresh token validation failed"
            );

            throw new RuntimeException(
                    "Invalid Refresh Token"
            );
        }

        // --------------------------------------------------------
        // Find user associated with refresh token
        // --------------------------------------------------------

        User user =
                userRepository.findByEmail(
                        refreshToken.getEmail()
                );

        if (user == null) {

            logger.warn(
                    "Refresh token belongs to a non-existent user"
            );

            throw new RuntimeException(
                    "User not found"
            );
        }

        // --------------------------------------------------------
        // Generate new access token
        // --------------------------------------------------------

        String accessToken =
                jwtUtil.generateToken(
                        user.getEmail(),
                        user.getRole()
                );

        // --------------------------------------------------------
        // Return authentication response
        // --------------------------------------------------------

        return new AuthResponse(
                accessToken,
                refreshToken.getToken(),
                user.getEmail(),
                user.getName()
        );
    }
}