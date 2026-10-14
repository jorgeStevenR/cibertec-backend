package com.cibertec.lms.dominio.cuestionario;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class Pregunta {

    private Long id;
    private Long cuestionarioId;
    private TipoPregunta tipo;
    private String enunciado;
    private int puntaje;
    private int orden;
    /** Vacía en preguntas ABIERTA. */
    private List<Opcion> opciones;
}
