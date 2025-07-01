package com.stock.bion.back.data;

import lombok.Data;

import java.time.LocalDate;

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
