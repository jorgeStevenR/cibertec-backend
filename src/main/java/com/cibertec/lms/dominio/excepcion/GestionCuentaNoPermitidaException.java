package com.cibertec.lms.dominio.excepcion;

/** Se lanza cuando GESTOR_USUARIOS intenta crear, editar, eliminar o resetear la contraseña de una
 *  cuenta ADMINISTRADOR/GESTOR_USUARIOS, o asignar uno de esos roles al crear/editar un usuario.
 *  A diferencia de AccessDeniedException, conserva su propio mensaje en la respuesta 403 en vez del
 *  genérico "No tienes permiso...". */
public class GestionCuentaNoPermitidaException extends RuntimeException {

    public GestionCuentaNoPermitidaException(String mensaje) {
        super(mensaje);
    }
}
