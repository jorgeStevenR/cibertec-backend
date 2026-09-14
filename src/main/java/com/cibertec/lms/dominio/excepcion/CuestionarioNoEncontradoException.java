package com.cibertec.lms.dominio.excepcion;

public class CuestionarioNoEncontradoException extends RecursoNoEncontradoException {

    public CuestionarioNoEncontradoException(Long id) {
        super("No se encontró el cuestionario con id " + id);
    }
}
