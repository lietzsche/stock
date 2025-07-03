package com.stock.bion.back.calculate;

import com.stock.bion.back.calculate.StrategyResultEntity;
import com.stock.bion.back.calculate.StrategyResultRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = "spring.task.scheduling.enabled=false")
@AutoConfigureMockMvc
class StrategyApiControllerTest {
	@Autowired
	MockMvc mockMvc;
	@Autowired
	StrategyResultRepository resultRepository;

	@BeforeEach
	void setup() {
		resultRepository.deleteAll();
		insertSample("AAA", "AAA Corp");
	}

	private void insertSample(String code, String name) {
		StrategyResultEntity e = new StrategyResultEntity();
		e.setCode(code);
		e.setName(name);
		e.setStrategyType("TRAILING_STOP");
		e.setTimeFrame("SHORT_TERM");
		e.setEvaluatedAt(LocalDate.now());
		e.setSignalValue(1.0);
		resultRepository.save(e);
	}

	@Test
	@WithMockUser
	void trailingStopShort_returnsData() throws Exception {
		mockMvc.perform(get("/api/strategies/trailing-stop/short").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(jsonPath("$[0].code").value("AAA"));
	}
}
