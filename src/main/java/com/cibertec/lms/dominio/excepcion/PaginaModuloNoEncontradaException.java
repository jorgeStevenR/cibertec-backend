package com.cibertec.lms.dominio.excepcion;

public class PaginaModuloNoEncontradaException extends RecursoNoEncontradoException {

    public PaginaModuloNoEncontradaException(Long id) {
        super("No se encontró la página con id " + id);
    }
}
