package com.ipn.mx.reservaciones7cm3.features.auth.service.impl;

import com.ipn.mx.reservaciones7cm3.core.domain.Rol;
import com.ipn.mx.reservaciones7cm3.core.domain.Usuario;
import com.ipn.mx.reservaciones7cm3.core.exceptions.BussinesValidationException;
import com.ipn.mx.reservaciones7cm3.features.auth.dto.LoginRequestDTO;
import com.ipn.mx.reservaciones7cm3.features.auth.dto.RegistroRequestDTO;
import com.ipn.mx.reservaciones7cm3.features.auth.dto.UsuarioDTO;
import com.ipn.mx.reservaciones7cm3.features.auth.repository.RolRepository;
import com.ipn.mx.reservaciones7cm3.features.auth.repository.UsuarioRepository;
import com.ipn.mx.reservaciones7cm3.features.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @Override
    @Transactional
    public UsuarioDTO registrarUsuario(RegistroRequestDTO request) {
        if (usuarioRepository.existsByUsername(request.username())) {
            throw new BussinesValidationException("El nombre de usuario ya está registrado");
        }
        if (usuarioRepository.existsByEmail(request.email())) {
            throw new BussinesValidationException("El correo electrónico ya está registrado");
        }

        Set<Rol> roles = new HashSet<>();
        if (request.roles() == null || request.roles().isEmpty()) {
            roles.add(obtenerOCrearRol("ROLE_USER"));
        } else {
            for (String rolNombre : request.roles()) {
                String rolConPrefijo = rolNombre.startsWith("ROLE_") ? rolNombre : "ROLE_" + rolNombre.toUpperCase();
                roles.add(obtenerOCrearRol(rolConPrefijo));
            }
        }

        Usuario usuario = Usuario.builder()
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .nombre(request.nombre())
                .email(request.email())
                .activo(true)
                .roles(roles)
                .build();

        Usuario usuarioGuardado = usuarioRepository.save(usuario);
        return mapearDTO(usuarioGuardado);
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioDTO iniciarSesion(LoginRequestDTO request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );

        Usuario usuario = usuarioRepository.findByUsername(request.username())
                .orElseThrow(() -> new BussinesValidationException("Usuario no encontrado tras autenticación"));

        return mapearDTO(usuario);
    }

    private Rol obtenerOCrearRol(String nombre) {
        return rolRepository.findByNombre(nombre)
                .orElseGet(() -> rolRepository.save(Rol.builder().nombre(nombre).build()));
    }

    private UsuarioDTO mapearDTO(Usuario usuario) {
        return new UsuarioDTO(
                usuario.getId(),
                usuario.getUsername(),
                usuario.getNombre(),
                usuario.getEmail(),
                usuario.isActivo(),
                usuario.getRoles().stream()
                        .map(Rol::getNombre)
                        .collect(Collectors.toSet())
        );
    }
}
