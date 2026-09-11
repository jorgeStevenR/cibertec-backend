package com.cibertec.lms.dominio.matricula;

import java.util.List;
import java.util.Optional;

public interface MatriculaProgramaRepositorio {

    MatriculaPrograma guardar(MatriculaPrograma matricula);

    Optional<MatriculaPrograma> buscarPorId(Long id);

    List<MatriculaPrograma> listarPorEstudiante(Long estudianteId);

    List<MatriculaPrograma> listarPorPrograma(Long programaId);

    boolean existePorProgramaYEstudiante(Long programaId, Long estudianteId);

    void eliminar(Long id);
}
