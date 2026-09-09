package com.cibertec.lms.dominio.excepcion;

public class UsuarioNoEncontradoException extends RecursoNoEncontradoException {

    public UsuarioNoEncontradoException(Long id) {
        super("No se encontró el usuario con id " + id);
    }
}
