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
public class TransactionControllerIntegrationTest {

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

        // =========================================================
        // SENDER USER
        // =========================================================

        User sender =
                userRepository.findByEmail(
                        "integration@test.com"
                );

        if (sender == null) {

            sender = new User();

            sender.setName("Integration User");
            sender.setEmail("integration@test.com");

            sender.setPassword(
                    passwordEncoder.encode("123456")
            );

            sender.setRole("ROLE_USER");

            sender = userRepository.save(sender);

        } else {

            sender.setRole("ROLE_USER");

            if (sender.getPassword() == null) {
                sender.setPassword(
                        passwordEncoder.encode("123456")
                );
            }

            userRepository.save(sender);
        }

        // =========================================================
        // SENDER WALLET
        // =========================================================

        Wallet senderWallet =
                walletRepository.findByUserEmail(
                        "integration@test.com"
                );

        if (senderWallet == null) {

            senderWallet = new Wallet();

            senderWallet.setUser(sender);

            senderWallet.setBalance(
                    new BigDecimal("10000")
            );

            senderWallet.setCurrency("INR");
            senderWallet.setStatus("ACTIVE");

            walletRepository.save(senderWallet);
        }

        // =========================================================
        // RECEIVER USER
        // =========================================================

        User receiver =
                userRepository.findByEmail(
                        "rahul@gmail.com"
                );

        if (receiver == null) {

            receiver = new User();

            receiver.setName("Rahul");
            receiver.setEmail("rahul@gmail.com");

            receiver.setPassword(
                    passwordEncoder.encode("123456")
            );

            receiver.setRole("ROLE_USER");

            receiver = userRepository.save(receiver);

        } else {

            receiver.setRole("ROLE_USER");

            if (receiver.getPassword() == null) {
                receiver.setPassword(
                        passwordEncoder.encode("123456")
                );
            }

            userRepository.save(receiver);
        }

        // =========================================================
        // RECEIVER WALLET
        // =========================================================

        Wallet receiverWallet =
                walletRepository.findByUserEmail(
                        "rahul@gmail.com"
                );

        if (receiverWallet == null) {

            receiverWallet = new Wallet();

            receiverWallet.setUser(receiver);

            receiverWallet.setBalance(
                    new BigDecimal("5000")
            );

            receiverWallet.setCurrency("INR");
            receiverWallet.setStatus("ACTIVE");

            walletRepository.save(receiverWallet);
        }
    }

    @Test
    void shouldSendMoney() throws Exception {

        String token =
                jwtUtil.generateToken(
                        "integration@test.com",
                        "USER"
                );

        String json = """
        {
            "receiverEmail":"rahul@gmail.com",
            "amount":100,
            "category":"FOOD"
        }
        """;

        mockMvc.perform(
                post("/api/transactions/send")
                        .header(
                                "Authorization",
                                "Bearer " + token
                        )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
        )
        .andExpect(status().isOk());
    }

    @Test
    void shouldGetTransactionHistory() throws Exception {

        String token =
                jwtUtil.generateToken(
                        "integration@test.com",
                        "USER"
                );

        mockMvc.perform(
                get("/api/transactions/history")
                        .header(
                                "Authorization",
                                "Bearer " + token
                        )
        )
        .andExpect(status().isOk());
    }
}