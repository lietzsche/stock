package com.stock.bion.reviewservice;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/review")
public class ReviewController {
    @GetMapping("/daily")
    public Map<String, Object> daily(@RequestParam String userId, @RequestParam String date) {
        return Map.of("userId", userId, "date", date, "pnl", 0);
    }

    @GetMapping("/weekly")
    public Map<String, Object> weekly(@RequestParam String userId, @RequestParam String week) {
        return Map.of("userId", userId, "week", week, "stats", "none");
    }
}
