package com.uq.analisis.dto;

public record RiskReport(
        String symbol,
        double dailyStdDev,
        double annualizedVolatility,
        String riskProfile
) {}