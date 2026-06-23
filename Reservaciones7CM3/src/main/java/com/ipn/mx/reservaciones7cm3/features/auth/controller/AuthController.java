package com.ipn.mx.reservaciones7cm3.features.auth.controller;

import com.ipn.mx.reservaciones7cm3.features.auth.dto.LoginRequestDTO;
import com.ipn.mx.reservaciones7cm3.features.auth.dto.RegistroRequestDTO;
import com.ipn.mx.reservaciones7cm3.features.auth.dto.UsuarioDTO;
import com.ipn.mx.reservaciones7cm3.features.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Módulo de Autenticación", description = "Endpoints para registro e inicio de sesión de usuarios")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Registrar un nuevo usuario", description = "Registra un usuario en la base de datos con roles opcionales (por defecto se asigna ROLE_USER).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuario registrado con éxito"),
            @ApiResponse(responseCode = "400", description = "Nombre de usuario o correo ya registrados, o datos inválidos")
    })
    public ResponseEntity<UsuarioDTO> register(@Valid @RequestBody RegistroRequestDTO request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(authService.registrarUsuario(request));
    }

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión", description = "Valida las credenciales de un usuario y devuelve sus datos de perfil y roles.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Autenticación exitosa"),
            @ApiResponse(responseCode = "400", description = "Credenciales incorrectas")
    })
    public ResponseEntity<UsuarioDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        return ResponseEntity.ok(authService.iniciarSesion(request));
    }
}
