package com.stock.bion.back.calculate;

import com.stock.bion.back.data.DataService;
import com.stock.bion.back.data.Price;
import com.stock.bion.back.data.Stock;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MomentumIncreasingService {

    private final DataService dataService;

    public MomentumIncreasingService(DataService dataService) {
        this.dataService = dataService;
    }

    public List<Stock> getByMomentumIncreasing(DataService.TimeFrame timeframe) {
        return dataService.getCompanyInfo().stream()
                .filter(stock -> isNonHerdTrendSignal(stock.getCode(), timeframe)).toList();
    }

    /**
     * 주어진 종목 코드와 조회 기간에 대해 상승 추세 모멘텀 여부를 확인합니다.
     */
    public boolean isNonHerdTrendSignal(String code, DataService.TimeFrame timeframe) {
        List<Price> prices = dataService.fetchPricesForTimeframe(code, timeframe);
        double alpha = 0.01; // 최소 일간 수익률 +1 %
        double beta = 0.0; // 가속도 0 이상
        return isMomentumIncreasing(prices, alpha, beta);
    }

    private boolean isMomentumIncreasing(List<Price> prices, double alpha, double beta) {
        List<Double> d1 = computeDailyReturns(prices); // 1차
        List<Double> d2 = new ArrayList<>();
        for (int i = 1; i < d1.size(); i++)
            d2.add(d1.get(i) - d1.get(i - 1)); // ★ 방향 교정

        for (int i = 0; i < 3; i++) {
            if (d1.get(i) < alpha)
                return false;
            if (d2.get(i) < beta)
                return false;
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
