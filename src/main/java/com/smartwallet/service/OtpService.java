package com.smartwallet.service;

import com.smartwallet.dto.VerifyOtpRequest;
import com.smartwallet.exception.TooManyRequestsException;
import com.smartwallet.model.Otp;
import com.smartwallet.repository.OtpRepository;
import com.smartwallet.security.RateLimiterService;
import java.time.LocalDateTime;
import java.util.Random;
import org.springframework.stereotype.Service;

@Service
public class OtpService {

    private final OtpRepository otpRepository;
    private final EmailService emailService;
    private final RateLimiterService rateLimiterService;
    private final AuditService auditService;

    public OtpService(
            OtpRepository otpRepository,
            EmailService emailService,
            RateLimiterService rateLimiterService,
            AuditService auditService) {

        this.otpRepository = otpRepository;
        this.emailService = emailService;
        this.rateLimiterService = rateLimiterService;
        this.auditService = auditService;
    }

    public String sendOtp(String email) {

        if (!rateLimiterService.isAllowed(email)) {

            auditService.log(
                    email,
                    "OTP_GENERATED",
                    "BLOCKED"
            );

            throw new TooManyRequestsException(
                    "Too many OTP requests. Try again after 1 minute."
            );
        }

        String otp =
                String.valueOf(
                        100000 + new Random().nextInt(900000)
                );

        Otp existingOtp =
                otpRepository.findByEmail(email);

        if (existingOtp != null) {
            otpRepository.delete(existingOtp);
        }

        Otp otpEntity =
                new Otp(
                        email,
                        otp,
                        LocalDateTime.now().plusMinutes(5)
                );

        otpRepository.save(otpEntity);

        emailService.sendOtpEmail(
                email,
                otp
        );

        auditService.log(
                email,
                "OTP_GENERATED",
                "SUCCESS"
        );

        return "OTP sent successfully";
    }

    public String verifyOtp(
            VerifyOtpRequest request) {

        Otp otp =
                otpRepository.findByEmail(
                        request.getEmail()
                );

        if (otp == null) {

            auditService.log(
                    request.getEmail(),
                    "OTP_VERIFY",
                    "FAILED"
            );

            return "OTP not found";
        }

        if (LocalDateTime.now()
                .isAfter(otp.getExpiryTime())) {

            auditService.log(
                    request.getEmail(),
                    "OTP_VERIFY",
                    "EXPIRED"
            );

            return "OTP expired";
        }

        if (!otp.getOtpCode()
                .equals(request.getOtp())) {

            auditService.log(
                    request.getEmail(),
                    "OTP_VERIFY",
                    "FAILED"
            );

            return "Invalid OTP";
        }

        otpRepository.delete(otp);

        auditService.log(
                request.getEmail(),
                "OTP_VERIFY",
                "SUCCESS"
        );

        return "OTP verified successfully";
    }
}