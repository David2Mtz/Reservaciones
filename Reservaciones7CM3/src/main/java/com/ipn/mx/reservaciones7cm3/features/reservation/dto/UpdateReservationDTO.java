package com.ipn.mx.reservaciones7cm3.features.reservation.dto;

import java.time.LocalDate;

public record UpdateReservationDTO(
        LocalDate fechaInicio,
        LocalDate fechaFin,
        String estado,
        String detalles
) {}
