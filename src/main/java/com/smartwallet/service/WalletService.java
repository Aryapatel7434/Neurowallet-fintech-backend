package com.smartwallet.service;

import com.smartwallet.dto.AddMoneyRequest;
import com.smartwallet.dto.WithdrawRequest;
import com.smartwallet.exception.BadRequestException;
import com.smartwallet.exception.ResourceNotFoundException;
import com.smartwallet.model.Wallet;
import com.smartwallet.repository.WalletRepository;
import java.math.BigDecimal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WalletService {

    private final WalletRepository walletRepository;

    public WalletService(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    public Wallet getMyWallet() {

        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        return walletRepository.findByUserEmail(email);
    }

    @Transactional
    public Wallet addMoney(AddMoneyRequest request) {

        if (request.getAmount() == null ||
                request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {

            throw new BadRequestException("Amount must be greater than zero");
        }

        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        Wallet wallet = walletRepository.findByUserEmail(email);

        if (wallet == null) {
            throw new ResourceNotFoundException("Wallet not found");
        }

        wallet.setBalance(
                wallet.getBalance().add(request.getAmount())
        );

        return walletRepository.save(wallet);
    }

    @Transactional
    public Wallet withdrawMoney(WithdrawRequest request) {

        if (request.getAmount() == null ||
                request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {

            throw new BadRequestException("Withdraw amount must be greater than zero");
        }

        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        Wallet wallet = walletRepository.findByUserEmail(email);

        if (wallet == null) {
            throw new ResourceNotFoundException("Wallet not found");
        }

        if (wallet.getBalance().compareTo(request.getAmount()) < 0) {
            throw new BadRequestException("Insufficient wallet balance");
        }

        wallet.setBalance(
                wallet.getBalance().subtract(request.getAmount())
        );

        return walletRepository.save(wallet);
    }
}