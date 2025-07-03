package com.stock.bion.back.calculate;

import com.stock.bion.back.data.DataService;
import com.stock.bion.back.data.Price;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TrailingStopStrategyService {
	private final DataService dataService;

	public TrailingStopStrategyService(DataService dataService) {
		this.dataService = dataService;
	}

	/**
	 * 주어진 종목 코드와 조회 기간에 대해 소신파 상승 추세 시그널 여부를 확인합니다.
	 */
	public boolean isNonHerdTrendSignal(String code, DataService.TimeFrame timeframe) {
		List<Price> prices = dataService.fetchPricesForTimeframe(code, timeframe);
		return isNonHerdTrendSignal(prices);
	}

	/**
	 * 핵심 시그널 로직: 1) 오늘 거래량이 전체 기간 중 최고치가 아니어야 합니다. (거래량의 최고점을 피함) 2) 오늘 종가가 이전 최고
	 * 종가를 돌파해야 합니다. 3) 최근 3일 동안 저가와 고가가 모두 연속 상승해야 합니다.
	 */
	public boolean isNonHerdTrendSignal(List<Price> prices) {
		if (prices.size() < 4) {
			return false;
		}
		// prices.get(0): 오늘, get(1): 어제, ...
		Price today = prices.get(0);

		// 1) 오늘 거래량이 전체 기간 중 최고치가 아니어야 함
		double maxVolume = prices.stream().mapToDouble(Price::getVolume).max().orElse(Double.MIN_VALUE);
		if (today.getVolume() >= maxVolume) {
			return false;
		}

		// 2) 오늘 종가가 이전 최고 종가를 돌파해야 함
		double prevMaxClose = prices.stream().skip(1).mapToDouble(Price::getClose).max().orElse(Double.MIN_VALUE);
		if (today.getClose() <= prevMaxClose) {
			return false;
		}

		// 3) 최근 3일(인덱스 3, 2, 1)의 저가와 고가가 연속 상승해야 함
		for (int i = 3; i >= 1; i--) {
			Price prev = prices.get(i);
			Price next = prices.get(i - 1);
			if (next.getLow() <= prev.getLow() || next.getHigh() <= prev.getHigh()) {
				return false;
			}
		}

		return true;
	}
}
