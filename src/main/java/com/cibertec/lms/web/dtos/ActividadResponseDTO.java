package com.cibertec.lms.web.dtos;

import java.time.LocalDateTime;

import com.cibertec.lms.dominio.actividad.Actividad;

public record ActividadResponseDTO(
        Long id,
        Long moduloId,
        String titulo,
        String descripcion,
        LocalDateTime fechaEntrega,
        double puntajeMaximo,
        LocalDateTime fechaCreacion
) {
    public static ActividadResponseDTO desde(Actividad actividad) {
        return new ActividadResponseDTO(
                actividad.getId(),
                actividad.getModuloId(),
                actividad.getTitulo(),
                actividad.getDescripcion(),
                actividad.getFechaEntrega(),
                actividad.getPuntajeMaximo(),
                actividad.getFechaCreacion()
        );
    }
}
