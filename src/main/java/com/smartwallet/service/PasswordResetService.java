package com.smartwallet.service;

import com.smartwallet.model.PasswordResetToken;
import com.smartwallet.model.User;
import com.smartwallet.repository.PasswordResetTokenRepository;
import com.smartwallet.repository.UserRepository;
import jakarta.mail.MessagingException;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PasswordResetService {

    private final PasswordResetTokenRepository tokenRepository;

    private final UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder;

    private final EmailService emailService;

    public PasswordResetService(

            PasswordResetTokenRepository tokenRepository,

            UserRepository userRepository,

            BCryptPasswordEncoder passwordEncoder,

            EmailService emailService

    ) {

        this.tokenRepository = tokenRepository;

        this.userRepository = userRepository;

        this.passwordEncoder = passwordEncoder;

        this.emailService = emailService;

    }

    /* ==========================================
            Generate Reset Token
    ========================================== */

    public String generateResetToken(String email) throws MessagingException {

        User user = userRepository.findByEmail(email);

        if (user == null) {

            throw new RuntimeException("User not found");

        }

        String token = UUID.randomUUID().toString();

        PasswordResetToken resetToken =
                new PasswordResetToken();

        resetToken.setEmail(email);

        resetToken.setToken(token);

        resetToken.setExpiryDate(

                LocalDateTime.now().plusMinutes(15)

        );

        tokenRepository.save(resetToken);

        /* ==========================
             Send Email
        ========================== */

        emailService.sendPasswordResetEmail(

                user.getEmail(),

                token

        );

        return token;

    }

    /* ==========================================
            Reset Password
    ========================================== */

    public void resetPassword(

            String token,

            String newPassword

    ) {

        PasswordResetToken resetToken =
                tokenRepository.findByToken(token);

        if (resetToken == null) {

            throw new RuntimeException("Invalid token");

        }

        if (resetToken.getExpiryDate()
                .isBefore(LocalDateTime.now())) {

            throw new RuntimeException("Token expired");

        }

        User user = userRepository.findByEmail(

                resetToken.getEmail()

        );

        if (user == null) {

            throw new RuntimeException("User not found");

        }

        user.setPassword(

                passwordEncoder.encode(newPassword)

        );

        userRepository.save(user);

        tokenRepository.delete(resetToken);

    }

}