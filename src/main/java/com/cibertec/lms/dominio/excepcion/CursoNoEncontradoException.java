package com.cibertec.lms.dominio.excepcion;

public class CursoNoEncontradoException extends RecursoNoEncontradoException {

    public CursoNoEncontradoException(Long id) {
        super("No se encontró el curso con id " + id);
    }
}
