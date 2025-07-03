package com.stock.bion.back.data;

import com.stock.bion.back.calculate.StrategyResultService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class PriceScheduler {
    private final PricePersistenceService pricePersistenceService;
    private final StrategyResultService strategyResultService;

    public PriceScheduler(PricePersistenceService pricePersistenceService,
                         StrategyResultService strategyResultService) {
        this.pricePersistenceService = pricePersistenceService;
        this.strategyResultService = strategyResultService;
    }

    @Scheduled(cron = "0 0 1 * * *")
    public void collectDailyPrices() {
        pricePersistenceService.fetchAndSavePricesForAllCompanies();
        strategyResultService.evaluateDaily();
    }
}
