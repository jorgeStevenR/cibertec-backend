package com.cibertec.lms.web.dtos;

import java.time.LocalDateTime;

import com.cibertec.lms.dominio.cuestionario.EstadoIntento;
import com.cibertec.lms.dominio.cuestionario.IntentoCuestionario;
import com.cibertec.lms.dominio.usuario.Usuario;

public record IntentoResumenResponseDTO(
        Long id,
        Long cuestionarioId,
        Long estudianteId,
        String estudianteNombre,
        String estudianteEmail,
        double nota,
        double puntajeMaximo,
        EstadoIntento estado,
        LocalDateTime fechaEntrega
) {
    public static IntentoResumenResponseDTO desde(IntentoCuestionario intento, Usuario estudiante) {
        return new IntentoResumenResponseDTO(
                intento.getId(),
                intento.getCuestionarioId(),
                intento.getEstudianteId(),
                estudiante != null ? estudiante.getNombre() + " " + estudiante.getApellido() : null,
                estudiante != null ? estudiante.getEmail() : null,
                intento.getNota(),
                intento.getPuntajeMaximo(),
                intento.getEstado(),
                intento.getFechaEntrega()
        );
    }
}
