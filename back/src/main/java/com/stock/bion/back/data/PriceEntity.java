package com.stock.bion.back.data;

import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "prices")
@Getter
@Setter
public class PriceEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String code;
	private String name;
	private LocalDate date;
	private double close;
	private double diff;
	private double open;
	private double high;
	private double low;
	private double volume;
}
