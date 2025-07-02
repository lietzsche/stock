package com.stock.bion.back.data;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class PriceScheduler {
    private final PricePersistenceService pricePersistenceService;

    public PriceScheduler(PricePersistenceService pricePersistenceService) {
        this.pricePersistenceService = pricePersistenceService;
    }

    @Scheduled(cron = "0 0 1 * * *")
    public void collectDailyPrices() {
        pricePersistenceService.fetchAndSavePricesForAllCompanies();
    }
}
