package com.ipn.mx.reservaciones7cm3.core.config;

import com.ipn.mx.reservaciones7cm3.core.domain.Cuarto;
import com.ipn.mx.reservaciones7cm3.core.domain.Rol;
import com.ipn.mx.reservaciones7cm3.core.domain.Usuario;
import com.ipn.mx.reservaciones7cm3.features.room.repository.CuartoRepository;
import com.ipn.mx.reservaciones7cm3.features.auth.repository.RolRepository;
import com.ipn.mx.reservaciones7cm3.features.auth.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final CuartoRepository cuartoRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        log.info("Iniciando sembrado de datos en la Base de Datos...");

        // 1. Inicializar Roles
        Rol rolAdmin = obtenerOCrearRol("ROLE_ADMIN");
        Rol rolUser = obtenerOCrearRol("ROLE_USER");

        // 2. Inicializar Usuarios
        if (!usuarioRepository.existsByUsername("admin")) {
            Set<Rol> rolesAdmin = new HashSet<>();
            rolesAdmin.add(rolAdmin);
            
            Usuario admin = Usuario.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("admin"))
                    .nombre("Administrador del Sistema")
                    .email("admin@reservaeasy.com")
                    .activo(true)
                    .roles(rolesAdmin)
                    .build();
            usuarioRepository.save(admin);
            log.info("Usuario administrador creado con exito (user: admin, pass: admin)");
        }

        if (!usuarioRepository.existsByUsername("user")) {
            Set<Rol> rolesUser = new HashSet<>();
            rolesUser.add(rolUser);

            Usuario user = Usuario.builder()
                    .username("user")
                    .password(passwordEncoder.encode("user"))
                    .nombre("Luis David Martinez")
                    .email("user@reservaeasy.com")
                    .activo(true)
                    .roles(rolesUser)
                    .build();
            usuarioRepository.save(user);
            log.info("Usuario cliente creado con exito (user: user, pass: user)");
        }

        // 3. Inicializar Catálogo de Habitaciones (si está vacío)
        if (cuartoRepository.count() == 0) {
            Cuarto c1 = Cuarto.builder().tipo("Sencilla").numero(101).precio(new BigDecimal("850.00")).numeroCamas(1).disponible(true).build();
            Cuarto c2 = Cuarto.builder().tipo("Doble").numero(102).precio(new BigDecimal("1350.00")).numeroCamas(2).disponible(true).build();
            Cuarto c3 = Cuarto.builder().tipo("Suite").numero(201).precio(new BigDecimal("2250.00")).numeroCamas(2).disponible(true).build();
            Cuarto c4 = Cuarto.builder().tipo("Presidencial").numero(301).precio(new BigDecimal("4800.00")).numeroCamas(3).disponible(true).build();

            cuartoRepository.save(c1);
            cuartoRepository.save(c2);
            cuartoRepository.save(c3);
            cuartoRepository.save(c4);
            log.info("Catalogo de habitaciones inicializado con 4 habitaciones de prueba");
        }

        log.info("Sembrado de datos finalizado correctamente.");
    }

    private Rol obtenerOCrearRol(String nombre) {
        return rolRepository.findByNombre(nombre)
                .orElseGet(() -> {
                    Rol nuevoRol = Rol.builder().nombre(nombre).build();
                    log.info("Rol {} creado en base de datos.", nombre);
                    return rolRepository.save(nuevoRol);
                });
    }
}
