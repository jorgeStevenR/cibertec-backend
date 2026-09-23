package com.cibertec.lms.web.dtos;

import java.time.LocalDateTime;

import com.cibertec.lms.dominio.matricula.Matricula;
import com.cibertec.lms.dominio.usuario.Usuario;

public record MatriculaResponseDTO(
        Long id,
        Long cursoId,
        Long estudianteId,
        String estudianteNombre,
        String estudianteEmail,
        LocalDateTime fechaMatricula,
        LocalDateTime fechaUltimoAcceso,
        LocalDateTime estudianteUltimoLogin
) {
    public static MatriculaResponseDTO desde(Matricula matricula, Usuario estudiante) {
        return new MatriculaResponseDTO(
                matricula.getId(),
                matricula.getCursoId(),
                matricula.getEstudianteId(),
                estudiante != null ? estudiante.getNombre() + " " + estudiante.getApellido() : null,
                estudiante != null ? estudiante.getEmail() : null,
                matricula.getFechaMatricula(),
                matricula.getFechaUltimoAcceso(),
                estudiante != null ? estudiante.getFechaUltimoLogin() : null
        );
    }
}
