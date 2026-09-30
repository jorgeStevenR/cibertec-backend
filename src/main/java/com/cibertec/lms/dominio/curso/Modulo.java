package com.cibertec.lms.dominio.curso;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class Modulo {

    private Long id;
    private Long cursoId;
    private String titulo;
    private int orden;
    private Long moduloPadreId;
}
