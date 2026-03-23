package com.uq.analisis.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "financial_assets", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"symbol", "date"})
})

@Data // Lombok: Genera getters, setters, toString, equals y hashCode
@NoArgsConstructor // Lombok: Constructor vacío requerido por JPA
@AllArgsConstructor // Lombok: Constructor con todos los argumentos
@Builder // Lombok: Patrón Builder para instanciar objetos fácilmente
public class FinancialAsset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El símbolo del activo es obligatorio")
    @Column(nullable = false, length = 20)
    private String symbol;

    @NotNull(message = "La fecha es obligatoria")
    @Column(nullable = false)
    private LocalDate date;

    @NotNull(message = "El precio de apertura no puede ser nulo")
    @PositiveOrZero(message = "El precio no puede ser negativo")
    @Column(name = "open_price", nullable = false)
    private Double open;

    @NotNull(message = "El precio de cierre no puede ser nulo")
    @PositiveOrZero(message = "El precio no puede ser negativo")
    @Column(name = "close_price", nullable = false)
    private Double close;

    @NotNull(message = "El precio máximo no puede ser nulo")
    @PositiveOrZero(message = "El precio no puede ser negativo")
    @Column(name = "high_price", nullable = false)
    private Double high;

    @NotNull(message = "El precio mínimo no puede ser nulo")
    @PositiveOrZero(message = "El precio no puede ser negativo")
    @Column(name = "low_price", nullable = false)
    private Double low;

    @NotNull(message = "El volumen no puede ser nulo")
    @PositiveOrZero(message = "El volumen no puede ser negativo")
    @Column(nullable = false)
    private Long volume;
}