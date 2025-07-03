package com.stock.bion.back.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class PricePersistenceService {
	private final PriceRepository priceRepository;
	private final DataService dataService;

	public PricePersistenceService(PriceRepository priceRepository, DataService dataService) {
		this.priceRepository = priceRepository;
		this.dataService = dataService;
	}

	public void fetchAndSavePricesForAllCompanies() {
		List<Stock> stocks = dataService.getCompanyInfo();
		for (Stock s : stocks) {
			fetchAndSavePrices(s);
		}
	}

	public void fetchAndSavePrices(Stock stock) {
		List<Price> prices = dataService.fetchPricesForTimeframe(stock.getCode(), DataService.TimeFrame.SHORT_TERM);
		for (Price p : prices) {
			if (!priceRepository.existsByCodeAndDate(stock.getCode(), p.getDate())) {
				PriceEntity e = toEntity(stock, p);
				priceRepository.save(e);
			}
		}
	}

	private PriceEntity toEntity(Stock stock, Price p) {
		PriceEntity e = new PriceEntity();
		e.setCode(stock.getCode());
		e.setName(stock.getName());
		e.setDate(p.getDate());
		e.setClose(p.getClose());
		e.setDiff(p.getDiff());
		e.setOpen(p.getOpen());
		e.setHigh(p.getHigh());
		e.setLow(p.getLow());
		e.setVolume(p.getVolume());
		return e;
	}

	private Price toPrice(PriceEntity e) {
		Price p = new Price();
		p.setDate(e.getDate());
		p.setClose(e.getClose());
		p.setDiff(e.getDiff());
		p.setOpen(e.getOpen());
		p.setHigh(e.getHigh());
		p.setLow(e.getLow());
		p.setVolume(e.getVolume());
		return p;
	}

	public List<Price> getPrices(String code, DataService.TimeFrame timeframe) {
		List<PriceEntity> list = priceRepository.findByCodeOrderByDateDesc(code);
		int days = timeframe.getLookbackDays();
		return list.stream().limit(days).map(this::toPrice).toList();
	}

	public List<Stock> getStocks() {
		List<PriceEntity> all = priceRepository.findAll();
		Map<String, String> map = new HashMap<>();
		for (PriceEntity e : all) {
			map.putIfAbsent(e.getCode(), e.getName());
		}
		List<Stock> stocks = new ArrayList<>();
		for (Map.Entry<String, String> entry : map.entrySet()) {
			Stock s = new Stock();
			s.setCode(entry.getKey());
			s.setName(entry.getValue());
			stocks.add(s);
		}
		return stocks;
	}
}
