package com.stock.bion.back.calculate;

import com.stock.bion.back.data.DataService.TimeFrame;
import com.stock.bion.back.data.Price;
import com.stock.bion.back.data.PricePersistenceService;
import com.stock.bion.back.data.Stock;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class StrategyResultService {
	private final PricePersistenceService priceService;
	private final TrailingStopStrategyService trailingStopStrategyService;
	private final MomentumIncreasingService momentumIncreasingService;
	private final StrategyResultRepository repository;

	public StrategyResultService(PricePersistenceService priceService,
			TrailingStopStrategyService trailingStopStrategyService,
			MomentumIncreasingService momentumIncreasingService, StrategyResultRepository repository) {
		this.priceService = priceService;
		this.trailingStopStrategyService = trailingStopStrategyService;
		this.momentumIncreasingService = momentumIncreasingService;
		this.repository = repository;
	}

	public void evaluateDaily() {
		LocalDate today = LocalDate.now();
		List<Stock> stocks = priceService.getStocks();
		for (Stock stock : stocks) {
			for (TimeFrame tf : TimeFrame.values()) {
				List<Price> prices = priceService.getPrices(stock.getCode(), tf);
				if (prices.size() < 4)
					continue;
				if (trailingStopStrategyService.isNonHerdTrendSignal(prices)) {
					save(stock, StrategyType.TRAILING_STOP, tf, today, 1.0);
				}
				if (momentumIncreasingService.isMomentumIncreasing(prices, 0.01, 0.0)) {
					save(stock, StrategyType.MOMENTUM, tf, today, 1.0);
				}
			}
		}
	}

	private void save(Stock stock, StrategyType type, TimeFrame tf, LocalDate date, double value) {
		StrategyResultEntity e = new StrategyResultEntity();
		e.setCode(stock.getCode());
		e.setName(stock.getName());
		e.setStrategyType(type.name());
		e.setTimeFrame(tf.name());
		e.setEvaluatedAt(date);
		e.setSignalValue(value);
		repository.save(e);
	}
}
