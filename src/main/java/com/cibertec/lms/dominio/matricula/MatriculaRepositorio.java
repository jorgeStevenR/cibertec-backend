package com.cibertec.lms.dominio.matricula;

import java.util.List;
import java.util.Optional;

public interface MatriculaRepositorio {

    Matricula guardar(Matricula matricula);

    Optional<Matricula> buscarPorId(Long id);

    List<Matricula> listarPorEstudiante(Long estudianteId);

    List<Matricula> listarPorCurso(Long cursoId);

    boolean existePorCursoYEstudiante(Long cursoId, Long estudianteId);

    void eliminar(Long id);
}
