package com.uq.analisis.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**") // Aplica a TODAS las rutas de tu API
                        .allowedOrigins("*") // Permite cualquier dominio (Vercel, localhost, etc.)
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Permite todos los verbos
                        .allowedHeaders("*"); // Permite cualquier cabecera
            }
        };
    }
}