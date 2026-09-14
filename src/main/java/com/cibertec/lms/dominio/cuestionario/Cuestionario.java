package com.cibertec.lms.dominio.cuestionario;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class Cuestionario {

    private Long id;
    private Long moduloId;
    private String titulo;
    private String descripcion;

    /** Puede ser null si el cuestionario está disponible desde que se crea. */
    private LocalDateTime fechaApertura;

    /** Puede ser null si el cuestionario no tiene fecha límite. */
    private LocalDateTime fechaEntrega;

    /** Informativo por ahora: no hay cronómetro que corte el intento automáticamente. Puede ser null. */
    private Integer tiempoLimiteMinutos;

    private LocalDateTime fechaCreacion;
}
