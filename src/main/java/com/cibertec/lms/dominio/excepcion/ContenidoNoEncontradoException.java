package com.cibertec.lms.dominio.excepcion;

public class ContenidoNoEncontradoException extends RecursoNoEncontradoException {

    public ContenidoNoEncontradoException(Long id) {
        super("No se encontró el contenido con id " + id);
    }
}
