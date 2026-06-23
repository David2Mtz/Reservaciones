package com.ipn.mx.reservaciones7cm3.core.exceptions;

import java.time.LocalDateTime;

public record CustomErrorRecord(
        LocalDateTime dateTime,
        String message,
        String details
) {
}
