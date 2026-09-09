package com.cibertec.lms.dominio.excepcion;

public class ModuloNoEncontradoException extends RecursoNoEncontradoException {

    public ModuloNoEncontradoException(Long id) {
        super("No se encontró el módulo con id " + id);
    }
}
