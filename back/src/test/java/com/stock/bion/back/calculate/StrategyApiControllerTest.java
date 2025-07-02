package com.stock.bion.back.calculate;

import com.stock.bion.back.data.PriceEntity;
import com.stock.bion.back.data.PriceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = "spring.task.scheduling.enabled=false")
@AutoConfigureMockMvc
class StrategyApiControllerTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    PriceRepository priceRepository;

    @BeforeEach
    void setup() {
        priceRepository.deleteAll();
        insertSample("AAA", "AAA Corp");
    }

    private void insertSample(String code, String name) {
        LocalDate today = LocalDate.now();
        priceRepository.save(make(code, name, today, 110, 1000));
        priceRepository.save(make(code, name, today.minusDays(1), 105, 800));
        priceRepository.save(make(code, name, today.minusDays(2), 100, 900));
        priceRepository.save(make(code, name, today.minusDays(3), 95, 1100));
        priceRepository.save(make(code, name, today.minusDays(4), 90, 1200));
    }

    private PriceEntity make(String code, String name, LocalDate date, double close, double volume) {
        PriceEntity e = new PriceEntity();
        e.setCode(code);
        e.setName(name);
        e.setDate(date);
        e.setClose(close);
        e.setHigh(close);
        e.setLow(close - 10);
        e.setOpen(close - 5);
        e.setVolume(volume);
        e.setDiff(0);
        return e;
    }

    @Test
    void trailingStopShort_returnsData() throws Exception {
        mockMvc.perform(get("/api/strategies/trailing-stop/short").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].code").value("AAA"));
    }
}
