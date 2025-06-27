package com.stock.bion.marketpoller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class StockController {
    @GetMapping("/api/stocks/{code}")
    public Map<String, Object> getStock(@PathVariable String code) {
        return Map.of("code", code, "price", 100.0);
    }
}
