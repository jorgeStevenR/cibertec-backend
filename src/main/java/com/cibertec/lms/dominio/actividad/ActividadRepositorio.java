package com.cibertec.lms.dominio.actividad;

import java.util.List;
import java.util.Optional;

public interface ActividadRepositorio {

    Actividad guardar(Actividad actividad);

    Optional<Actividad> buscarPorId(Long id);

    List<Actividad> listarPorModulo(Long moduloId);

    void eliminar(Long id);
}
