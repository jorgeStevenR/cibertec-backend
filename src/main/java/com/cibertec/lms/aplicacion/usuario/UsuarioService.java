package com.cibertec.lms.aplicacion.usuario;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.cibertec.lms.dominio.excepcion.EmailYaRegistradoException;
import com.cibertec.lms.dominio.excepcion.UsuarioNoEncontradoException;
import com.cibertec.lms.dominio.usuario.Usuario;
import com.cibertec.lms.dominio.usuario.UsuarioRepositorio;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepositorio usuarioRepositorio;
    private final PasswordEncoder passwordEncoder;

    public Usuario registrar(String nombre, String apellido, String email, String password, com.cibertec.lms.dominio.usuario.Rol rol) {
        if (usuarioRepositorio.existePorEmail(email)) {
            throw new EmailYaRegistradoException(email);
        }

        Usuario usuario = Usuario.builder()
                .nombre(nombre)
                .apellido(apellido)
                .email(email)
                .passwordHash(passwordEncoder.encode(password))
                .rol(rol)
                .activo(true)
                .fechaCreacion(LocalDateTime.now())
                .build();

        return usuarioRepositorio.guardar(usuario);
    }

    public Usuario buscarPorId(Long id) {
        return usuarioRepositorio.buscarPorId(id)
                .orElseThrow(() -> new UsuarioNoEncontradoException(id));
    }

    public List<Usuario> listarTodos() {
        return usuarioRepositorio.listarTodos();
    }

    public Usuario actualizar(Long id, String nombre, String apellido, com.cibertec.lms.dominio.usuario.Rol rol, boolean activo) {
        Usuario existente = buscarPorId(id);

        Usuario actualizado = Usuario.builder()
                .id(existente.getId())
                .nombre(nombre)
                .apellido(apellido)
                .email(existente.getEmail())
                .passwordHash(existente.getPasswordHash())
                .rol(rol)
                .activo(activo)
                .fechaCreacion(existente.getFechaCreacion())
                .build();

        return usuarioRepositorio.guardar(actualizado);
    }

    public void eliminar(Long id) {
        buscarPorId(id);
        usuarioRepositorio.eliminar(id);
    }

    /** Reseteo asistido por administrador: genera una contraseña temporal, la guarda hasheada y la devuelve en claro para que el admin se la entregue al usuario. */
    public String resetearPassword(Long id) {
        Usuario existente = buscarPorId(id);
        String passwordTemporal = generarPasswordTemporal();

        Usuario actualizado = Usuario.builder()
                .id(existente.getId())
                .nombre(existente.getNombre())
                .apellido(existente.getApellido())
                .email(existente.getEmail())
                .passwordHash(passwordEncoder.encode(passwordTemporal))
                .rol(existente.getRol())
                .activo(existente.isActivo())
                .fechaCreacion(existente.getFechaCreacion())
                .build();

        usuarioRepositorio.guardar(actualizado);
        return passwordTemporal;
    }

    private String generarPasswordTemporal() {
        String caracteres = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnpqrstuvwxyz23456789";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(10);
        for (int i = 0; i < 10; i++) {
            sb.append(caracteres.charAt(random.nextInt(caracteres.length())));
        }
        return sb.toString();
    }
}
