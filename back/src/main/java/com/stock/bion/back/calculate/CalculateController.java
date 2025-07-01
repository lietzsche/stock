package com.stock.bion.back.calculate;

import com.stock.bion.back.data.Stock;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CalculateController {

    private final CalculateService calculateService;

    public CalculateController(CalculateService calculateService) {
        this.calculateService = calculateService;
    }

    @GetMapping("strategy/trailing-stop/long")
    public ResponseEntity<List<Stock>> trailingStopStrategyLongTerm() {
        return ResponseEntity.ok(calculateService.getByTrailingStopStrategy(TrailingStopStrategyService.TimeFrame.LONG_TERM));
    }

    @GetMapping("strategy/trailing-stop/middle")
    public ResponseEntity<List<Stock>> trailingStopStrategyMiddleTerm() {
        return ResponseEntity.ok(calculateService.getByTrailingStopStrategy(TrailingStopStrategyService.TimeFrame.MEDIUM_TERM));
    }

    @GetMapping("strategy/trailing-stop/short")
    public ResponseEntity<List<Stock>> trailingStopStrategyShortTerm() {
        return ResponseEntity.ok(calculateService.getByTrailingStopStrategy(TrailingStopStrategyService.TimeFrame.SHORT_TERM));
    }
}
