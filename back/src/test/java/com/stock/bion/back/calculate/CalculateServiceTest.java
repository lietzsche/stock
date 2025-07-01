package com.stock.bion.back.calculate;

import com.stock.bion.back.data.DataService;
import com.stock.bion.back.data.Stock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * CalculateService#getByTrailingStopStrategy(TimeFrame) 단위 테스트
 *
 * – DataService.getCompanyInfo()  ➜ 상장 종목 리스트 제공 (mock)
 * – TrailingStopStrategyService.isNonHerdTrendSignal() ➜ 종목별 필터 여부 (mock)
 *
 * 기대 : true 를 리턴한 종목만 결과에 포함
 */
@ExtendWith(MockitoExtension.class)
class CalculateServiceTest {

    @Mock
    private DataService dataService;

    @Mock
    private TrailingStopStrategyService trailingStopStrategyService;

    @InjectMocks
    private CalculateService calculateService;

    private List<Stock> companyList;

    @BeforeEach
    void setUp() {
        companyList = List.of(
                makeStock("AAA", "AAA Corp."),
                makeStock("BBB", "BBB Inc."),
                makeStock("CCC", "CCC Ltd.")
        );
    }

    private Stock makeStock(String code, String name) {
        Stock s = new Stock();
        s.setCode(code);
        s.setName(name);
        return s;
    }

    @Test
    void getByTrailingStopStrategy_filters_by_signal() {
        // 1. 상장 종목 목록 stub
        when(dataService.getCompanyInfo()).thenReturn(companyList);

        // 2. 시그널 : AAA = true, BBB/CCC = false
        when(trailingStopStrategyService.isNonHerdTrendSignal(eq("AAA"),
                any(TrailingStopStrategyService.TimeFrame.class))).thenReturn(true);
        when(trailingStopStrategyService.isNonHerdTrendSignal(eq("BBB"),
                any())).thenReturn(false);
        when(trailingStopStrategyService.isNonHerdTrendSignal(eq("CCC"),
                any())).thenReturn(false);

        // 3. 호출
        List<Stock> result = calculateService.getByTrailingStopStrategy(
                TrailingStopStrategyService.TimeFrame.SHORT_TERM);

        // 4. 검증
        assertEquals(1, result.size(), "시그널이 true 인 종목만 반환해야 한다");
        assertEquals("AAA", result.get(0).getCode());

        // 5. 상호작용 검증 (선택)
        verify(dataService).getCompanyInfo();
        verify(trailingStopStrategyService, times(1))
                .isNonHerdTrendSignal("AAA", TrailingStopStrategyService.TimeFrame.SHORT_TERM);
        verify(trailingStopStrategyService, times(1))
                .isNonHerdTrendSignal("BBB", TrailingStopStrategyService.TimeFrame.SHORT_TERM);
        verify(trailingStopStrategyService, times(1))
                .isNonHerdTrendSignal("CCC", TrailingStopStrategyService.TimeFrame.SHORT_TERM);
        verifyNoMoreInteractions(dataService, trailingStopStrategyService);
    }
}