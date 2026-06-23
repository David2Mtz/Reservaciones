package com.ipn.mx.reservaciones7cm3.core.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "rooms")
public class Cuarto implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "tipo", length = 50, nullable = false)
    private String tipo;

    @Column(name = "numero", nullable = false, unique = true)
    private int numero;

    @Column(name = "precio", nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;

    @Column(name = "numeroCamas", nullable = false)
    private int numeroCamas;

    @Column(name = "disponible", nullable = false)
    private boolean disponible;
}
