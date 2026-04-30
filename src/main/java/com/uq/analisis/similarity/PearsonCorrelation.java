package com.uq.analisis.similarity;

import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class PearsonCorrelation implements SimilarityAlgorithm{

    @Override
    public double calculate(List<Double> seriesA, List<Double> seriesB) {
        if (seriesA.size() != seriesB.size()) {
            throw new IllegalArgumentException("Para Pearson, las series deben tener el mismo tamaño.");
        }

        int n = seriesA.size();
        double sumX = 0.0, sumY = 0.0, sumXY = 0.0;
        double sumX2 = 0.0, sumY2 = 0.0;

        for (int i = 0; i < n; i++) {
            double x = seriesA.get(i);
            double y = seriesB.get(i);

            sumX += x;
            sumY += y;
            sumXY += x * y;
            sumX2 += x * x;
            sumY2 += y * y;
        }

        double numerator = (n * sumXY) - (sumX * sumY);
        double denominator = Math.sqrt((n * sumX2 - sumX * sumX) * (n * sumY2 - sumY * sumY));

        if (denominator == 0) return 0.0;

        return numerator / denominator;
    }

    @Override
    public String getName() {
        return "Correlación de Pearson";
    }
}
