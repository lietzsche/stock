package com.stock.bion.signalengine;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import org.springframework.http.MediaType;

@WebMvcTest(SignalController.class)
class SignalEngineTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void runSignal() throws Exception {
        String json = "{\"userId\":\"u1\",\"date\":\"2025-07-01\"}";
        mockMvc.perform(post("/api/v1/signals/run")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("run"));
    }
}
