package com.smartwallet.integration;

import com.smartwallet.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

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

    @Test
    void shouldSendMoney() throws Exception {

        String token =
                jwtUtil.generateToken(
                        "arya@gmail.com",
                        "USER"
                );

        String json = """
        {
            "receiverEmail":"rahul@gmail.com",
            "amount":100
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
                        "arya@gmail.com",
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