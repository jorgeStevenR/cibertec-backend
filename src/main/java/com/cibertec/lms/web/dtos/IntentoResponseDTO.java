package com.cibertec.lms.web.dtos;

import java.time.LocalDateTime;
import java.util.List;

import com.cibertec.lms.dominio.cuestionario.IntentoCuestionario;
import com.cibertec.lms.dominio.usuario.Usuario;

public record IntentoResponseDTO(
        Long id,
        Long cuestionarioId,
        Long estudianteId,
        String estudianteNombre,
        String estudianteEmail,
        double nota,
        double puntajeMaximo,
        LocalDateTime fechaEntrega,
        List<PreguntaConRespuestaResponseDTO> preguntas
) {
    public static IntentoResponseDTO desde(
            IntentoCuestionario intento, Usuario estudiante, List<PreguntaConRespuestaResponseDTO> preguntas) {
        return new IntentoResponseDTO(
                intento.getId(),
                intento.getCuestionarioId(),
                intento.getEstudianteId(),
                estudiante != null ? estudiante.getNombre() + " " + estudiante.getApellido() : null,
                estudiante != null ? estudiante.getEmail() : null,
                intento.getNota(),
                intento.getPuntajeMaximo(),
                intento.getFechaEntrega(),
                preguntas
        );
    }
}
