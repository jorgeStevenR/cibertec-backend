package com.cibertec.lms.dominio.estudiante;

import java.time.LocalDate;
import java.util.List;

public record CalificacionesCurso(
        Long cursoId,
        String cursoTitulo,
        double puntajeObtenido,
        double puntajeMaximoCalificado,
        List<ItemCalificacion> items,
        LocalDate fechaInicio,
        LocalDate fechaFin
) {
}
