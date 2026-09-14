package com.cibertec.lms.dominio.excepcion;

/** Se lanza cuando se intenta responder un cuestionario fuera de su ventana de disponibilidad
 *  (todavía no abre, o ya venció el plazo). A diferencia de AccessDeniedException, conserva su
 *  propio mensaje en la respuesta 403 en vez del genérico "No tienes permiso...". */
public class VentanaCuestionarioException extends RuntimeException {

    public VentanaCuestionarioException(String mensaje) {
        super(mensaje);
    }
}
