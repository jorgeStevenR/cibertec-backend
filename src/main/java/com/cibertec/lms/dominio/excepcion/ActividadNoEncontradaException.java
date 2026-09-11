package com.cibertec.lms.dominio.excepcion;

public class ActividadNoEncontradaException extends RecursoNoEncontradoException {

    public ActividadNoEncontradaException(Long id) {
        super("No se encontró la actividad con id " + id);
    }
}
