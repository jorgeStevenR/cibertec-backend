package com.cibertec.lms.web.dtos;

import java.time.LocalDate;

import com.cibertec.lms.dominio.estudiante.ResumenCurso;

public record ResumenCursoResponseDTO(
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
    public static ResumenCursoResponseDTO desde(ResumenCurso resumen) {
        return new ResumenCursoResponseDTO(
                resumen.cursoId(),
                resumen.totalActividades(),
                resumen.actividadesEntregadas(),
                resumen.actividadesPendientes(),
                resumen.totalCuestionarios(),
                resumen.cuestionariosRendidos(),
                resumen.cuestionariosPendientes(),
                resumen.porcentajeCompletado(),
                resumen.fechaInicio(),
                resumen.fechaFin()
        );
    }
}
