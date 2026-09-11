package com.cibertec.lms.dominio.actividad;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class Actividad {

    private Long id;
    private Long moduloId;
    private String titulo;
    private String descripcion;

    /** Puede ser null si la actividad no tiene fecha límite. */
    private LocalDateTime fechaEntrega;

    private double puntajeMaximo;
    private LocalDateTime fechaCreacion;
}
