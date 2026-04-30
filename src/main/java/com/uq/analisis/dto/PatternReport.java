package com.uq.analisis.dto;

public record PatternReport(
        String patternName,
        int occurrences,
        String description
) {}