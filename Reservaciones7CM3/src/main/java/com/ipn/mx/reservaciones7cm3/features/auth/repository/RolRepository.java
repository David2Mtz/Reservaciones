package com.ipn.mx.reservaciones7cm3.features.auth.repository;

import com.ipn.mx.reservaciones7cm3.core.domain.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RolRepository extends JpaRepository<Rol, Long> {
    Optional<Rol> findByNombre(String nombre);
}
