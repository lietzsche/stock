package com.stock.bion.back.data;

import java.time.LocalDate;
import lombok.Data;

@Data
public class Price {
	private LocalDate date;
	private double close;
	private double diff;
	private double open;
	private double high;
	private double low;
	private double volume;
}
