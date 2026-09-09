package com.cibertec.lms.dominio.curso;

import java.util.List;
import java.util.Optional;

public interface CursoRepositorio {

    Curso guardar(Curso curso);

    Optional<Curso> buscarPorId(Long id);

    List<Curso> listarTodos();

    List<Curso> listarPorProfesor(Long profesorId);

    void eliminar(Long id);
}
