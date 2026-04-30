package com.uq.analisis.similarity;

import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class CosineSimilarity implements  SimilarityAlgorithm {


    @Override
    public double calculate(List<Double> seriesA, List<Double> seriesB) {
        if (seriesA.size() != seriesB.size()){
            throw new IllegalArgumentException("Para similitud pro coseno, las series deben tener el mismo tamaño");
        }

        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;

        for (int i = 0; i < seriesA.size(); i++) {
            double a = seriesA.get(i);
            double b = seriesB.get(i);

            dotProduct += a * b;
            normA += a * a;
            normB += b * b;
        }

        if(normA == 0.0 || normB == 0.0){
            return 0.0; //Evitar divicion por cero si una serie es completamente  plana
        }

        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }

    @Override
    public String getName() {
        return "Similitud por Coseno";
    }
}
