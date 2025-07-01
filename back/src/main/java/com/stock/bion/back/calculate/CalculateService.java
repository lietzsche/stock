package com.stock.bion.back.calculate;

import com.stock.bion.back.data.DataService;
import com.stock.bion.back.data.Stock;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CalculateService {
    private final TrailingStopStrategyService trailingStopStrategyService;
    private final DataService dataService;

    public CalculateService(TrailingStopStrategyService trailingStopStrategyService, DataService dataService) {
        this.trailingStopStrategyService = trailingStopStrategyService;
        this.dataService = dataService;
    }

    public List<Stock> getByTrailingStopStrategy(TrailingStopStrategyService.TimeFrame timeframe) {
        return dataService.getCompanyInfo().stream()
                .filter(stock -> trailingStopStrategyService.isNonHerdTrendSignal(stock.getCode(), timeframe))
                .toList();
    }
}
