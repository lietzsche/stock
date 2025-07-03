package com.stock.bion.back.calculate;

import com.stock.bion.back.data.DataService;
import com.stock.bion.back.data.Price;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrailingStopStrategyServiceTest {
	@Mock
	private DataService dataService;

	@InjectMocks
	private TrailingStopStrategyService service;

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

	private void stubPrices(List<Price> prices) {
                when(dataService.fetchPricesForTimeframe(eq("TEST"), any()))
                                .thenReturn(prices);
        }

	/** 시그널이 true 여야 하는 경우 */
	@Test
	void testIsNonHerdTrendSignal_true() {
		stubPrices(samplePrices);
		boolean result = service.isNonHerdTrendSignal("TEST", DataService.TimeFrame.SHORT_TERM);
		assertTrue(result);
	}

	/** 오늘 거래량이 최고치라 false */
	@Test
	void testIsNonHerdTrendSignal_falseOnHighVolume() {
		samplePrices.get(0).setVolume(1300);
		stubPrices(samplePrices);
		assertFalse(service.isNonHerdTrendSignal("TEST", DataService.TimeFrame.SHORT_TERM));
	}

	/** 전고점 돌파가 없어서 false */
	@Test
	void testIsNonHerdTrendSignal_falseOnNoBreakout() {
		samplePrices.get(0).setClose(100);
		stubPrices(samplePrices);
		assertFalse(service.isNonHerdTrendSignal("TEST", DataService.TimeFrame.SHORT_TERM));
	}

	@Test
        void testIsNonHerdTrendSignal_viaServiceFetch() {
                when(dataService.fetchPricesForTimeframe("TEST", DataService.TimeFrame.SHORT_TERM))
                                .thenReturn(samplePrices);

                boolean result = service.isNonHerdTrendSignal("TEST", DataService.TimeFrame.SHORT_TERM);

                assertTrue(result);
                verify(dataService, atLeastOnce())
                                .fetchPricesForTimeframe("TEST", DataService.TimeFrame.SHORT_TERM);
        }
}
