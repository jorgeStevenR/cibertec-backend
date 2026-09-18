package com.cibertec.lms.web.dtos;

import java.time.LocalDate;
import java.util.List;

import com.cibertec.lms.dominio.estudiante.CalificacionesCurso;

public record CalificacionesCursoResponseDTO(
        Long cursoId,
        String cursoTitulo,
        double puntajeObtenido,
        double puntajeMaximoCalificado,
        List<CalificacionItemResponseDTO> items,
        LocalDate fechaInicio,
        LocalDate fechaFin
) {
    public static CalificacionesCursoResponseDTO desde(CalificacionesCurso calificaciones) {
        return new CalificacionesCursoResponseDTO(
                calificaciones.cursoId(),
                calificaciones.cursoTitulo(),
                calificaciones.puntajeObtenido(),
                calificaciones.puntajeMaximoCalificado(),
                calificaciones.items().stream().map(CalificacionItemResponseDTO::desde).toList(),
                calificaciones.fechaInicio(),
                calificaciones.fechaFin()
        );
    }
}
