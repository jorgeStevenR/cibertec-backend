package com.cibertec.lms.dominio.curso;

import java.util.Optional;

import com.cibertec.lms.dominio.comun.Pagina;

public interface CursoRepositorio {

    Curso guardar(Curso curso);

    Optional<Curso> buscarPorId(Long id);

    /** Igual que buscarPorId pero toma un bloqueo de escritura sobre la fila hasta el fin de la transacción en curso; sirve
     *  para serializar operaciones que dependen del estado del curso (ej. comprobar el cupo antes de matricular). */
    Optional<Curso> buscarPorIdBloqueando(Long id);

    /** El código se compara sin distinguir mayúsculas. */
    Optional<Curso> buscarPorCodigo(String codigo);

    boolean existePorCodigo(String codigo);

    /** profesorId y programaId son opcionales (null = sin filtro); si ambos vienen, profesorId tiene prioridad. */
    Pagina<Curso> listarPaginado(int pagina, int tamano, Long profesorId, Long programaId);

    boolean existePorProgramaId(Long programaId);

    void eliminar(Long id);
}
