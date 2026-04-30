package com.uq.analisis.controller;

import com.uq.analisis.service.SimilarityService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/similarity")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")

public class SimilarityController {

    private final SimilarityService similarityService;

    @GetMapping("/compare")
    public Map<String, Double> compareAssets(
            @RequestParam String symbolA,
            @RequestParam String symbolB) {
        return similarityService.compareAllMetrics(symbolA,symbolB);
    }

    @PostMapping("/matrix")
    public Map<String, Map<String, Double>> getCorrelationMatrix(@RequestBody List<String> symbols) {
        return similarityService.getCorrelationMatrix(symbols);
    }


}
