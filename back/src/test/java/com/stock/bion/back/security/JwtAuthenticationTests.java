package com.stock.bion.back.security;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stock.bion.back.stock.StockInfo;
import com.stock.bion.back.stock.StockService;
import com.stock.bion.back.user.LoginRequest;
import com.stock.bion.back.user.UserRegistrationRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class JwtAuthenticationTests {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    StockService stockService;

    private String registerAndLogin(String username) throws Exception {
        UserRegistrationRequest request = new UserRegistrationRequest(
                username,
                "pass1",
                "pass1",
                username + "@example.com",
                "01000000000");
        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        LoginRequest loginRequest = new LoginRequest(username, "pass1");
        String response = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        JsonNode node = objectMapper.readTree(response);
        return node.get("token").asText();
    }

    @Test
    void accessWithoutTokenReturnsUnauthorized() throws Exception {
        given(stockService.fetchStock("005930"))
                .willReturn(new StockInfo("005930", 123456L));

        mockMvc.perform(get("/api/stocks/005930"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void accessWithMalformedTokenReturnsUnauthorized() throws Exception {
        given(stockService.fetchStock("005930"))
                .willReturn(new StockInfo("005930", 123456L));

        mockMvc.perform(get("/api/stocks/005930")
                .header("Authorization", "Bearer invalid"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void accessWithValidTokenReturnsData() throws Exception {
        given(stockService.fetchStock("005930"))
                .willReturn(new StockInfo("005930", 123456L));

        String token = registerAndLogin("secureuser");

        mockMvc.perform(get("/api/stocks/005930")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("005930"))
                .andExpect(jsonPath("$.price").value(123456));
    }
}
