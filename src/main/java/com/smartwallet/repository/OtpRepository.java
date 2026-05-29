package com.smartwallet.repository;

import com.smartwallet.model.Otp;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OtpRepository
        extends JpaRepository<Otp, Long> {

    Otp findByEmail(String email);
}