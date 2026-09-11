package com.cibertec.lms.web.dtos;

import java.time.LocalDateTime;

import com.cibertec.lms.dominio.curso.Curso;
import com.cibertec.lms.dominio.usuario.Usuario;

public record CursoResponseDTO(
        Long id,
        String titulo,
        String descripcion,
        Long profesorId,
        String profesorNombre,
        String profesorEmail,
        boolean publicado,
        LocalDateTime fechaCreacion
) {
    public static CursoResponseDTO desde(Curso curso, Usuario profesor) {
        return new CursoResponseDTO(
                curso.getId(),
                curso.getTitulo(),
                curso.getDescripcion(),
                curso.getProfesorId(),
                profesor != null ? profesor.getNombre() + " " + profesor.getApellido() : null,
                profesor != null ? profesor.getEmail() : null,
                curso.isPublicado(),
                curso.getFechaCreacion()
        );
    }
}
