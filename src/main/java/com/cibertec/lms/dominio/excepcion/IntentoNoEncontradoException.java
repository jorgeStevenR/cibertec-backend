package com.cibertec.lms.dominio.excepcion;

public class IntentoNoEncontradoException extends RecursoNoEncontradoException {

    public IntentoNoEncontradoException(Long id) {
        super("No se encontró el intento con id " + id);
    }
}
