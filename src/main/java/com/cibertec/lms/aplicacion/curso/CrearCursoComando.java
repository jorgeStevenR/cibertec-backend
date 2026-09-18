package com.cibertec.lms.aplicacion.curso;

import java.time.LocalDate;

/** Datos para crear un curso. El resto (publicado, fecha de creación) lo decide el servicio. */
public record CrearCursoComando(
        String titulo,
        String descripcion,
        Long profesorId,
        Long programaId,
        String codigo,
        String periodo,
        Integer cupoMaximo,
        LocalDate fechaInicio,
        LocalDate fechaFin
) {
}
