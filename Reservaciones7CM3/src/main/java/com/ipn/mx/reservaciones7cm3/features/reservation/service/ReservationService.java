package com.ipn.mx.reservaciones7cm3.features.reservation.service;

import com.ipn.mx.reservaciones7cm3.features.reservation.dto.CreateReservationDTO;
import com.ipn.mx.reservaciones7cm3.features.reservation.dto.ReservationDTO;
import com.ipn.mx.reservaciones7cm3.features.reservation.dto.UpdateReservationDTO;

import java.util.List;

public interface ReservationService {
    ReservationDTO createReservation(CreateReservationDTO dto);
    List<ReservationDTO> readAllReservations();
    ReservationDTO readById(Long id);
    List<ReservationDTO> readByUsuarioId(Long usuarioId);
    ReservationDTO updateReservation(Long id, UpdateReservationDTO dto);
    ReservationDTO cancelReservation(Long id);
}
