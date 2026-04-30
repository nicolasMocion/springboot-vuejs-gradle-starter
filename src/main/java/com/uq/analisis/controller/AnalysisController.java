package com.uq.analisis.controller;

import com.uq.analisis.dto.PatternReport;
import com.uq.analisis.dto.RiskReport;
import com.uq.analisis.service.RiskAndPatternService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/analysis")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AnalysisController {

    private final RiskAndPatternService service;

    @PostMapping("/risk-profile")
    public List<RiskReport> getPortfolioRisk(@RequestBody List<String> symbols) {
        return service.calculatePortfolioRisk(symbols);
    }

    @GetMapping("/patterns/{symbol}")
    public List<PatternReport> getPatterns(@PathVariable String symbol, @RequestParam(defaultValue = "3") int days) {
        return service.detectPatterns(symbol, days);
    }

    @GetMapping("/sma/{symbol}")
    public List<Double> getSMA(@PathVariable String symbol, @RequestParam(defaultValue = "20") int window) {
        return service.calculateSMA(symbol, window);
    }

}