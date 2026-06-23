package com.ipn.mx.reservaciones7cm3.features.room.service.impl;

import com.ipn.mx.reservaciones7cm3.core.domain.Cuarto;
import com.ipn.mx.reservaciones7cm3.core.exceptions.BussinesValidationException;
import com.ipn.mx.reservaciones7cm3.core.exceptions.EntityNotFoundException;
import com.ipn.mx.reservaciones7cm3.features.room.dto.CreateCuartoDTO;
import com.ipn.mx.reservaciones7cm3.features.room.dto.CuartoDTO;
import com.ipn.mx.reservaciones7cm3.features.room.dto.UpdateCuartoDTO;
import com.ipn.mx.reservaciones7cm3.features.room.dto.UpdateDisponibilidadDTO;
import com.ipn.mx.reservaciones7cm3.features.room.repository.CuartoRepository;
import com.ipn.mx.reservaciones7cm3.features.room.service.CuartoService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CuartoServiceImpl implements CuartoService {
    private final CuartoRepository cuartoRepository;

    @Override
    @Transactional()
    public CuartoDTO createCuarto(CreateCuartoDTO dto) {
        //Validar que el cuarto
        if (cuartoRepository.findByNumero(dto.numero()).isPresent()) {
            throw new BussinesValidationException("El numero de cuarto " + dto.numero() + "ya esta registrado");
        }
        Cuarto cuarto = mapearAEntidad(dto);
        cuarto.setDisponible(true);
        Cuarto cuartoGuardado = cuartoRepository.save(cuarto);
        return mapearDTO(cuartoGuardado);
    }

    @Override
    public List<CuartoDTO> readAllCuartos() {
        return cuartoRepository.findAll()
                .stream()
                .map(this::mapearDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CuartoDTO readById(Long id) {
        return cuartoRepository.findById(id)
                .map(this::mapearDTO)
                .orElseThrow(()->new EntityNotFoundException("El cuarto " + "no existe"));
    }

    @Override
    @Transactional()
    public CuartoDTO updateCuarto(Long id, UpdateCuartoDTO dto) {
        Cuarto cuarto = cuartoRepository.findById(id).orElseThrow(
                ()->new EntityNotFoundException("El cuarto " + "no existe"));
        if (dto.numero() != null && dto.numero() != cuarto.getNumero()) {
            if (cuartoRepository.findByNumero(dto.numero()).isPresent()) {
                throw new BussinesValidationException("El numero " + dto.numero() + "ya esta registrado");
            }
        }
        return mapearDTO(cuartoRepository.save(cuarto));
    }

    @Override
    @Transactional
    public CuartoDTO updateDisponibilidad(Long id, UpdateDisponibilidadDTO dto) {
        Cuarto cuarto = cuartoRepository.findById(id).orElseThrow(
                ()->new EntityNotFoundException("El cuarto " + "no existe"));
        cuarto.setDisponible(dto.disponible());
        return mapearDTO(cuartoRepository.save(cuarto));
    }

    @Override
    public void deleteCuarto(Long id) {
        if (!cuartoRepository.existsById(id)){
            throw new EntityNotFoundException("El cuarto " + "no existe");
        }
        cuartoRepository.deleteById(id);
    }

    private Cuarto mapearAEntidad(CreateCuartoDTO dto) {
        if (dto == null) {
            return null;
        }
        Cuarto entidad = new Cuarto();
        entidad.setTipo(dto.tipo());
        entidad.setNumero(dto.numero());
        entidad.setPrecio(dto.precio());
        entidad.setNumeroCamas(dto.numeroCamas());
        return entidad;
    }

    private CuartoDTO mapearDTO(Cuarto entidad){
        if (entidad == null) {
            return null;
        }
        return new CuartoDTO(
                entidad.getId(),
                entidad.getTipo(),
                entidad.getNumero(),
                entidad.getPrecio(),
                entidad.getNumeroCamas(),
                entidad.isDisponible()
        );
    }
}
