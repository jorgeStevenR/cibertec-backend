package com.cibertec.lms.dominio.excepcion;

public class MatriculaProgramaNoEncontradaException extends RecursoNoEncontradoException {

    public MatriculaProgramaNoEncontradaException(Long id) {
        super("No se encontró la matrícula de programa con id " + id);
    }
}
