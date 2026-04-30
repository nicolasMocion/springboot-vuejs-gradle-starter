package com.uq.analisis.similarity;

import org.springframework.stereotype.Component;
import java.util.List;


@Component
public class EuclideanDistance implements SimilarityAlgorithm{

    @Override
    public double calculate(List<Double> seriesA, List<Double> seriesB) {
        if(seriesA.size() != seriesB.size()) {
            throw new IllegalArgumentException("Para la Distancia Euclidiana, las series deben tener el mismo tamaño.");
        }

        double sum = 0.0;
        for (int i = 0; i < seriesA.size(); i++) {
            double diff = seriesA.get(i) - seriesB.get(i);
            sum += diff * diff;
        }

        return Math.sqrt(sum);

    }

    @Override
    public String getName() {
        return "Distancia Euclidiana";
    }
}



