package com.cibertec.lms.dominio.actividad;

import java.util.List;
import java.util.Optional;

public interface EntregaRepositorio {

    Entrega guardar(Entrega entrega);

    Optional<Entrega> buscarPorId(Long id);

    Optional<Entrega> buscarPorActividadYEstudiante(Long actividadId, Long estudianteId);

    List<Entrega> listarPorActividad(Long actividadId);
}
