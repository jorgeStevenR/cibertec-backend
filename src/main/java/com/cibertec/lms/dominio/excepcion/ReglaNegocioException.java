package com.cibertec.lms.dominio.excepcion;

/** Se lanza cuando la entrada es válida sintácticamente pero viola una regla del negocio (ej. matrícula duplicada, rol incorrecto). */
public class ReglaNegocioException extends RuntimeException {

    public ReglaNegocioException(String mensaje) {
        super(mensaje);
    }
}
