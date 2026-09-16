package com.cibertec.lms.infraestructura.config;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.cibertec.lms.dominio.usuario.Rol;
import com.cibertec.lms.dominio.usuario.Usuario;
import com.cibertec.lms.dominio.usuario.UsuarioRepositorio;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/** Crea el gestor de usuarios inicial si todavía no existe ninguno. Ninguna cuenta ADMINISTRADOR ni
 *  GESTOR_USUARIOS se puede crear vía /api/usuarios (ver UsuarioService.validarRolGestionable) — este
 *  seeder, igual que AdminSeeder, es el único mecanismo para dar de alta esos roles privilegiados. Si
 *  hace falta un segundo GESTOR_USUARIOS o ADMINISTRADOR además del sembrado, se inserta directo en la
 *  base de datos (no hay Flyway/Liquibase en este proyecto todavía). */
@Slf4j
@Component
@RequiredArgsConstructor
public class GestorUsuariosSeeder implements CommandLineRunner {

    private final UsuarioRepositorio usuarioRepositorio;
    private final PasswordEncoder passwordEncoder;

    @Value("${lms.gestor.email:gestor@cibertec.com}")
    private String gestorEmail;

    @Value("${lms.gestor.password:Gestor123!}")
    private String gestorPassword;

    @Override
    public void run(String... args) {
        if (usuarioRepositorio.buscarPorEmail(gestorEmail).isPresent()) {
            return;
        }

        Usuario gestor = Usuario.builder()
                .nombre("Gestor")
                .apellido("de Usuarios")
                .email(gestorEmail)
                .passwordHash(passwordEncoder.encode(gestorPassword))
                .rol(Rol.GESTOR_USUARIOS)
                .activo(true)
                .fechaCreacion(LocalDateTime.now())
                .build();

        usuarioRepositorio.guardar(gestor);
        log.warn("Gestor de usuarios inicial creado -> email: {} / password: {} (cámbiala apenas inicies sesión)", gestorEmail, gestorPassword);
    }
}
