package com.cibertec.lms.dominio.curso;

import java.util.Optional;

import com.cibertec.lms.dominio.comun.Pagina;

public interface CursoRepositorio {

    Curso guardar(Curso curso);

    Optional<Curso> buscarPorId(Long id);

    /** profesorId y programaId son opcionales (null = sin filtro); si ambos vienen, profesorId tiene prioridad. */
    Pagina<Curso> listarPaginado(int pagina, int tamano, Long profesorId, Long programaId);

    boolean existePorProgramaId(Long programaId);

    void eliminar(Long id);
}
