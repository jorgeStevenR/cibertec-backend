package com.cibertec.lms.dominio.estudiante;

import java.time.LocalDate;

/** Curso publicado en el que el estudiante está matriculado. Las fechas son null en cursos anteriores a ese dato. */
public record CursoDeEstudiante(Long cursoId, String titulo, LocalDate fechaInicio, LocalDate fechaFin) {
}
