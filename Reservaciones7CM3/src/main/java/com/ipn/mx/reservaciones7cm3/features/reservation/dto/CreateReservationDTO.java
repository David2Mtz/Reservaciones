package com.ipn.mx.reservaciones7cm3.features.reservation.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.FutureOrPresent;
import java.time.LocalDate;

public record CreateReservationDTO(
        @NotNull(message = "El id del cuarto no puede ser nulo")
        Long cuartoId,

        @NotNull(message = "El id del usuario no puede ser nulo")
        Long usuarioId,

        @NotNull(message = "La fecha de inicio no puede ser nula")
        @FutureOrPresent(message = "La fecha de inicio debe ser hoy o en el futuro")
        LocalDate fechaInicio,

        @NotNull(message = "La fecha de fin no puede ser nula")
        LocalDate fechaFin,

        String detalles
) {}
