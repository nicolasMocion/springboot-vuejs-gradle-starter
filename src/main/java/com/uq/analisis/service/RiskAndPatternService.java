package com.uq.analisis.service;

import com.uq.analisis.dto.PatternReport;
import com.uq.analisis.dto.RiskReport;
import com.uq.analisis.model.FinancialAsset;
import com.uq.analisis.Repository.FinancialAssetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RiskAndPatternService {

    private final FinancialAssetRepository repository;
    private static final int TRADING_DAYS_PER_YEAR = 252; // Días hábiles de bolsa

    // ==========================================
    // PARTE 1: CÁLCULO DE RIESGO Y VOLATILIDAD
    // ==========================================
    public List<RiskReport> calculatePortfolioRisk(List<String> symbols) {
        List<RiskReport> portfolioRisk = new ArrayList<>();

        for (String symbol : symbols) {
            List<FinancialAsset> data = repository.findAllBySymbolOrderByDateAsc(symbol);
            if (data == null || data.size() < 2) continue;

            // 1. Calcular retornos diarios: (Precio Hoy - Precio Ayer) / Precio Ayer
            List<Double> returns = new ArrayList<>();
            for (int i = 1; i < data.size(); i++) {
                double today = data.get(i).getClose();
                double yesterday = data.get(i - 1).getClose();
                returns.add((today - yesterday) / yesterday);
            }

            // 2. Media de retornos
            double mean = returns.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);

            // 3. Varianza y Desviación Estándar (Riesgo Diario)
            double sumSquaredDiffs = 0.0;
            for (double r : returns) {
                sumSquaredDiffs += Math.pow(r - mean, 2);
            }
            double variance = sumSquaredDiffs / returns.size();
            double dailyStdDev = Math.sqrt(variance);

            // 4. Volatilidad Histórica Anualizada
            double annualVolatility = dailyStdDev * Math.sqrt(TRADING_DAYS_PER_YEAR);

            // 5. Clasificación Algorítmica del Perfil de Riesgo
            String profile;
            if (annualVolatility < 0.15) { // Menor al 15%
                profile = "Conservador";
            } else if (annualVolatility <= 0.30) { // Entre 15% y 30%
                profile = "Moderado";
            } else { // Mayor al 30%
                profile = "Agresivo";
            }

            portfolioRisk.add(new RiskReport(symbol, dailyStdDev, annualVolatility, profile));
        }

        // 6. Ordenar algorítmicamente del más seguro al más riesgoso
        portfolioRisk.sort((a, b) -> Double.compare(a.annualizedVolatility(), b.annualizedVolatility()));

        return portfolioRisk;
    }

    // ==========================================
    // PARTE 2: ALGORITMO DE VENTANA DESLIZANTE
    // ==========================================
    public List<PatternReport> detectPatterns(String symbol, int consecutiveDaysConfig) {
        List<FinancialAsset> data = repository.findAllBySymbolOrderByDateAsc(symbol);
        List<PatternReport> reports = new ArrayList<>();

        if (data == null || data.size() < Math.max(consecutiveDaysConfig, 4)) return reports;

        int bullishStreakCount = 0;
        int vReversalCount = 0;

        // Sliding Window O(n)
        for (int i = 0; i <= data.size() - consecutiveDaysConfig; i++) {

            // Patrón 1: Racha Alcista (N días seguidos subiendo)
            boolean isBullishStreak = true;
            for (int j = 1; j < consecutiveDaysConfig; j++) {
                if (data.get(i + j).getClose() <= data.get(i + j - 1).getClose()) {
                    isBullishStreak = false;
                    break;
                }
            }
            if (isBullishStreak) bullishStreakCount++;
        }

        // Sliding Window O(n) para el patrón formalizado "Reversión en V" (Tamaño ventana fija = 5 días / 4 movimientos)
        for (int i = 0; i <= data.size() - 5; i++) {
            double p0 = data.get(i).getClose();
            double p1 = data.get(i+1).getClose();
            double p2 = data.get(i+2).getClose();
            double p3 = data.get(i+3).getClose();
            double p4 = data.get(i+4).getClose();

            if (p1 < p0 && p2 < p1 && p3 > p2 && p4 > p3) {
                vReversalCount++;
            }
        }

        reports.add(new PatternReport("Racha Alcista (" + consecutiveDaysConfig + " días)", bullishStreakCount, "Secuencia de " + consecutiveDaysConfig + " días de incrementos consecutivos en el precio de cierre."));
        reports.add(new PatternReport("Reversión en V", vReversalCount, "Patrón formal: 2 caídas consecutivas seguidas inmediatamente por 2 subidas consecutivas. Indica rebote técnico local."));

        return reports;
    }

    public List<Double> calculateSMA(String symbol, int windowSize) {
        List<FinancialAsset> data = repository.findAllBySymbolOrderByDateAsc(symbol);
        List<Double> sma = new ArrayList<>();

        if (data == null || data.isEmpty()) return sma;

        // Algoritmo Sliding Window para SMA
        double sum = 0.0;
        for (int i = 0; i < data.size(); i++) {
            sum += data.get(i).getClose();

            // Si aún no tenemos suficientes días para la ventana completa, guardamos null o 0
            if (i < windowSize - 1) {
                sma.add(null); // Usamos null para que la gráfica no dibuje la línea al inicio
            } else {
                // Calcular promedio y remover el elemento más antiguo de la suma para la siguiente iteración
                sma.add(sum / windowSize);
                sum -= data.get(i - windowSize + 1).getClose();
            }
        }
        return sma;
    }
}