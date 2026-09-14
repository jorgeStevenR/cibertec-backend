package com.cibertec.lms.dominio.cuestionario;

import java.util.List;
import java.util.Optional;

public interface IntentoCuestionarioRepositorio {

    IntentoCuestionario guardar(IntentoCuestionario intento);

    Optional<IntentoCuestionario> buscarPorCuestionarioYEstudiante(Long cuestionarioId, Long estudianteId);

    boolean existePorCuestionarioYEstudiante(Long cuestionarioId, Long estudianteId);

    List<IntentoCuestionario> listarPorCuestionario(Long cuestionarioId);

    void eliminarPorCuestionario(Long cuestionarioId);
}
