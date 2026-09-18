package com.cibertec.lms.dominio.estudiante;

import java.time.LocalDateTime;

/** Una actividad de un curso del estudiante junto con la entrega que él mismo hizo (campos de entrega en null si no entregó). */
public record ActividadDeEstudiante(
        Long id,
        String titulo,
        LocalDateTime fechaLimite,
        double puntajeMaximo,
        LocalDateTime fechaCreacion,
        Long moduloId,
        String moduloTitulo,
        int moduloOrden,
        Long cursoId,
        LocalDateTime fechaEntrega,
        Double nota,
        LocalDateTime fechaCalificacion,
        String comentarioDocente
) {
    public boolean entregada() {
        return fechaEntrega != null;
    }
}
