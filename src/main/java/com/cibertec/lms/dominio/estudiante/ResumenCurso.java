package com.cibertec.lms.dominio.estudiante;

import java.time.LocalDate;

public record ResumenCurso(
        Long cursoId,
        int totalActividades,
        int actividadesEntregadas,
        int actividadesPendientes,
        int totalCuestionarios,
        int cuestionariosRendidos,
        int cuestionariosPendientes,
        int porcentajeCompletado,
        LocalDate fechaInicio,
        LocalDate fechaFin
) {
}
