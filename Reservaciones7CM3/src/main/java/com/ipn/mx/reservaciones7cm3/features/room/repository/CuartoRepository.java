package com.ipn.mx.reservaciones7cm3.features.room.repository;

import com.ipn.mx.reservaciones7cm3.core.domain.Cuarto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CuartoRepository extends JpaRepository<Cuarto, Long> {
    Optional<Cuarto> findByNumero(Integer numero);
}
