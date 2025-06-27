package com.stock.bion.tradeexecutor;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class TradeController {
    @PostMapping("/trades/execute")
    public Map<String, Object> execute(@RequestBody Map<String, Object> req) {
        return Map.of("status", "executed", "input", req);
    }
}
