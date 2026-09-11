package com.cibertec.lms.dominio.excepcion;

public class EntregaNoEncontradaException extends RecursoNoEncontradoException {

    public EntregaNoEncontradaException(Long id) {
        super("No se encontró la entrega con id " + id);
    }
}
