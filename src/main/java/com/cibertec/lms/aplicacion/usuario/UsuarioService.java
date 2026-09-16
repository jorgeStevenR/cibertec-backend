package com.cibertec.lms.aplicacion.usuario;

import java.security.SecureRandom;
import java.time.LocalDateTime;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.cibertec.lms.dominio.comun.Pagina;
import com.cibertec.lms.dominio.excepcion.EmailYaRegistradoException;
import com.cibertec.lms.dominio.excepcion.GestionCuentaNoPermitidaException;
import com.cibertec.lms.dominio.excepcion.UsuarioNoEncontradoException;
import com.cibertec.lms.dominio.usuario.Rol;
import com.cibertec.lms.dominio.usuario.Usuario;
import com.cibertec.lms.dominio.usuario.UsuarioRepositorio;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepositorio usuarioRepositorio;
    private final PasswordEncoder passwordEncoder;

    public Usuario registrar(String nombre, String apellido, String email, String password, Rol rol) {
        validarRolGestionable(rol);
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

    public Pagina<Usuario> listarPaginado(int pagina, int tamano, String busqueda) {
        return usuarioRepositorio.listarPaginado(pagina, tamano, busqueda);
    }

    public Usuario actualizar(Long id, String nombre, String apellido, Rol rol, boolean activo) {
        Usuario existente = buscarPorId(id);
        validarRolGestionable(existente.getRol());
        validarRolGestionable(rol);

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
        Usuario existente = buscarPorId(id);
        validarRolGestionable(existente.getRol());
        usuarioRepositorio.eliminar(id);
    }

    /** Reseteo asistido por el gestor de usuarios: genera una contraseña temporal, la guarda hasheada y la devuelve en claro para que se la entregue al usuario. */
    public String resetearPassword(Long id) {
        Usuario existente = buscarPorId(id);
        validarRolGestionable(existente.getRol());
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

    /** GESTOR_USUARIOS (único rol que llega a estos métodos, ver @PreAuthorize en UsuarioController) no puede
     *  crear/editar/eliminar/resetear cuentas ADMINISTRADOR ni GESTOR_USUARIOS, ni asignar esos roles a nadie.
     *  Esas cuentas se crean fuera de la API (ver GestorUsuariosSeeder/AdminSeeder). */
    private void validarRolGestionable(Rol rol) {
        if (rol == Rol.ADMINISTRADOR || rol == Rol.GESTOR_USUARIOS) {
            throw new GestionCuentaNoPermitidaException("No tienes permiso para gestionar cuentas con este rol.");
        }
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
