package com.ipn.mx.reservaciones7cm3.features.room.dto;

import jakarta.persistence.Column;

import java.math.BigDecimal;

/**
 * @param
 *
 */

public record CuartoDTO(
        Long id,
        String tipo,
        int numero,
        BigDecimal precio,
        int numeroCamas,
        boolean disponible
) {

}
