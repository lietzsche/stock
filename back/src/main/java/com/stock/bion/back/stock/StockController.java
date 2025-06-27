package com.stock.bion.back.stock;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/stocks")
@RequiredArgsConstructor
public class StockController {
    private final StockService stockService;

    @GetMapping("/{code}")
    public ResponseEntity<StockInfo> getStock(@PathVariable String code) throws Exception {
        return ResponseEntity.ok(stockService.fetchStock(code));
    }
}
