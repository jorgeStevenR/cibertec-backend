package com.cibertec.lms.infraestructura.persistencia.estudiante;

import java.time.LocalDateTime;

import com.cibertec.lms.dominio.cuestionario.EstadoIntento;

/** Proyección de una consulta JPQL (constructor expression); public porque Hibernate la instancia por reflexión.
 *  {@code puntajeMaximoActual} es Number porque SUM sobre un int de la entidad lo devuelve como Long. */
public record CuestionarioAvanceFila(
        Long id,
        String titulo,
        LocalDateTime fechaLimite,
        Number puntajeMaximoActual,
        LocalDateTime fechaCreacion,
        Long moduloId,
        String moduloTitulo,
        Integer moduloOrden,
        Long cursoId,
        LocalDateTime fechaRendicion,
        Double nota,
        Double puntajeMaximoIntento,
        EstadoIntento estadoIntento,
        Boolean mostrarNota
) {
}
