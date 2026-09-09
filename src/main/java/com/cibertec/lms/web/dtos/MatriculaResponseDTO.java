package com.cibertec.lms.web.dtos;

import java.time.LocalDateTime;

import com.cibertec.lms.dominio.matricula.Matricula;

public record MatriculaResponseDTO(
        Long id,
        Long cursoId,
        Long estudianteId,
        LocalDateTime fechaMatricula
) {
    public static MatriculaResponseDTO desde(Matricula matricula) {
        return new MatriculaResponseDTO(
                matricula.getId(),
                matricula.getCursoId(),
                matricula.getEstudianteId(),
                matricula.getFechaMatricula()
        );
    }
}
