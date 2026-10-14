package com.cibertec.lms.dominio.cuestionario;

import java.util.List;
import java.util.Optional;

public interface IntentoCuestionarioRepositorio {

    IntentoCuestionario guardar(IntentoCuestionario intento);

    Optional<IntentoCuestionario> buscarPorId(Long id);

    Optional<IntentoCuestionario> buscarPorCuestionarioYEstudiante(Long cuestionarioId, Long estudianteId);

    boolean existePorCuestionarioYEstudiante(Long cuestionarioId, Long estudianteId);

    List<IntentoCuestionario> listarPorCuestionario(Long cuestionarioId);

    /** Guarda el puntaje de una respuesta abierta junto con la nota y el estado recalculados del intento, en una sola transacción. */
    IntentoCuestionario calificarRespuesta(Long intentoId, Long preguntaId, double puntajeObtenido, double nota, EstadoIntento estado);

    void eliminarPorCuestionario(Long cuestionarioId);
}
