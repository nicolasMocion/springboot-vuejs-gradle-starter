package com.uq.analisis.similarity;

import java.util.List;

public interface SimilarityAlgorithm {
    /**
     * Calcula la similitud o distancia entre dos series de tiempo (ej. precios o retornos).
     * @param seriesA Lista de valores double del Activo A
     * @param seriesB Lista de valores double del Activo B
     * @return El valor matemático de la métrica
     */

    double calculate(List<Double> seriesA, List<Double> seriesB);

    String getName();

}
