package com.cibertec.lms.web.dtos;

import com.cibertec.lms.dominio.cuestionario.Pregunta;
import com.cibertec.lms.dominio.cuestionario.TipoPregunta;

public record PreguntaResponseDTO(
        Long id,
        Long cuestionarioId,
        TipoPregunta tipo,
        String enunciado,
        int puntaje,
        int orden,
        java.util.List<OpcionResponseDTO> opciones
) {
    public static PreguntaResponseDTO desde(Pregunta pregunta, boolean revelarCorrecta) {
        return new PreguntaResponseDTO(
                pregunta.getId(),
                pregunta.getCuestionarioId(),
                pregunta.getTipo(),
                pregunta.getEnunciado(),
                pregunta.getPuntaje(),
                pregunta.getOrden(),
                pregunta.getOpciones().stream().map(o -> OpcionResponseDTO.desde(o, revelarCorrecta)).toList()
        );
    }
}
