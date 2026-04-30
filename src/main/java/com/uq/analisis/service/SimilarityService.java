package com.uq.analisis.service;

import com.uq.analisis.model.FinancialAsset;
import com.uq.analisis.Repository.FinancialAssetRepository;
import com.uq.analisis.similarity.SimilarityAlgorithm;
import com.uq.analisis.similarity.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SimilarityService {

    private final FinancialAssetRepository repository;
    private final List<SimilarityAlgorithm> algorithms; // Inyecta los 4 algoritmos automáticamente

    public Map<String, Double> compareAllMetrics(String symbolA, String symbolB) {
        List<FinancialAsset> dataA = repository.findAllBySymbolOrderByDateAsc(symbolA);
        List<FinancialAsset> dataB = repository.findAllBySymbolOrderByDateAsc(symbolB);

        List<Double> pricesA = new ArrayList<>();
        List<Double> pricesB = new ArrayList<>();

        // Alinear tamaños (usamos el tamaño del menor para evitar OutOfBounds)
        int minSize = Math.min(dataA.size(), dataB.size());
        for (int i = 0; i < minSize; i++) {
            pricesA.add(dataA.get(i).getClose());
            pricesB.add(dataB.get(i).getClose());
        }

        // Ejecutar los 4 algoritmos y guardar los resultados
        Map<String, Double> results = new HashMap<>();
        for (SimilarityAlgorithm algo : algorithms) {
            results.put(algo.getName(), algo.calculate(pricesA, pricesB));
        }

        return results;
    }

    public Map<String, Map<String, Double>> getCorrelationMatrix(List<String> symbols) {
        Map<String, Map<String, Double>> matrix = new HashMap<>();
        PearsonCorrelation pearson = new PearsonCorrelation();

        for (String symA : symbols) {
            Map<String, Double> row = new HashMap<>();
            List<FinancialAsset> dataA = repository.findAllBySymbolOrderByDateAsc(symA);
            List<Double> pricesA = dataA.stream().map(FinancialAsset::getClose).toList();

            for (String symB : symbols) {
                // Optimización: La correlación de A con A es 1.0
                if (symA.equals(symB)) {
                    row.put(symB, 1.0);
                    continue;
                }

                List<FinancialAsset> dataB = repository.findAllBySymbolOrderByDateAsc(symB);
                List<Double> pricesB = dataB.stream().map(FinancialAsset::getClose).toList();

                // Alinear tamaños
                int minSize = Math.min(pricesA.size(), pricesB.size());
                if (minSize == 0) {
                    row.put(symB, 0.0);
                    continue;
                }

                double correlation = pearson.calculate(
                        pricesA.subList(0, minSize),
                        pricesB.subList(0, minSize)
                );
                row.put(symB, correlation);
            }
            matrix.put(symA, row);
        }
        return matrix;
    }
}