package com.cibertec.lms.dominio.curso;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class Curso {

    private Long id;
    private String titulo;
    private String descripcion;
    private Long profesorId;

    /** Nullable solo por cursos creados antes de que este campo existiera; en cursos nuevos es obligatorio. */
    private Long programaId;

    private boolean publicado;
    private LocalDateTime fechaCreacion;
}
