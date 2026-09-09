package com.cibertec.lms.dominio.auth;

import com.cibertec.lms.dominio.usuario.Usuario;

/** Puerto de salida: genera el token de sesión sin que la capa de aplicación sepa qué tecnología lo implementa (JWT hoy, otra cosa mañana). */
public interface TokenProveedor {

    String generarToken(Usuario usuario);
}
