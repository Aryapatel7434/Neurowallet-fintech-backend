package com.smartwallet.service;

import com.smartwallet.dto.VerifyOtpRequest;
import com.smartwallet.model.Otp;
import com.smartwallet.repository.OtpRepository;
import java.time.LocalDateTime;
import java.util.Random;
import org.springframework.stereotype.Service;

@Service
public class OtpService {

    private final OtpRepository otpRepository;
    private final EmailService emailService;

    public OtpService(
            OtpRepository otpRepository,
            EmailService emailService) {

        this.otpRepository = otpRepository;
        this.emailService = emailService;
    }

    public String sendOtp(String email) {
        ///if user register then this method call and generate Otp
        String otp =
                String.valueOf(
                        100000 + new Random().nextInt(900000)
                );
       //Check Existing OTP
        Otp existingOtp =
                otpRepository.findByEmail(email);
       //Delete Old OTP
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

        emailService.sendOtpEmail(email, otp);

        return "OTP sent successfully";
    }

    public String verifyOtp(
            VerifyOtpRequest request) {

        Otp otp =
                otpRepository.findByEmail(
                        request.getEmail()
                );

        if (otp == null) {
            return "OTP not found";
        }

        if (LocalDateTime.now()
                .isAfter(otp.getExpiryTime())) {

            return "OTP expired";
        }

        if (!otp.getOtpCode()
                .equals(request.getOtp())) {

            return "Invalid OTP";
        }

        otpRepository.delete(otp);

        return "OTP verified successfully";
    }
}