package com.smartwallet.controller;

import com.smartwallet.dto.OtpRequest;
import com.smartwallet.dto.VerifyOtpRequest;
import com.smartwallet.service.OtpService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/otp")
public class OtpController {

    private final OtpService otpService;

    public OtpController(OtpService otpService) {
        this.otpService = otpService;
    }

    @PostMapping("/send")
    public String sendOtp(
            @RequestBody OtpRequest request) {

        return otpService.sendOtp(
                request.getEmail()
        );
    }

    @PostMapping("/verify")
    public String verifyOtp(
            @RequestBody VerifyOtpRequest request) {

        return otpService.verifyOtp(request);
    }
}