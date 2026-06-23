package com.ipn.mx.reservaciones7cm3.features.room.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record CreateCuartoDTO(
        @NotBlank(message = "El tipo de cuarto es obligatorio")
        @Size(min = 4, max = 50, message = "El tipo debe estar entre 4 yy 50 caracteres")
        String tipo,
        @NotNull(message = "El numero es obligatorio")
        @Positive(message = "El numero asignado al cuarto debe ser un valor positivo")
        Integer numero,
        @NotNull(message = "El precio es obligatorio")
        @DecimalMin(value = "0.0", inclusive = false, message = "El precio debe ser mayor a 0")
        @Digits(integer = 8, fraction = 2, message = "El precio tiene un formato invalido")
        BigDecimal precio,
        @Min(value = 1, message = "El cuarto debe tener al menos una cama")
        int numeroCamas
) {

}
