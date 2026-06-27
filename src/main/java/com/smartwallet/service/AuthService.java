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

    public AuthResponse login(LoginRequest request) {

        System.out.println(
                "EMAIL RECEIVED = " +
                        request.getEmail()
        );

        System.out.println(
                "ALL USERS IN DB = " +
                        userRepository.findAll().size()
        );

        logger.info(
                "Login request received for email: {}",
                request.getEmail()
        );

        if (loginAttemptService.isBlocked(request.getEmail())) {

            logger.warn(
                    "Login blocked due to too many failed attempts for email: {}",
                    request.getEmail()
            );

            auditService.log(
                    request.getEmail(),
                    "LOGIN",
                    "BLOCKED"
            );

            throw new RuntimeException(
                    "Account temporarily locked due to too many failed login attempts"
            );
        }

       System.out.println("EMAIL FROM REQUEST = [" + request.getEmail() + "]");

User user = userRepository.findByEmail(
        request.getEmail()
);

System.out.println("USER = " + user);

        System.out.println(
                "USER OBJECT = " +
                        user
        );

        System.out.println(
                "USER FOUND = " +
                        (user != null)
        );

        if (user == null) {

            logger.warn(
                    "Login failed. Invalid email: {}",
                    request.getEmail()
            );

            loginAttemptService.loginFailed(
                    request.getEmail()
            );

            auditService.log(
                    request.getEmail(),
                    "LOGIN",
                    "FAILED"
            );

            throw new RuntimeException(
                    "Invalid email"
            );
        }

        System.out.println("RAW PASSWORD = " + request.getPassword());
System.out.println("DB HASH = " + user.getPassword());

boolean passwordMatch =
    passwordEncoder.matches(
        request.getPassword(),
        user.getPassword()
    );

System.out.println("PASSWORD MATCH = " + passwordMatch);

       

        if (!passwordMatch) {

            logger.warn(
                    "Login failed. Invalid password for email: {}",
                    request.getEmail()
            );

            loginAttemptService.loginFailed(
                    request.getEmail()
            );

            auditService.log(
                    request.getEmail(),
                    "LOGIN",
                    "FAILED"
            );

            throw new RuntimeException(
                    "Invalid password"
            );
        }

        loginAttemptService.loginSucceeded(
                request.getEmail()
        );

        auditService.log(
                request.getEmail(),
                "LOGIN",
                "SUCCESS"
        );

        logger.info(
                "Login successful for email: {}",
                request.getEmail()
        );

        String accessToken =
                jwtUtil.generateToken(
                        user.getEmail(),
                        user.getRole()
                );

        RefreshToken refreshToken =
                refreshTokenService.createRefreshToken(
                        user.getEmail()
                );

     return new AuthResponse(
    accessToken,
    refreshToken.getToken(),
    user.getEmail(),
    user.getName()
);
    }
    public AuthResponse refreshToken(
            String refreshTokenValue) {

        RefreshToken refreshToken =
                refreshTokenService.validateRefreshToken(
                        refreshTokenValue
                );

        if (refreshToken == null) {

            throw new RuntimeException(
                    "Invalid Refresh Token"
            );
        }

        User user =
                userRepository.findByEmail(
                        refreshToken.getEmail()
                );

        if (user == null) {

            throw new RuntimeException(
                    "User not found"
            );
        }

        String accessToken =
                jwtUtil.generateToken(
                        user.getEmail(),
                        user.getRole()
                );

   return new AuthResponse(
    accessToken,
    refreshToken.getToken(),
    user.getEmail(),
    user.getName()
);
    }
}