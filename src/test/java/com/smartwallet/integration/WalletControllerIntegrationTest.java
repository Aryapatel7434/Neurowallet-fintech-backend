package com.smartwallet.controller;

import com.smartwallet.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class WalletControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtil jwtUtil;

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