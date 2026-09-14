package com.cibertec.lms.web.dtos;

import java.util.List;

import com.cibertec.lms.dominio.cuestionario.Pregunta;

/** Igual que PreguntaResponseDTO (con esCorrecta siempre revelado) pero agrega qué opción eligió el
 *  estudiante en esta pregunta, para poder pintar "contestaste esto, lo correcto era esto otro". */
public record PreguntaConRespuestaResponseDTO(
        Long id,
        String enunciado,
        int puntaje,
        int orden,
        List<OpcionResponseDTO> opciones,
        Long opcionElegidaId
) {
    public static PreguntaConRespuestaResponseDTO desde(Pregunta pregunta, Long opcionElegidaId) {
        return new PreguntaConRespuestaResponseDTO(
                pregunta.getId(),
                pregunta.getEnunciado(),
                pregunta.getPuntaje(),
                pregunta.getOrden(),
                pregunta.getOpciones().stream().map(o -> OpcionResponseDTO.desde(o, true)).toList(),
                opcionElegidaId
        );
    }
}
