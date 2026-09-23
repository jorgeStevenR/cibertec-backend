package com.cibertec.lms.dominio.matricula;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface MatriculaRepositorio {

    Matricula guardar(Matricula matricula);

    Optional<Matricula> buscarPorId(Long id);

    List<Matricula> listarPorEstudiante(Long estudianteId);

    List<Matricula> listarPorCurso(Long cursoId);

    boolean existePorCursoYEstudiante(Long cursoId, Long estudianteId);

    int contarPorCurso(Long cursoId);

    /** Una sola consulta agrupada para todos los cursos dados. Los cursos sin matrículas no aparecen en el mapa (el llamador usa 0). */
    Map<Long, Integer> contarPorCursos(Collection<Long> cursoIds);

    void eliminar(Long id);

    /** Actualización puntual (no pasa por guardar/mapper completo) — se llama en cada visita al curso. */
    void registrarAcceso(Long cursoId, Long estudianteId, LocalDateTime fecha);
}
