package com.uq.analisis.similarity;

import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class DynamicTimeWarping implements SimilarityAlgorithm {

    @Override
    public double calculate(List<Double> seriesA, List<Double> seriesB) {
        int n = seriesA.size();
        int m = seriesB.size();

        // Matriz de costos acumulados
        double[][] dtw = new double[n + 1][m + 1];

        // Inicializar la matriz con infinito (Double.MAX_VALUE)
        for (int i = 0; i <= n; i++) {
            for (int j = 0; j <= m; j++) {
                dtw[i][j] = Double.MAX_VALUE;
            }
        }
        dtw[0][0] = 0.0;

        // Llenar la matriz de programación dinámica
        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= m; j++) {
                // Costo local (distancia absoluta entre los dos puntos)
                double cost = Math.abs(seriesA.get(i - 1) - seriesB.get(j - 1));

                // El costo acumulado es el costo local + el mínimo de los caminos anteriores
                // (Inserción, Eliminación, o Coincidencia)
                double minPrevious = Math.min(
                        Math.min(dtw[i - 1][j], dtw[i][j - 1]),
                        dtw[i - 1][j - 1]
                );

                dtw[i][j] = cost + minPrevious;
            }
        }

        // El resultado final es el costo en la esquina inferior derecha de la matriz
        return dtw[n][m];
    }

    @Override
    public String getName() {
        return "Dynamic Time Warping (DTW)";
    }
}