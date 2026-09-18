package com.cibertec.lms.dominio.estudiante;

import java.time.LocalDateTime;

public record ItemCalificacion(
        TipoItem tipo,
        Long id,
        String titulo,
        Long moduloId,
        String moduloTitulo,
        double puntajeMaximo,
        Double nota,
        EstadoCalificacion estado,
        LocalDateTime fechaEntrega,
        LocalDateTime fechaCalificacion,
        String comentarioDocente
) {
    /** Calificado = tiene nota: una actividad ya corregida o un cuestionario rendido (se autocalifica al enviarlo). */
    public boolean calificado() {
        return nota != null;
    }
}
