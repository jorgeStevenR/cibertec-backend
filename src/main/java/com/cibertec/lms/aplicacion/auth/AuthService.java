package com.cibertec.lms.aplicacion.auth;

import java.time.LocalDateTime;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.cibertec.lms.dominio.auth.TokenProveedor;
import com.cibertec.lms.dominio.excepcion.CredencialesInvalidasException;
import com.cibertec.lms.dominio.excepcion.ReglaNegocioException;
import com.cibertec.lms.dominio.usuario.Usuario;
import com.cibertec.lms.dominio.usuario.UsuarioRepositorio;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepositorio usuarioRepositorio;
    private final PasswordEncoder passwordEncoder;
    private final TokenProveedor tokenProveedor;

    public ResultadoLogin login(String email, String password) {
        Usuario usuario = usuarioRepositorio.buscarPorEmail(email)
                .orElseThrow(CredencialesInvalidasException::new);

        if (!passwordEncoder.matches(password, usuario.getPasswordHash())) {
            throw new CredencialesInvalidasException();
        }

        if (!usuario.isActivo()) {
            throw new ReglaNegocioException("El usuario está inactivo");
        }

        Usuario actualizado = Usuario.builder()
                .id(usuario.getId())
                .nombre(usuario.getNombre())
                .apellido(usuario.getApellido())
                .email(usuario.getEmail())
                .passwordHash(usuario.getPasswordHash())
                .rol(usuario.getRol())
                .activo(usuario.isActivo())
                .fechaCreacion(usuario.getFechaCreacion())
                .fechaUltimoLogin(LocalDateTime.now())
                .build();
        usuarioRepositorio.guardar(actualizado);

        String token = tokenProveedor.generarToken(actualizado);
        return new ResultadoLogin(token, actualizado);
    }

    public record ResultadoLogin(String token, Usuario usuario) {
    }
}
