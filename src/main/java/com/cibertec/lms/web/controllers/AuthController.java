package com.cibertec.lms.web.controllers;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cibertec.lms.aplicacion.auth.AuthService;
import com.cibertec.lms.web.dtos.LoginRequestDTO;
import com.cibertec.lms.web.dtos.LoginResponseDTO;
import com.cibertec.lms.web.dtos.UsuarioResponseDTO;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public LoginResponseDTO login(@Valid @RequestBody LoginRequestDTO request) {
        AuthService.ResultadoLogin resultado = authService.login(request.email(), request.password());
        return new LoginResponseDTO(resultado.token(), UsuarioResponseDTO.desde(resultado.usuario()));
    }
}
