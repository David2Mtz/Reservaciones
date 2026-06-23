package com.ipn.mx.reservaciones7cm3.features.room.service;

import com.ipn.mx.reservaciones7cm3.core.domain.Cuarto;
import com.ipn.mx.reservaciones7cm3.features.room.dto.CreateCuartoDTO;
import com.ipn.mx.reservaciones7cm3.features.room.dto.CuartoDTO;
import com.ipn.mx.reservaciones7cm3.features.room.dto.UpdateCuartoDTO;
import com.ipn.mx.reservaciones7cm3.features.room.dto.UpdateDisponibilidadDTO;

import java.util.List;

public interface CuartoService {

    CuartoDTO createCuarto(CreateCuartoDTO dto);
    List<CuartoDTO> readAllCuartos();
    CuartoDTO readById(Long id);
    CuartoDTO updateCuarto(Long id, UpdateCuartoDTO dto);

    /**
     *
     * @param id identificador del cuarto
     * @param dto Objeto que contiene el nuevo estatus de la disponibilidad
     * @return (@link CuartoDTO) actualizado
     */
    CuartoDTO updateDisponibilidad(Long id, UpdateDisponibilidadDTO dto);
    void deleteCuarto(Long id);
}
