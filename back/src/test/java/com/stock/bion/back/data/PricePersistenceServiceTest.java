package com.stock.bion.back.data;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PricePersistenceServiceTest {
	@Mock
	private PriceRepository priceRepository;
	@Mock
	private DataService dataService;
	@InjectMocks
	private PricePersistenceService service;

	private Stock stock;
	private List<Price> prices;

	@BeforeEach
	void setup() {
		stock = new Stock();
		stock.setCode("AAA");
		stock.setName("AAA Corp");
		Price p = new Price();
		p.setDate(LocalDate.now());
		prices = List.of(p);
	}

	@Test
    void fetchAndSavePrices_saves_when_not_exists() {
        when(dataService.fetchPricesForTimeframe(eq("AAA"), any())).thenReturn(prices);
        when(priceRepository.existsByCodeAndDate(eq("AAA"), any())).thenReturn(false);

        service.fetchAndSavePrices(stock);

        verify(priceRepository, times(1)).save(any(PriceEntity.class));
    }
}
