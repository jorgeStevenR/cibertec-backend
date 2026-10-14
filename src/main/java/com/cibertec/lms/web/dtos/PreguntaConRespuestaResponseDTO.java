package com.cibertec.lms.web.dtos;

import java.util.List;

import com.cibertec.lms.dominio.cuestionario.Pregunta;
import com.cibertec.lms.dominio.cuestionario.RespuestaValor;
import com.cibertec.lms.dominio.cuestionario.TipoPregunta;

/** Igual que PreguntaResponseDTO (con esCorrecta siempre revelado) pero agrega lo que respondió el estudiante:
 *  la opción elegida en las cerradas, para poder pintar "contestaste esto, lo correcto era esto otro", o el texto
 *  y el puntaje asignado por el profesor en las abiertas (puntajeObtenido null = todavía sin calificar). */
public record PreguntaConRespuestaResponseDTO(
        Long id,
        TipoPregunta tipo,
        String enunciado,
        int puntaje,
        int orden,
        List<OpcionResponseDTO> opciones,
        Long opcionElegidaId,
        String textoRespuesta,
        Double puntajeObtenido
) {
    /** {@code respuesta} es null si el estudiante dejó la pregunta sin responder. */
    public static PreguntaConRespuestaResponseDTO desde(Pregunta pregunta, RespuestaValor respuesta) {
        return new PreguntaConRespuestaResponseDTO(
                pregunta.getId(),
                pregunta.getTipo(),
                pregunta.getEnunciado(),
                pregunta.getPuntaje(),
                pregunta.getOrden(),
                pregunta.getOpciones().stream().map(o -> OpcionResponseDTO.desde(o, true)).toList(),
                respuesta != null ? respuesta.opcionId() : null,
                respuesta != null ? respuesta.texto() : null,
                respuesta != null ? respuesta.puntajeObtenido() : null
        );
    }
}
