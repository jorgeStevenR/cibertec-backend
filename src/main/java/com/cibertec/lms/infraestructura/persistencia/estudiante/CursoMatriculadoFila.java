package com.cibertec.lms.infraestructura.persistencia.estudiante;

import java.time.LocalDate;

/** Proyección de una consulta JPQL (constructor expression); public porque Hibernate la instancia por reflexión. */
public record CursoMatriculadoFila(Long id, String titulo, LocalDate fechaInicio, LocalDate fechaFin) {
}
