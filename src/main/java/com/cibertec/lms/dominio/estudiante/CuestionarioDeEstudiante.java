package com.cibertec.lms.dominio.estudiante;

import java.time.LocalDateTime;

/** Un cuestionario de un curso del estudiante junto con su único intento (campos del intento en null si no lo rindió).
 *  {@code puntajeMaximoActual} es la suma actual de puntajes de las preguntas; el intento conserva su propia copia. */
public record CuestionarioDeEstudiante(
        Long id,
        String titulo,
        LocalDateTime fechaLimite,
        double puntajeMaximoActual,
        LocalDateTime fechaCreacion,
        Long moduloId,
        String moduloTitulo,
        int moduloOrden,
        Long cursoId,
        LocalDateTime fechaRendicion,
        Double nota,
        Double puntajeMaximoDelIntento
) {
    public boolean rendido() {
        return fechaRendicion != null;
    }
}
