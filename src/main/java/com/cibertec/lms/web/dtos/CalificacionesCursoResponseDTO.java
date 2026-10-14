package com.cibertec.lms.web.dtos;

import java.time.LocalDate;
import java.util.List;

import com.cibertec.lms.dominio.comun.Calificacion;
import com.cibertec.lms.dominio.estudiante.CalificacionesCurso;

public record CalificacionesCursoResponseDTO(
        Long cursoId,
        String cursoTitulo,
        double puntajeObtenido,
        double puntajeMaximoCalificado,
        /** Total del curso en escala 0,0–5,0 sobre lo ya calificado; null si todavía no hay nada calificado. */
        Double notaSobre5,
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
                Calificacion.sobre5(calificaciones.puntajeObtenido(), calificaciones.puntajeMaximoCalificado()),
                calificaciones.items().stream().map(CalificacionItemResponseDTO::desde).toList(),
                calificaciones.fechaInicio(),
                calificaciones.fechaFin()
        );
    }
}
