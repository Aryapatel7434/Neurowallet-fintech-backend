package com.smartwallet.service;

import com.smartwallet.dto.LoginRequest;
import com.smartwallet.model.User;
import com.smartwallet.repository.UserRepository;
import com.smartwallet.security.JwtUtil;
import com.smartwallet.security.LoginAttemptService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder;
    private final LoginAttemptService loginAttemptService;
    private final AuditService auditService;

    public AuthService(UserRepository userRepository,
                       JwtUtil jwtUtil,
                       BCryptPasswordEncoder passwordEncoder,
                       LoginAttemptService loginAttemptService,
                       AuditService auditService) {

        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
        this.loginAttemptService = loginAttemptService;
        this.auditService = auditService;
    }

    public String login(LoginRequest request) {

        if (loginAttemptService.isBlocked(request.getEmail())) {

            auditService.log(
                    request.getEmail(),
                    "LOGIN",
                    "BLOCKED"
            );

            return "Account temporarily locked due to too many failed login attempts";
        }

        User user = userRepository.findByEmail(request.getEmail());

        if (user == null) {

            loginAttemptService.loginFailed(request.getEmail());

            auditService.log(
                    request.getEmail(),
                    "LOGIN",
                    "FAILED"
            );

            return "Invalid email";
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {

            loginAttemptService.loginFailed(request.getEmail());

            auditService.log(
                    request.getEmail(),
                    "LOGIN",
                    "FAILED"
            );

            return "Invalid password";
        }

        loginAttemptService.loginSucceeded(request.getEmail());

        auditService.log(
                request.getEmail(),
                "LOGIN",
                "SUCCESS"
        );

        return jwtUtil.generateToken(
                user.getEmail(),
                user.getRole()
        );
    }
}