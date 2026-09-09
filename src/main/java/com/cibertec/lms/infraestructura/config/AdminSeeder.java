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

/** Crea el administrador inicial si todavía no existe ninguno; sin esto no habría forma de entrar una vez la API exige JWT. */
@Slf4j
@Component
@RequiredArgsConstructor
public class AdminSeeder implements CommandLineRunner {

    private final UsuarioRepositorio usuarioRepositorio;
    private final PasswordEncoder passwordEncoder;

    @Value("${lms.admin.email:admin@cibertec.com}")
    private String adminEmail;

    @Value("${lms.admin.password:Admin123!}")
    private String adminPassword;

    @Override
    public void run(String... args) {
        if (usuarioRepositorio.buscarPorEmail(adminEmail).isPresent()) {
            return;
        }

        Usuario admin = Usuario.builder()
                .nombre("Administrador")
                .apellido("CIBERCTEC")
                .email(adminEmail)
                .passwordHash(passwordEncoder.encode(adminPassword))
                .rol(Rol.ADMINISTRADOR)
                .activo(true)
                .fechaCreacion(LocalDateTime.now())
                .build();

        usuarioRepositorio.guardar(admin);
        log.warn("Administrador inicial creado -> email: {} / password: {} (cámbiala apenas inicies sesión)", adminEmail, adminPassword);
    }
}
