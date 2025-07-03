package com.stock.bion.back.calculate;

import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "strategy_results")
@Getter
@Setter
public class StrategyResultEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String code;
    private String name;
    private String strategyType;
    private String timeFrame;
    private LocalDate evaluatedAt;
    private Double signalValue;
}
