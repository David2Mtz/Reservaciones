package com.ipn.mx.reservaciones7cm3.features.room.controller;

import com.ipn.mx.reservaciones7cm3.features.room.dto.CreateCuartoDTO;
import com.ipn.mx.reservaciones7cm3.features.room.dto.CuartoDTO;
import com.ipn.mx.reservaciones7cm3.features.room.dto.UpdateCuartoDTO;
import com.ipn.mx.reservaciones7cm3.features.room.dto.UpdateDisponibilidadDTO;
import com.ipn.mx.reservaciones7cm3.features.room.service.CuartoService;
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
@RequestMapping("/api/v1/cuartos")
@RequiredArgsConstructor
@Tag(name = "Módulo de Habitaciones", description = "Endpoints para gestionar las habitaciones/cuartos del hotel")
public class CuartoController {
    private final CuartoService cuartoService;

    @PostMapping
    @Operation(summary = "Crear un nuevo cuarto", description = "Crea una habitación en el catálogo de habitaciones.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Habitación creada con éxito"),
            @ApiResponse(responseCode = "400", description = "Número de habitación ya registrado o datos inválidos")
    })
    public ResponseEntity<CuartoDTO> createCuarto(@Valid @RequestBody CreateCuartoDTO dto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(cuartoService.createCuarto(dto));
    }

    @GetMapping
    @Operation(summary = "Listar todas las habitaciones", description = "Obtiene la lista de todas las habitaciones del hotel.")
    public ResponseEntity<List<CuartoDTO>> findAllCuartos() {
        return ResponseEntity.ok(cuartoService.readAllCuartos());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar habitación por ID", description = "Obtiene los detalles de una habitación utilizando su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Habitación encontrada"),
            @ApiResponse(responseCode = "404", description = "Habitación no encontrada")
    })
    public ResponseEntity<CuartoDTO> findCuarto(@PathVariable Long id) {
        return ResponseEntity.ok(cuartoService.readById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar información de la habitación", description = "Modifica los datos de una habitación existente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Habitación actualizada con éxito"),
            @ApiResponse(responseCode = "404", description = "Habitación no encontrada")
    })
    public ResponseEntity<CuartoDTO> updateCuarto(@PathVariable Long id, @Valid @RequestBody UpdateCuartoDTO dto) {
        return ResponseEntity.ok(cuartoService.updateCuarto(id, dto));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Actualizar disponibilidad de la habitación", description = "Permite cambiar el estado de disponibilidad (libre/ocupada) de una habitación.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Disponibilidad modificada con éxito"),
            @ApiResponse(responseCode = "404", description = "Habitación no encontrada")
    })
    public  ResponseEntity<CuartoDTO> updateDisponibilidad(@PathVariable Long id,
                                                           @Valid @RequestBody UpdateDisponibilidadDTO dto) {
        return ResponseEntity.ok(cuartoService.updateDisponibilidad(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar una habitación", description = "Elimina una habitación del catálogo del hotel.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "244", description = "No content / Eliminada con éxito"),
            @ApiResponse(responseCode = "404", description = "Habitación no encontrada")
    })
    public ResponseEntity<Void> deleteCuarto(@PathVariable Long id) {
        cuartoService.deleteCuarto(id);
        return ResponseEntity.noContent().build();
    }

}
