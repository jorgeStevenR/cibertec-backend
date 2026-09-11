package com.cibertec.lms.web.dtos;

import java.time.LocalDateTime;

import com.cibertec.lms.dominio.matricula.MatriculaPrograma;
import com.cibertec.lms.dominio.usuario.Usuario;

public record MatriculaProgramaResponseDTO(
        Long id,
        Long programaId,
        Long estudianteId,
        String estudianteNombre,
        String estudianteEmail,
        LocalDateTime fechaMatricula
) {
    public static MatriculaProgramaResponseDTO desde(MatriculaPrograma matricula, Usuario estudiante) {
        return new MatriculaProgramaResponseDTO(
                matricula.getId(),
                matricula.getProgramaId(),
                matricula.getEstudianteId(),
                estudiante != null ? estudiante.getNombre() + " " + estudiante.getApellido() : null,
                estudiante != null ? estudiante.getEmail() : null,
                matricula.getFechaMatricula()
        );
    }
}
