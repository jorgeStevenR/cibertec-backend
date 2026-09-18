package com.cibertec.lms.dominio.curso;

import java.time.LocalDate;
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

    /** Código único del curso, siempre en mayúsculas y sin espacios en los extremos. Null solo en cursos anteriores a este campo. */
    private String codigo;

    /** Período académico "AAAA-N" (ej. "2026-2"). Null solo en cursos anteriores a este campo. */
    private String periodo;

    /** Null = sin límite de estudiantes. */
    private Integer cupoMaximo;

    /** Fechas de inicio y fin del curso. Null solo en cursos anteriores a estos campos. */
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
}
