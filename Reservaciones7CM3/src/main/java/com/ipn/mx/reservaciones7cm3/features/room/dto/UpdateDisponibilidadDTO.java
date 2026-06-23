package com.ipn.mx.reservaciones7cm3.features.room.dto;

import jakarta.validation.constraints.NotNull;

public record UpdateDisponibilidadDTO(
        @NotNull(message = "El status para la disponibilidad es obligatorio (falso o verdadero)")
        Boolean disponible
) {
}
