package com.cibertec.lms.dominio.excepcion;

public class LienzoNoEncontradoException extends RecursoNoEncontradoException {

    public LienzoNoEncontradoException(Long id) {
        super("No se encontró el lienzo con id " + id);
    }
}
