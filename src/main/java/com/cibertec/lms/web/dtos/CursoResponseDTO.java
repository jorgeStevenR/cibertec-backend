package com.cibertec.lms.web.dtos;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.cibertec.lms.dominio.curso.Curso;
import com.cibertec.lms.dominio.programa.Programa;
import com.cibertec.lms.dominio.usuario.Usuario;

/** codigo, periodo, cupoMaximo, fechaInicio y fechaFin viajan null en los cursos creados antes de que existieran. */
public record CursoResponseDTO(
        Long id,
        String titulo,
        String descripcion,
        Long profesorId,
        String profesorNombre,
        String profesorEmail,
        Long programaId,
        String programaNombre,
        boolean publicado,
        LocalDateTime fechaCreacion,
        String codigo,
        String periodo,
        Integer cupoMaximo,
        LocalDate fechaInicio,
        LocalDate fechaFin,
        int matriculados
) {
    public static CursoResponseDTO desde(Curso curso, Usuario profesor, Programa programa, int matriculados) {
        return new CursoResponseDTO(
                curso.getId(),
                curso.getTitulo(),
                curso.getDescripcion(),
                curso.getProfesorId(),
                profesor != null ? profesor.getNombre() + " " + profesor.getApellido() : null,
                profesor != null ? profesor.getEmail() : null,
                curso.getProgramaId(),
                programa != null ? programa.getNombre() : null,
                curso.isPublicado(),
                curso.getFechaCreacion(),
                curso.getCodigo(),
                curso.getPeriodo(),
                curso.getCupoMaximo(),
                curso.getFechaInicio(),
                curso.getFechaFin(),
                matriculados
        );
    }
}
