package com.smartwallet.service;

import com.smartwallet.model.Wallet;
import com.smartwallet.repository.UserRepository;
import com.smartwallet.repository.WalletRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WalletServiceMockitoTest {

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private UserRepository userRepository;

    @Test
    void shouldMockRepositoryCall() {

        Wallet wallet = new Wallet();
        wallet.setBalance(BigDecimal.valueOf(1000));

        when(walletRepository.findByUserEmail("arya@gmail.com"))
                .thenReturn(wallet);

        Wallet result =
                walletRepository.findByUserEmail("arya@gmail.com");

        assertNotNull(result);
        assertEquals(
                BigDecimal.valueOf(1000),
                result.getBalance()
        );

        verify(walletRepository)
                .findByUserEmail("arya@gmail.com");
    }
}