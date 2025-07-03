package com.stock.bion.back.calculate;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StrategyResultRepository extends JpaRepository<StrategyResultEntity, Long> {
	StrategyResultEntity findFirstByStrategyTypeAndTimeFrameOrderByEvaluatedAtDesc(String strategyType,
			String timeFrame);
	List<StrategyResultEntity> findByStrategyTypeAndTimeFrameAndEvaluatedAt(String strategyType, String timeFrame,
			LocalDate evaluatedAt);
}
