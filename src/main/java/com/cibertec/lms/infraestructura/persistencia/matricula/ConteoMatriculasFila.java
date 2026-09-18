package com.cibertec.lms.infraestructura.persistencia.matricula;

/** Proyección de una consulta JPQL agrupada (constructor expression); public porque Hibernate la instancia por reflexión. */
public record ConteoMatriculasFila(Long cursoId, Long total) {
}
