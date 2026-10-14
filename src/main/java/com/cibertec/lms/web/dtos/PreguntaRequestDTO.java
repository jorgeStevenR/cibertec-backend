package com.cibertec.lms.web.dtos;

import java.util.List;

import com.cibertec.lms.dominio.cuestionario.TipoPregunta;
import com.cibertec.lms.web.validacion.OpcionesSegunTipo;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

@OpcionesSegunTipo
public record PreguntaRequestDTO(
        /** Null = OPCION_MULTIPLE, para no romper a clientes anteriores a este campo. */
        TipoPregunta tipo,

        @NotBlank(message = "El enunciado es obligatorio")
        String enunciado,

        @Positive(message = "El puntaje debe ser mayor a cero")
        int puntaje,

        /** Las reglas dependen del tipo (ver OpcionesSegunTipo); en ABIERTA va vacía o se omite. */
        @Valid
        List<OpcionRequestDTO> opciones
) {

    public TipoPregunta tipoEfectivo() {
        return tipo != null ? tipo : TipoPregunta.OPCION_MULTIPLE;
    }
}
