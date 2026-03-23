package com.uq.analisis.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor; // Using Lombok as seen in your build.gradle

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

@Service
@RequiredArgsConstructor // This automatically creates the constructor for objectMapper
public class MarketDataClientETLService {

    private static final String API_KEY = "demo";
    private static final String BASE_URL = "https://www.alphavantage.co/query";

    // 1. Inject the ObjectMapper bean
    private final ObjectMapper objectMapper;

    // 2. We can still keep the HttpClient setup
    private final HttpClient httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    public String fetchHistoricalData(String symbol) throws Exception {
        String url = String.format("%s?function=TIME_SERIES_DAILY&symbol=%s&outputsize=full&apikey=%s",
                BASE_URL, symbol, API_KEY);

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(url))
                .header("Accept", "application/json")
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Error HTTP: " + response.statusCode());
        }

        // 3. Example: Instead of just returning String, you could parse it here
        // parseData(response.body());

        return response.body();
    }

    /**
     * Example of how to use ObjectMapper to navigate the AlphaVantage JSON
     */
    public void parseData(String rawJson) throws Exception {
        JsonNode root = objectMapper.readTree(rawJson);

        // AlphaVantage puts data under "Time Series (Daily)"
        JsonNode timeSeries = root.path("Time Series (Daily)");

        // Now you can iterate over the dates in the JSON
        timeSeries.fieldNames().forEachRemaining(date -> {
            JsonNode dayData = timeSeries.get(date);
            String closePrice = dayData.get("4. close").asText();
            System.out.println("Date: " + date + " | Close: " + closePrice);
        });
    }
}