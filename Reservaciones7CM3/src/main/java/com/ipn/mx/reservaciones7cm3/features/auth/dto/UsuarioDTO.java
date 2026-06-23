package com.ipn.mx.reservaciones7cm3.features.auth.dto;

import java.util.Set;

public record UsuarioDTO(
        Long id,
        String username,
        String nombre,
        String email,
        boolean activo,
        Set<String> roles
) {}
