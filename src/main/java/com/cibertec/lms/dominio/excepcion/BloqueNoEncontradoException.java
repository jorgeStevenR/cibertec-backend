package com.cibertec.lms.dominio.excepcion;

public class BloqueNoEncontradoException extends RecursoNoEncontradoException {

    public BloqueNoEncontradoException(Long id) {
        super("No se encontró el bloque con id " + id);
    }
}
