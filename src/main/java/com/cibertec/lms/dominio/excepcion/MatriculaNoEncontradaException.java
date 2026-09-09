package com.cibertec.lms.dominio.excepcion;

public class MatriculaNoEncontradaException extends RecursoNoEncontradoException {

    public MatriculaNoEncontradaException(Long id) {
        super("No se encontró la matrícula con id " + id);
    }
}
