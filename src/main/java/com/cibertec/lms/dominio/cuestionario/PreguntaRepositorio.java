package com.cibertec.lms.dominio.cuestionario;

import java.util.List;
import java.util.Optional;

public interface PreguntaRepositorio {

    /** Persiste la pregunta y hace un reemplazo total de sus opciones (upsert por id + borra las que ya no vienen). */
    Pregunta guardar(Pregunta pregunta);

    Optional<Pregunta> buscarPorId(Long id);

    /** Ordenadas por "orden" ascendente. */
    List<Pregunta> listarPorCuestionario(Long cuestionarioId);

    void eliminar(Long id);
}
