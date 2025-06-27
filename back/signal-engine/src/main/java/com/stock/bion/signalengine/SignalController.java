package com.stock.bion.signalengine;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class SignalController {
    @PostMapping("/signals/run")
    public Map<String, Object> runSignal(@RequestBody Map<String, Object> req) {
        return Map.of("status", "run", "input", req);
    }
}
