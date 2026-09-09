package com.cibertec.lms.web.dtos;

import java.time.LocalDateTime;

import com.cibertec.lms.dominio.curso.Curso;

public record CursoResponseDTO(
        Long id,
        String titulo,
        String descripcion,
        Long profesorId,
        boolean publicado,
        LocalDateTime fechaCreacion
) {
    public static CursoResponseDTO desde(Curso curso) {
        return new CursoResponseDTO(
                curso.getId(),
                curso.getTitulo(),
                curso.getDescripcion(),
                curso.getProfesorId(),
                curso.isPublicado(),
                curso.getFechaCreacion()
        );
    }
}
