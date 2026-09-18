package com.cibertec.lms.aplicacion.curso;

import java.time.LocalDate;

/** Datos para actualizar un curso (reemplazo completo). codigo, periodo, cupoMaximo, fechaInicio y fechaFin son datos
 *  administrativos: un ADMINISTRADOR debe enviarlos (menos cupoMaximo, donde null = sin límite); un PROFESOR no puede
 *  cambiarlos, y los que no envíe (null) se conservan tal como están. */
public record ActualizarCursoComando(
        String titulo,
        String descripcion,
        boolean publicado,
        Long programaId,
        String codigo,
        String periodo,
        Integer cupoMaximo,
        LocalDate fechaInicio,
        LocalDate fechaFin
) {
}
