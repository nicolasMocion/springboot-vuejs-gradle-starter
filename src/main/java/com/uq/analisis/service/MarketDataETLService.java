package com.uq.analisis.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.uq.analisis.Repository.FinancialAssetRepository;
import com.uq.analisis.client.MarketDataClient;
import com.uq.analisis.model.FinancialAsset;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j // Lombok: Para usar el logger automáticamente (log.info, log.error)
@Service
@RequiredArgsConstructor // Lombok: Inyecta automáticamente los 'final'
public class MarketDataETLService {

    private final MarketDataClient marketDataClient;
    private final FinancialAssetRepository repository;
    private final ObjectMapper objectMapper;

    /**
     * Método principal que orquesta la extracción y carga.
     * @Transactional asegura que si algo falla, no se guarden datos a medias.
     */
    @Transactional
    public void syncMarketData(List<String> symbols) {
        for (String symbol : symbols) {
            if (repository.existsBySymbol(symbol)) {
                log.info("Los datos para {} ya existen en la BD. Saltando extracción.", symbol);
                continue;
            }

            try {
                log.info("Extrayendo datos para: {}", symbol);
                String jsonResponse = marketDataClient.fetchHistoricalData(symbol);
                List<FinancialAsset> assets = parseJsonResponse(jsonResponse, symbol);

                // Carga masiva a la base de datos
                repository.saveAll(assets);
                log.info("Se guardaron {} registros estandarizados en USD para {}", assets.size(), symbol);

                // IMPORTANTE: Pausa para no saturar APIs públicas (Rate Limiting)
                Thread.sleep(2000);

            } catch (Exception e) {
                log.error("Error procesando el símbolo {}: {}", symbol, e.getMessage());
            }
        }
    }

    private List<FinancialAsset> parseJsonResponse(String jsonResponse, String symbol) throws Exception {
        // Usamos un Map (Fecha -> Activo) para evitar fechas duplicadas en la respuesta
        Map<LocalDate, FinancialAsset> uniqueAssetsMap = new java.util.HashMap<>();

        JsonNode rootNode = objectMapper.readTree(jsonResponse);

        JsonNode resultNode = rootNode.path("chart").path("result").get(0);
        if (resultNode == null || resultNode.isMissingNode()) {
            throw new RuntimeException("Estructura JSON inválida desde Yahoo Finance para " + symbol);
        }

        JsonNode timestamps = resultNode.path("timestamp");
        JsonNode quote = resultNode.path("indicators").path("quote").get(0);

        JsonNode opens = quote.path("open");
        JsonNode closes = quote.path("close");
        JsonNode highs = quote.path("high");
        JsonNode lows = quote.path("low");
        JsonNode volumes = quote.path("volume");

        if (timestamps == null || timestamps.isMissingNode()) {
            return new ArrayList<>(); // Retorna lista vacía si no hay datos
        }

        // 1. Definimos la tasa de cambio y la bandera de conversión
        double jpyToUsd = 0.0064; // 1 Yen = 0.0064 Dólares (Aprox)
        boolean isJapaneseAsset = symbol.equals("^N225");

        for (int i = 0; i < timestamps.size(); i++) {
            // Ignorar días nulos (feriados de bolsa)
            if (opens.get(i).isNull() || closes.get(i).isNull()) {
                continue;
            }

            long unixTime = timestamps.get(i).asLong();
            LocalDate date = java.time.Instant.ofEpochSecond(unixTime)
                    .atZone(java.time.ZoneId.systemDefault())
                    .toLocalDate();

            // 2. Extraemos los valores crudos a variables intermedias
            double rawOpen = opens.get(i).asDouble();
            double rawHigh = highs.get(i).asDouble();
            double rawLow = lows.get(i).asDouble();
            double rawClose = closes.get(i).asDouble();

            // 3. APLICAMOS LA CONVERSIÓN SI ES NECESARIA
            if (isJapaneseAsset) {
                rawOpen = rawOpen * jpyToUsd;
                rawHigh = rawHigh * jpyToUsd;
                rawLow = rawLow * jpyToUsd;
                rawClose = rawClose * jpyToUsd;
            }

            // 4. Construimos el objeto usando las variables procesadas
            FinancialAsset asset = FinancialAsset.builder()
                    .symbol(symbol)
                    .date(date)
                    .open(rawOpen)
                    .high(rawHigh)
                    .low(rawLow)
                    .close(rawClose)
                    .volume(volumes.get(i).asLong())
                    .build();

            // Si Yahoo envía dos registros para el mismo día, esto sobrescribe el viejo con el nuevo,
            // garantizando que a la base de datos solo llegue una copia por fecha.
            uniqueAssetsMap.put(date, asset);
        }

        // Convertimos el Map de vuelta a una Lista
        return new ArrayList<>(uniqueAssetsMap.values());
    }
}