package com.cibertec.lms.dominio.curso;

import java.util.Optional;

import com.cibertec.lms.dominio.comun.Pagina;

public interface CursoRepositorio {

    Curso guardar(Curso curso);

    Optional<Curso> buscarPorId(Long id);

    /** profesorId es opcional (null = sin filtro). */
    Pagina<Curso> listarPaginado(int pagina, int tamano, Long profesorId);

    void eliminar(Long id);
}
