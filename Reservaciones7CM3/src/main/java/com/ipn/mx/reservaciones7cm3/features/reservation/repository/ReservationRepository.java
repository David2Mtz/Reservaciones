package com.ipn.mx.reservaciones7cm3.features.reservation.repository;

import com.ipn.mx.reservaciones7cm3.core.domain.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByUsuarioId(Long usuarioId);

    @Query("SELECT r FROM Reservation r WHERE r.cuarto.id = :cuartoId AND r.estado <> 'CANCELADA' " +
           "AND (:fechaInicio < r.fechaFin AND :fechaFin > r.fechaInicio)")
    List<Reservation> findOverlappingReservations(
            @Param("cuartoId") Long cuartoId,
            @Param("fechaInicio") LocalDate fechaInicio,
            @Param("fechaFin") LocalDate fechaFin);
}
