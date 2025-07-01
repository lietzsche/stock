package com.stock.bion.back.calculate;

import com.stock.bion.back.data.DataService;
import com.stock.bion.back.data.Stock;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CalculateController {

	private final CalculateService calculateService;
	private final MomentumIncreasingService momentumIncreasingService;

	public CalculateController(CalculateService calculateService, MomentumIncreasingService momentumIncreasingService) {
		this.calculateService = calculateService;
		this.momentumIncreasingService = momentumIncreasingService;
	}

	@GetMapping("strategy/trailing-stop/long")
	public ResponseEntity<List<Stock>> trailingStopStrategyLongTerm() {
		return ResponseEntity.ok(calculateService.getByTrailingStopStrategy(DataService.TimeFrame.LONG_TERM));
	}

	@GetMapping("strategy/trailing-stop/middle")
	public ResponseEntity<List<Stock>> trailingStopStrategyMiddleTerm() {
		return ResponseEntity.ok(calculateService.getByTrailingStopStrategy(DataService.TimeFrame.MEDIUM_TERM));
	}

	@GetMapping("strategy/trailing-stop/short")
	public ResponseEntity<List<Stock>> trailingStopStrategyShortTerm() {
		return ResponseEntity.ok(calculateService.getByTrailingStopStrategy(DataService.TimeFrame.SHORT_TERM));
	}

	@GetMapping("strategy/momentum/long")
	public ResponseEntity<List<Stock>> momentumStrategyLongTerm() {
		return ResponseEntity.ok(momentumIncreasingService.getByMomentumIncreasing(DataService.TimeFrame.LONG_TERM));
	}

	@GetMapping("strategy/momentum/middle")
	public ResponseEntity<List<Stock>> momentumStrategyMiddleTerm() {
		return ResponseEntity.ok(momentumIncreasingService.getByMomentumIncreasing(DataService.TimeFrame.LONG_TERM));
	}

	@GetMapping("strategy/momentum/short")
	public ResponseEntity<List<Stock>> momentumStrategyShortTerm() {
		return ResponseEntity.ok(momentumIncreasingService.getByMomentumIncreasing(DataService.TimeFrame.LONG_TERM));
	}
}
