package com.stock.bion.back.calculate;

import com.stock.bion.back.data.DataService;
import com.stock.bion.back.data.PricePersistenceService;
import com.stock.bion.back.data.Price;
import com.stock.bion.back.data.Stock;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/strategies")
public class StrategyApiController {
    private final PricePersistenceService priceService;
    private final TrailingStopStrategyService trailingStopStrategyService;
    private final MomentumIncreasingService momentumIncreasingService;

    public StrategyApiController(PricePersistenceService priceService,
                                 TrailingStopStrategyService trailingStopStrategyService,
                                 MomentumIncreasingService momentumIncreasingService) {
        this.priceService = priceService;
        this.trailingStopStrategyService = trailingStopStrategyService;
        this.momentumIncreasingService = momentumIncreasingService;
    }

    @GetMapping("/trailing-stop/short")
    public ResponseEntity<List<Stock>> trailingStopShort() {
        return ResponseEntity.ok(applyTrailing(DataService.TimeFrame.SHORT_TERM));
    }

    @GetMapping("/trailing-stop/middle")
    public ResponseEntity<List<Stock>> trailingStopMiddle() {
        return ResponseEntity.ok(applyTrailing(DataService.TimeFrame.MEDIUM_TERM));
    }

    @GetMapping("/trailing-stop/long")
    public ResponseEntity<List<Stock>> trailingStopLong() {
        return ResponseEntity.ok(applyTrailing(DataService.TimeFrame.LONG_TERM));
    }

    @GetMapping("/momentum/short")
    public ResponseEntity<List<Stock>> momentumShort() {
        return ResponseEntity.ok(applyMomentum(DataService.TimeFrame.SHORT_TERM));
    }

    @GetMapping("/momentum/middle")
    public ResponseEntity<List<Stock>> momentumMiddle() {
        return ResponseEntity.ok(applyMomentum(DataService.TimeFrame.MEDIUM_TERM));
    }

    @GetMapping("/momentum/long")
    public ResponseEntity<List<Stock>> momentumLong() {
        return ResponseEntity.ok(applyMomentum(DataService.TimeFrame.LONG_TERM));
    }

    private List<Stock> applyTrailing(DataService.TimeFrame timeframe) {
        List<Stock> stocks = priceService.getStocks();
        List<Stock> result = new ArrayList<>();
        for (Stock stock : stocks) {
            List<Price> prices = priceService.getPrices(stock.getCode(), timeframe);
            if (prices.size() < 4) continue;
            if (trailingStopStrategyService.isNonHerdTrendSignal(prices)) {
                result.add(stock);
            }
        }
        return result;
    }

    private List<Stock> applyMomentum(DataService.TimeFrame timeframe) {
        List<Stock> stocks = priceService.getStocks();
        List<Stock> result = new ArrayList<>();
        for (Stock stock : stocks) {
            List<Price> prices = priceService.getPrices(stock.getCode(), timeframe);
            if (prices.size() < 4) continue;
            if (momentumIncreasingService.isMomentumIncreasing(prices, 0.01, 0.0)) {
                result.add(stock);
            }
        }
        return result;
    }
}
