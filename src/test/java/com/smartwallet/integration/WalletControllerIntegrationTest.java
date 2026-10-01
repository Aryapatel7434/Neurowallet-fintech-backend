package com.smartwallet.controller;

import com.smartwallet.model.User;
import com.smartwallet.model.Wallet;
import com.smartwallet.repository.UserRepository;
import com.smartwallet.repository.WalletRepository;
import com.smartwallet.security.JwtUtil;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class WalletControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setup() {

        User user =
                userRepository.findByEmail(
                        "testuser@gmail.com"
                );

        if (user == null) {

            user = new User();

            user.setName("Test User");
            user.setEmail("testuser@gmail.com");
            user.setPassword(
                    passwordEncoder.encode("password123")
            );
            user.setRole("ROLE_USER");

            user = userRepository.save(user);

        } else {

            /*
             * Keep integration-test user role
             * consistent with application security.
             */
            user.setRole("ROLE_USER");

            if (user.getPassword() == null) {
                user.setPassword(
                        passwordEncoder.encode("password123")
                );
            }

            userRepository.save(user);
        }

        Wallet wallet =
                walletRepository.findByUserEmail(
                        "testuser@gmail.com"
                );

        if (wallet == null) {

            wallet = new Wallet();

            wallet.setUser(user);
            wallet.setBalance(
                    new BigDecimal("10000")
            );
            wallet.setCurrency("INR");
            wallet.setStatus("ACTIVE");

            walletRepository.save(wallet);
        }
    }

    @Test
    void shouldRejectRequestWithoutToken() throws Exception {

        String json = """
        {
            "amount":500
        }
        """;

        mockMvc.perform(
                post("/api/wallet/add-money")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
        )
        .andExpect(status().isForbidden());
    }

    @Test
    void shouldAddMoneyWithValidToken() throws Exception {

        String token =
                jwtUtil.generateToken(
                        "testuser@gmail.com",
                        "USER"
                );

        String json = """
        {
            "amount":500
        }
        """;

        mockMvc.perform(
                post("/api/wallet/add-money")
                        .header(
                                "Authorization",
                                "Bearer " + token
                        )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
        )
        .andExpect(status().isOk());
    }
}