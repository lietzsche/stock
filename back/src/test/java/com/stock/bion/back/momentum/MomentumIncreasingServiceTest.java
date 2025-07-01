package com.stock.bion.back.momentum;

import com.stock.bion.back.calculate.MomentumIncreasingService;
import com.stock.bion.back.data.Price;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class MomentumIncreasingServiceTest {

	@InjectMocks
	private MomentumIncreasingService service;

	private List<Price> samplePrices;

	@BeforeEach
	void setup() {
		samplePrices = new ArrayList<>();
		samplePrices.add(makePrice(LocalDate.now(), 110, 100, 115, 95, 1000)); // 오늘
		samplePrices.add(makePrice(LocalDate.now().minusDays(1), 105, 95, 110, 90, 800)); // 1일 전
		samplePrices.add(makePrice(LocalDate.now().minusDays(2), 100, 90, 105, 85, 900)); // 2일 전
		samplePrices.add(makePrice(LocalDate.now().minusDays(3), 95, 85, 100, 80, 1100)); // 3일 전
		samplePrices.add(makePrice(LocalDate.now().minusDays(4), 90, 80, 95, 75, 1200)); // 4일 전
	}

	private Price makePrice(LocalDate date, double close, double low, double high, double open, double volume) {
		Price p = new Price();
		p.setDate(date);
		p.setClose(close);
		p.setLow(low);
		p.setHigh(high);
		p.setOpen(open);
		p.setVolume(volume);
		return p;
	}

	@Test
	void testComputeDailyReturns() {
		List<Double> returns = service.computeDailyReturns(samplePrices);
		assertEquals(4, returns.size(), "리턴 리스트 크기 확인");

		// 기대 수익률을 변수로 계산
		double expected0 = (samplePrices.get(0).getClose() - samplePrices.get(1).getClose())
				/ samplePrices.get(1).getClose();
		double expected1 = (samplePrices.get(1).getClose() - samplePrices.get(2).getClose())
				/ samplePrices.get(2).getClose();
		double expected2 = (samplePrices.get(2).getClose() - samplePrices.get(3).getClose())
				/ samplePrices.get(3).getClose();
		double expected3 = (samplePrices.get(3).getClose() - samplePrices.get(4).getClose())
				/ samplePrices.get(4).getClose();

		assertEquals(expected0, returns.get(0), 1e-6, "오늘/어제 수익률");
		assertEquals(expected1, returns.get(1), 1e-6, "어제/2일전 수익률");
		assertEquals(expected2, returns.get(2), 1e-6, "2일전/3일전 수익률");
		assertEquals(expected3, returns.get(3), 1e-6, "3일전/4일전 수익률");
	}

}
