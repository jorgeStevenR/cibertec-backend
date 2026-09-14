package com.cibertec.lms.web.dtos;

import java.time.LocalDateTime;
import java.util.List;

import com.cibertec.lms.dominio.cuestionario.Cuestionario;
import com.fasterxml.jackson.annotation.JsonInclude;

/** "preguntas" viaja null (y se omite del JSON) en el listado del módulo; solo se completa en el
 *  detalle de un cuestionario puntual - y, si quien pregunta es un estudiante y el cuestionario
 *  todavía no abrió, viaja como una lista vacía en vez de null (no se omite: confirma que no hay
 *  nada que mostrar todavía, en vez de parecer un campo no soportado). */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record CuestionarioResponseDTO(
        Long id,
        Long moduloId,
        String titulo,
        String descripcion,
        LocalDateTime fechaApertura,
        LocalDateTime fechaEntrega,
        Integer tiempoLimiteMinutos,
        boolean disponible,
        double puntajeMaximo,
        LocalDateTime fechaCreacion,
        List<PreguntaResponseDTO> preguntas
) {
    public static CuestionarioResponseDTO resumen(Cuestionario cuestionario, boolean disponible, double puntajeMaximo) {
        return new CuestionarioResponseDTO(
                cuestionario.getId(), cuestionario.getModuloId(), cuestionario.getTitulo(),
                cuestionario.getDescripcion(), cuestionario.getFechaApertura(), cuestionario.getFechaEntrega(),
                cuestionario.getTiempoLimiteMinutos(), disponible, puntajeMaximo, cuestionario.getFechaCreacion(), null);
    }

    public static CuestionarioResponseDTO detalle(
            Cuestionario cuestionario, boolean disponible, double puntajeMaximo, List<PreguntaResponseDTO> preguntas) {
        return new CuestionarioResponseDTO(
                cuestionario.getId(), cuestionario.getModuloId(), cuestionario.getTitulo(),
                cuestionario.getDescripcion(), cuestionario.getFechaApertura(), cuestionario.getFechaEntrega(),
                cuestionario.getTiempoLimiteMinutos(), disponible, puntajeMaximo, cuestionario.getFechaCreacion(), preguntas);
    }
}
