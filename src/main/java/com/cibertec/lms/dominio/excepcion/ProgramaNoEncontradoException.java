package com.cibertec.lms.dominio.excepcion;

public class ProgramaNoEncontradoException extends RecursoNoEncontradoException {

    public ProgramaNoEncontradoException(Long id) {
        super("No se encontró el programa con id " + id);
    }
}
