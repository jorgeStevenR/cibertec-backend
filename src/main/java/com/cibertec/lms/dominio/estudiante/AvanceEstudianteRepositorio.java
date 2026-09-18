package com.cibertec.lms.dominio.estudiante;

import java.util.Collection;
import java.util.List;

/** Lecturas agregadas para el panel del estudiante. Cada método es una única consulta, sin importar cuántos cursos
 *  o ítems haya, para que los servicios no caigan en N+1. */
public interface AvanceEstudianteRepositorio {

    List<CursoDeEstudiante> listarCursosPublicadosMatriculados(Long estudianteId);

    List<ActividadDeEstudiante> listarActividades(Long estudianteId, Collection<Long> cursoIds);

    List<CuestionarioDeEstudiante> listarCuestionarios(Long estudianteId, Collection<Long> cursoIds);
}
