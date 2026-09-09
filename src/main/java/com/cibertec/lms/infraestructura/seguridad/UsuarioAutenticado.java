package com.cibertec.lms.infraestructura.seguridad;

import com.cibertec.lms.dominio.usuario.Rol;

/** Representa al usuario que hizo la petición, extraído del JWT. Es lo que llega en @AuthenticationPrincipal. */
public record UsuarioAutenticado(Long id, String email, Rol rol) {
}
