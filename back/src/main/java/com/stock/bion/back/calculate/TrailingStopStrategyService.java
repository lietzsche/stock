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
	 * 조회 기간(TimeFrame)을 나타내는 열거형입니다. SHORT_TERM: 단기(약 1개월) LONG_TERM : 장기(약 1년)
	 */
	public enum TimeFrame {
		SHORT_TERM, // 약 1개월
		MEDIUM_TERM,
		LONG_TERM; // 약 1년

		/**
		 * 각 기간별 대략적인 거래일 수를 반환합니다.
		 */
		public int getLookbackDays() {
            return switch (this) {
                case SHORT_TERM -> 22; // 약 1개월
                case MEDIUM_TERM -> 120; // 약 1년
                case LONG_TERM -> 250; // 약 1년
                default -> throw new IllegalArgumentException("지원하지 않는 기간: " + this);
            };
		}
	}

	/**
	 * 주어진 종목 코드와 조회 기간에 대해 소신파 상승 추세 시그널 여부를 확인합니다.
	 */
	public boolean isNonHerdTrendSignal(String code, TimeFrame timeframe) {
		List<Price> prices = fetchPricesForTimeframe(code, timeframe);
		return isNonHerdTrendSignal(prices);
	}

	/**
	 * 조회 기간(TimeFrame)에 따라 과거 가격 데이터를 페이지별로 조회해 반환합니다.
	 */
	private List<Price> fetchPricesForTimeframe(String code, TimeFrame timeframe) {
		int days = timeframe.getLookbackDays();
		List<Price> allPrices = new ArrayList<>();
		int page = 1;
		while (allPrices.size() < days) {
			List<Price> pagePrices = dataService.getPriceInfo(code, page++);
			if (pagePrices.isEmpty()) {
				break;
			}
			allPrices.addAll(pagePrices);
		}
		// API가 최신 데이터를 먼저 반환한다고 가정하고 최근 'days'개만 반환
		return allPrices.stream().limit(days).toList();
	}

	/**
	 * 핵심 시그널 로직:
	 * 1) 오늘 거래량이 전체 기간 중 최고치가 아니어야 합니다. (거래량의 최고점을 피함)
	 * 2) 오늘 종가가 이전 최고 종가를 돌파해야 합니다.
	 * 3) 최근 3일 동안 저가와 고가가 모두 연속 상승해야 합니다.
	 */
	private boolean isNonHerdTrendSignal(List<Price> prices) {
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

	private boolean isMomentumIncreasing(List<Price> prices, double alpha, double beta) {
		List<Double> d1 = computeDailyReturns(prices);               // 1차
		List<Double> d2 = new ArrayList<>();
		for (int i = 1; i < d1.size(); i++) d2.add(d1.get(i-1) - d1.get(i));

		// 당일 포함 최근 3일 검사
		for (int i = 0; i < 3; i++) {
			if (d1.get(i) < alpha) return false;
			if (d2.get(i) < beta ) return false;
		}
		return true;
	}

	/**
	 * 가격 리스트로부터 일일 수익률(변화율)을 계산해 반환합니다.
	 */
	public List<Double> computeDailyReturns(List<Price> prices) {
		List<Double> returns = new ArrayList<>();
		for (int i = 1; i < prices.size(); i++) {
			double prevClose = prices.get(i).getClose();
			double currClose = prices.get(i - 1).getClose();
			returns.add((currClose - prevClose) / prevClose);
		}
		return returns;
	}
}
