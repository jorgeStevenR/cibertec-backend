package com.cibertec.lms.dominio.actividad;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class Entrega {

    private Long id;
    private Long actividadId;
    private Long estudianteId;
    private String nombreArchivo;
    private String rutaArchivo;
    private LocalDateTime fechaEntrega;

    /** Null hasta que el profesor la califique. */
    private Double nota;

    private String comentarioDocente;
    private LocalDateTime fechaCalificacion;
}
