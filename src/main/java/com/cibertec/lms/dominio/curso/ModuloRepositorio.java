package com.cibertec.lms.dominio.curso;

import java.util.List;
import java.util.Optional;

public interface ModuloRepositorio {

    Modulo guardar(Modulo modulo);

    Optional<Modulo> buscarPorId(Long id);

    /** Todos los módulos del curso (raíces y submódulos), ordenados por (padre, orden). */
    List<Modulo> listarPorCurso(Long cursoId);

    /** Submódulos directos de ese módulo (lista vacía si no tiene). */
    List<Modulo> listarHijos(Long moduloPadreId);

    void eliminar(Long id);
}
