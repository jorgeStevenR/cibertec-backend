package com.cibertec.lms.infraestructura.persistencia.usuario;

import com.cibertec.lms.dominio.usuario.Usuario;

class UsuarioMapper {

    private UsuarioMapper() {
    }

    static Usuario aDominio(UsuarioJpaEntity entidad) {
        return Usuario.builder()
                .id(entidad.getId())
                .nombre(entidad.getNombre())
                .apellido(entidad.getApellido())
                .email(entidad.getEmail())
                .passwordHash(entidad.getPasswordHash())
                .rol(entidad.getRol())
                .activo(entidad.isActivo())
                .fechaCreacion(entidad.getFechaCreacion())
                .fechaUltimoLogin(entidad.getFechaUltimoLogin())
                .build();
    }

    static UsuarioJpaEntity aEntidad(Usuario usuario) {
        UsuarioJpaEntity entidad = new UsuarioJpaEntity();
        entidad.setId(usuario.getId());
        entidad.setNombre(usuario.getNombre());
        entidad.setApellido(usuario.getApellido());
        entidad.setEmail(usuario.getEmail());
        entidad.setPasswordHash(usuario.getPasswordHash());
        entidad.setRol(usuario.getRol());
        entidad.setActivo(usuario.isActivo());
        entidad.setFechaCreacion(usuario.getFechaCreacion());
        entidad.setFechaUltimoLogin(usuario.getFechaUltimoLogin());
        return entidad;
    }
}
