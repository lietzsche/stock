package com.stock.bion.back.calculate;

import com.stock.bion.back.data.DataService.TimeFrame;
import com.stock.bion.back.data.Stock;
import com.stock.bion.back.calculate.StrategyResultRepository;
import com.stock.bion.back.calculate.StrategyResultEntity;
import com.stock.bion.back.calculate.StrategyType;
import java.time.LocalDate;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/strategies")
public class StrategyApiController {
    private final StrategyResultRepository resultRepository;

    public StrategyApiController(StrategyResultRepository resultRepository) {
        this.resultRepository = resultRepository;
    }

    @GetMapping("/trailing-stop/short")
    public ResponseEntity<List<Stock>> trailingStopShort() {
        return ResponseEntity.ok(fetch(StrategyType.TRAILING_STOP, TimeFrame.SHORT_TERM));
    }

    @GetMapping("/trailing-stop/middle")
    public ResponseEntity<List<Stock>> trailingStopMiddle() {
        return ResponseEntity.ok(fetch(StrategyType.TRAILING_STOP, TimeFrame.MEDIUM_TERM));
    }

    @GetMapping("/trailing-stop/long")
    public ResponseEntity<List<Stock>> trailingStopLong() {
        return ResponseEntity.ok(fetch(StrategyType.TRAILING_STOP, TimeFrame.LONG_TERM));
    }

    @GetMapping("/momentum/short")
    public ResponseEntity<List<Stock>> momentumShort() {
        return ResponseEntity.ok(fetch(StrategyType.MOMENTUM, TimeFrame.SHORT_TERM));
    }

    @GetMapping("/momentum/middle")
    public ResponseEntity<List<Stock>> momentumMiddle() {
        return ResponseEntity.ok(fetch(StrategyType.MOMENTUM, TimeFrame.MEDIUM_TERM));
    }

    @GetMapping("/momentum/long")
    public ResponseEntity<List<Stock>> momentumLong() {
        return ResponseEntity.ok(fetch(StrategyType.MOMENTUM, TimeFrame.LONG_TERM));
    }

    private List<Stock> fetch(StrategyType type, TimeFrame tf) {
        StrategyResultEntity latest =
                resultRepository.findFirstByStrategyTypeAndTimeFrameOrderByEvaluatedAtDesc(type.name(), tf.name());
        if (latest == null) {
            return List.of();
        }
        LocalDate date = latest.getEvaluatedAt();
        List<StrategyResultEntity> results =
                resultRepository.findByStrategyTypeAndTimeFrameAndEvaluatedAt(type.name(), tf.name(), date);
        return results.stream().map(this::toStock).toList();
    }

    private Stock toStock(StrategyResultEntity e) {
        Stock s = new Stock();
        s.setCode(e.getCode());
        s.setName(e.getName());
        return s;
    }
}
