package com.cibertec.lms.web.dtos;

import java.time.LocalDateTime;
import java.util.List;

import com.cibertec.lms.dominio.comun.Calificacion;
import com.cibertec.lms.dominio.cuestionario.EstadoIntento;
import com.cibertec.lms.dominio.cuestionario.IntentoCuestionario;
import com.cibertec.lms.dominio.usuario.Usuario;

public record IntentoResponseDTO(
        Long id,
        Long cuestionarioId,
        Long estudianteId,
        String estudianteNombre,
        String estudianteEmail,
        /** En puntos. Al estudiante le llega null si notaVisible es false. */
        Double nota,
        /** nota en escala 0,0–5,0; null cuando nota es null. */
        Double notaSobre5,
        double puntajeMaximo,
        EstadoIntento estado,
        /** mostrarNota del cuestionario && estado CALIFICADO: distingue "nota oculta por el profesor" o "en revisión"
         *  de una nota ya disponible. */
        boolean notaVisible,
        LocalDateTime fechaEntrega,
        /** Al estudiante le llega vacía si el profesor ocultó la nota del cuestionario (mostrarNota = false). */
        List<PreguntaConRespuestaResponseDTO> preguntas
) {
    /** Profesor dueño o admin: siempre todo. */
    public static IntentoResponseDTO paraProfesor(
            IntentoCuestionario intento, Usuario estudiante, List<PreguntaConRespuestaResponseDTO> preguntas,
            boolean mostrarNota) {
        return armar(intento, estudiante, intento.getNota(), intento.notaVisible(mostrarNota), preguntas);
    }

    /** El estudiante solo ve la nota si notaVisible, y solo ve la revisión de sus respuestas si el profesor muestra la
     *  nota del cuestionario: se oculta acá, en el servidor, no se confía en que el frontend no la pinte. */
    public static IntentoResponseDTO paraEstudiante(
            IntentoCuestionario intento, Usuario estudiante, List<PreguntaConRespuestaResponseDTO> preguntas,
            boolean mostrarNota) {
        boolean notaVisible = intento.notaVisible(mostrarNota);
        return armar(intento, estudiante, notaVisible ? intento.getNota() : null, notaVisible,
                mostrarNota ? preguntas : List.of());
    }

    private static IntentoResponseDTO armar(
            IntentoCuestionario intento, Usuario estudiante, Double nota, boolean notaVisible,
            List<PreguntaConRespuestaResponseDTO> preguntas) {
        return new IntentoResponseDTO(
                intento.getId(),
                intento.getCuestionarioId(),
                intento.getEstudianteId(),
                estudiante != null ? estudiante.getNombre() + " " + estudiante.getApellido() : null,
                estudiante != null ? estudiante.getEmail() : null,
                nota,
                Calificacion.sobre5(nota, intento.getPuntajeMaximo()),
                intento.getPuntajeMaximo(),
                intento.getEstado(),
                notaVisible,
                intento.getFechaEntrega(),
                preguntas
        );
    }
}
