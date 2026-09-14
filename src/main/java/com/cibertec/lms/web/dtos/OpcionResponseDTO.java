package com.cibertec.lms.web.dtos;

import com.cibertec.lms.dominio.cuestionario.Opcion;
import com.fasterxml.jackson.annotation.JsonInclude;

/** esCorrecta viaja como null (y por JsonInclude.NON_NULL se omite del JSON, no solo se oculta en el
 *  frontend) cuando quien pregunta es un estudiante que todavía no respondió el cuestionario. */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record OpcionResponseDTO(Long id, String texto, Boolean esCorrecta) {

    public static OpcionResponseDTO desde(Opcion opcion, boolean revelarCorrecta) {
        return new OpcionResponseDTO(
                opcion.getId(),
                opcion.getTexto(),
                revelarCorrecta ? opcion.isEsCorrecta() : null
        );
    }
}
