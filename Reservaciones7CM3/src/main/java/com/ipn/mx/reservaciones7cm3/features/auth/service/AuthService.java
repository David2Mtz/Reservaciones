package com.ipn.mx.reservaciones7cm3.features.auth.service;

import com.ipn.mx.reservaciones7cm3.features.auth.dto.LoginRequestDTO;
import com.ipn.mx.reservaciones7cm3.features.auth.dto.RegistroRequestDTO;
import com.ipn.mx.reservaciones7cm3.features.auth.dto.UsuarioDTO;

public interface AuthService {
    UsuarioDTO registrarUsuario(RegistroRequestDTO request);
    UsuarioDTO iniciarSesion(LoginRequestDTO request);
}
