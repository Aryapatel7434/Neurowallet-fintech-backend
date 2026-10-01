package com.smartwallet.integration;

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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @BeforeEach
    void setup() {

        User user =
                userRepository.findByEmail(
                        "integration@test.com"
                );

        if (user == null) {

            user = new User();

            user.setName("Integration User");
            user.setEmail("integration@test.com");
            user.setPassword(
                    passwordEncoder.encode("123456")
            );
            user.setRole("ROLE_USER");

            user = userRepository.save(user);

        } else {

            /*
             * Normalize role for integration testing.
             */
            user.setRole("ROLE_USER");

            if (user.getPassword() == null) {
                user.setPassword(
                        passwordEncoder.encode("123456")
                );
            }

            userRepository.save(user);
        }

        Wallet wallet =
                walletRepository.findByUserEmail(
                        "integration@test.com"
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
    void shouldAccessSwaggerPage() throws Exception {

        mockMvc.perform(
                get("/swagger-ui/index.html")
        )
        .andExpect(status().isOk());
    }

    @Test
    void shouldAddMoneyWithValidToken() throws Exception {

        String token =
                jwtUtil.generateToken(
                        "integration@test.com",
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