package com.cibertec.lms.dominio.excepcion;

public class PreguntaNoEncontradaException extends RecursoNoEncontradoException {

    public PreguntaNoEncontradaException(Long id) {
        super("No se encontró la pregunta con id " + id);
    }
}
