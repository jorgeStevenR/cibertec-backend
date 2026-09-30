package com.cibertec.lms.dominio.curso;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/** Recurso "Página" de un módulo (como en Moodle): un título con bloques de contenido apilados adentro. */
@Getter
@Builder
@AllArgsConstructor
public class PaginaModulo {

    private Long id;
    private Long moduloId;
    private String titulo;
    private int orden;
    private LocalDateTime fechaCreacion;
}
