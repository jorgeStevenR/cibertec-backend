package com.cibertec.lms.dominio.excepcion;

public class EmailYaRegistradoException extends ReglaNegocioException {

    public EmailYaRegistradoException(String email) {
        super("Ya existe un usuario registrado con el email " + email);
    }
}
