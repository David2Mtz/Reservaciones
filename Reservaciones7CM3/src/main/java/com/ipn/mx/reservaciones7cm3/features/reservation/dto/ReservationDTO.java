package com.ipn.mx.reservaciones7cm3.features.reservation.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ReservationDTO(
        Long id,
        Long cuartoId,
        int cuartoNumero,
        String cuartoTipo,
        Long usuarioId,
        String usuarioUsername,
        String usuarioEmail,
        LocalDate fechaInicio,
        LocalDate fechaFin,
        String estado,
        BigDecimal precioTotal,
        String detalles
) {}
