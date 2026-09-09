package com.cibertec.lms.dominio.curso;

import java.util.List;
import java.util.Optional;

public interface ModuloRepositorio {

    Modulo guardar(Modulo modulo);

    Optional<Modulo> buscarPorId(Long id);

    List<Modulo> listarPorCurso(Long cursoId);

    void eliminar(Long id);
}
