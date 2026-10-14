package com.cibertec.lms.web.dtos;

import java.time.LocalDateTime;
import java.util.List;

import com.cibertec.lms.dominio.cuestionario.EstadoIntento;
import com.cibertec.lms.dominio.cuestionario.IntentoCuestionario;
import com.cibertec.lms.dominio.usuario.Usuario;

public record IntentoResponseDTO(
        Long id,
        Long cuestionarioId,
        Long estudianteId,
        String estudianteNombre,
        String estudianteEmail,
        /** Null cuando se le muestra al estudiante un intento PENDIENTE_REVISION: la nota todavía es parcial. */
        Double nota,
        double puntajeMaximo,
        EstadoIntento estado,
        LocalDateTime fechaEntrega,
        List<PreguntaConRespuestaResponseDTO> preguntas
) {
    public static IntentoResponseDTO desde(
            IntentoCuestionario intento, Usuario estudiante, List<PreguntaConRespuestaResponseDTO> preguntas,
            boolean ocultarNotaParcial) {
        return new IntentoResponseDTO(
                intento.getId(),
                intento.getCuestionarioId(),
                intento.getEstudianteId(),
                estudiante != null ? estudiante.getNombre() + " " + estudiante.getApellido() : null,
                estudiante != null ? estudiante.getEmail() : null,
                ocultarNotaParcial && intento.pendienteDeRevision() ? null : intento.getNota(),
                intento.getPuntajeMaximo(),
                intento.getEstado(),
                intento.getFechaEntrega(),
                preguntas
        );
    }
}
