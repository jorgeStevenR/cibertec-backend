package com.cibertec.lms.dominio.excepcion;

/** Se lanza cuando un parámetro de la petición es sintácticamente válido pero incoherente (ej. un rango de fechas
 *  con desde posterior a hasta). Responde 400, a diferencia de ReglaNegocioException que responde 409. */
public class SolicitudInvalidaException extends RuntimeException {

    public SolicitudInvalidaException(String mensaje) {
        super(mensaje);
    }
}
