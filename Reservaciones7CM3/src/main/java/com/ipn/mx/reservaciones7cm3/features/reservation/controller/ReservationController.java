package com.ipn.mx.reservaciones7cm3.features.reservation.controller;

import com.ipn.mx.reservaciones7cm3.features.reservation.dto.CreateReservationDTO;
import com.ipn.mx.reservaciones7cm3.features.reservation.dto.ReservationDTO;
import com.ipn.mx.reservaciones7cm3.features.reservation.dto.UpdateReservationDTO;
import com.ipn.mx.reservaciones7cm3.features.reservation.service.ReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reservaciones")
@RequiredArgsConstructor
@Tag(name = "Módulo de Reservaciones", description = "Endpoints para la gestión del ciclo de vida de las reservaciones")
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping
    @Operation(summary = "Crear una nueva reservación", description = "Crea una reservación para una habitación y un usuario, validando disponibilidad y enviando confirmación por correo electrónico.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Reservación creada con éxito"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada no válidos o conflicto de disponibilidad"),
            @ApiResponse(responseCode = "404", description = "Habitación o usuario no encontrado")
    })
    public ResponseEntity<ReservationDTO> createReservation(@Valid @RequestBody CreateReservationDTO dto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(reservationService.createReservation(dto));
    }

    @GetMapping
    @Operation(summary = "Obtener todas las reservaciones", description = "Obtiene una lista de todas las reservaciones registradas en el sistema.")
    public ResponseEntity<List<ReservationDTO>> findAllReservations() {
        return ResponseEntity.ok(reservationService.readAllReservations());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener una reservación por ID", description = "Busca una reservación en el sistema por su identificador único.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reservación encontrada"),
            @ApiResponse(responseCode = "404", description = "Reservación no encontrada")
    })
    public ResponseEntity<ReservationDTO> findReservationById(@PathVariable Long id) {
        return ResponseEntity.ok(reservationService.readById(id));
    }

    @GetMapping("/usuario/{usuarioId}")
    @Operation(summary = "Obtener reservaciones de un usuario", description = "Obtiene una lista de las reservaciones correspondientes a un usuario específico.")
    public ResponseEntity<List<ReservationDTO>> findReservationsByUsuarioId(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(reservationService.readByUsuarioId(usuarioId));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar una reservación", description = "Actualiza las fechas o detalles de una reservación activa.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reservación actualizada con éxito"),
            @ApiResponse(responseCode = "400", description = "Conflicto de disponibilidad o fechas inválidas"),
            @ApiResponse(responseCode = "404", description = "Reservación no encontrada")
    })
    public ResponseEntity<ReservationDTO> updateReservation(@PathVariable Long id, @Valid @RequestBody UpdateReservationDTO dto) {
        return ResponseEntity.ok(reservationService.updateReservation(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Cancelar una reservación", description = "Establece el estado de una reservación como CANCELADA y notifica por correo electrónico.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reservación cancelada con éxito"),
            @ApiResponse(responseCode = "404", description = "Reservación no encontrada")
    })
    public ResponseEntity<ReservationDTO> cancelReservation(@PathVariable Long id) {
        return ResponseEntity.ok(reservationService.cancelReservation(id));
    }
}
