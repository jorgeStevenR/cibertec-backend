package com.cibertec.lms.dominio.cuestionario;

import java.util.List;
import java.util.Optional;

public interface CuestionarioRepositorio {

    Cuestionario guardar(Cuestionario cuestionario);

    Optional<Cuestionario> buscarPorId(Long id);

    List<Cuestionario> listarPorModulo(Long moduloId);

    void eliminar(Long id);
}
