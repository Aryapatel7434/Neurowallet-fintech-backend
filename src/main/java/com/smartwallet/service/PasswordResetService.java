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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@Service
public class PasswordResetService {

    private final PasswordResetTokenRepository tokenRepository;

    private final UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder;

    private final EmailService emailService;
private static final Logger log =LoggerFactory.getLogger(PasswordResetService.class);
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

    log.info("Password reset requested. Checking account.");

    User user = userRepository.findByEmail(email);

    if (user == null) {
        log.warn("Password reset stopped: account was not found.");
        return null;
    }

    log.info("Account found. Generating password reset token.");

    String token = UUID.randomUUID().toString();

    PasswordResetToken resetToken = new PasswordResetToken();
    resetToken.setEmail(email);
    resetToken.setToken(token);
    resetToken.setExpiryDate(LocalDateTime.now().plusMinutes(15));

    tokenRepository.save(resetToken);

    log.info("Password reset token saved. Starting email delivery.");

    emailService.sendPasswordResetEmail(user.getEmail(), token);

    log.info("Password reset email service completed successfully.");

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
        Object email = null;
 
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