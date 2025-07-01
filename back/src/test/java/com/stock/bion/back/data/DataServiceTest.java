package com.stock.bion.back.data;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DataServiceTest {
	private final DataService dataService = new DataService();

	@Test
	void getCompanyInfo_정상적으로_종목을_가져온다() {
		List<Stock> stocks = dataService.getCompanyInfo();

		assertNotNull(stocks);
		assertFalse(stocks.isEmpty());
		assertTrue(stocks.stream().allMatch(s -> s.getCode() != null && !s.getCode().isBlank()));
	}

	@Test
	void isIdentifier_코스피_또는_코스닥_종목을_제대로_판별한다() {
		String samsung = "005930"; // 코스피
		String kakao = "035720"; // 코스닥 → 코스피로 이전됨 (현재도 true)

		assertTrue(dataService.isIdentifier(samsung));
		assertTrue(dataService.isIdentifier(kakao));
	}

	@Test
	void getPriceInfo_가격정보를_정상적으로_불러온다() {
		String code = "005930"; // 삼성전자
		int page = 1;

		List<Price> prices = dataService.getPriceInfo(code, page);

		assertNotNull(prices);
		assertFalse(prices.isEmpty());
		assertNotNull(prices.get(0).getDate());
		assertTrue(prices.get(0).getClose() > 0);
	}
}
