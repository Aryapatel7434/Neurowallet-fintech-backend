
package com.smartwallet.service;

import com.smartwallet.model.RefreshToken;
import com.smartwallet.repository.RefreshTokenRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshTokenService(
            RefreshTokenRepository refreshTokenRepository) {

        this.refreshTokenRepository = refreshTokenRepository;
    }

    public RefreshToken createRefreshToken(String email) {

        RefreshToken refreshToken = new RefreshToken();

        refreshToken.setEmail(email);

        refreshToken.setToken(
                UUID.randomUUID().toString()
        );

        refreshToken.setExpiryDate(
                LocalDateTime.now().plusDays(7)
        );

        return refreshTokenRepository.save(refreshToken);
    }

    public RefreshToken findByToken(String token) {

        return refreshTokenRepository.findByToken(token);
    }

    @Transactional
    public RefreshToken validateRefreshToken(String token) {

        if (token == null || token.isBlank()) {
            return null;
        }

        RefreshToken refreshToken =
                refreshTokenRepository.findByToken(token);

        if (refreshToken == null) {
            return null;
        }

        if (refreshToken.getExpiryDate() == null
                || !refreshToken.getExpiryDate()
                        .isAfter(LocalDateTime.now())) {

            refreshTokenRepository.deleteByToken(token);
            return null;
        }

        return refreshToken;
    }

    @Transactional
    public void revokeRefreshToken(String token) {

        if (token == null || token.isBlank()) {
            return;
        }

        refreshTokenRepository.deleteByToken(token);
    }
}
