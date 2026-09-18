package com.cibertec.lms.infraestructura.persistencia.estudiante;

import java.time.LocalDateTime;

/** Proyección de una consulta JPQL (constructor expression); public porque Hibernate la instancia por reflexión.
 *  Tipos envoltorio a propósito: los campos de la entrega vienen null cuando el estudiante no entregó. */
public record ActividadAvanceFila(
        Long id,
        String titulo,
        LocalDateTime fechaLimite,
        Double puntajeMaximo,
        LocalDateTime fechaCreacion,
        Long moduloId,
        String moduloTitulo,
        Integer moduloOrden,
        Long cursoId,
        LocalDateTime fechaEntrega,
        Double nota,
        LocalDateTime fechaCalificacion,
        String comentarioDocente
) {
}
