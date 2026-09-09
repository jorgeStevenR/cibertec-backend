package com.cibertec.lms.dominio.curso;

import java.util.List;
import java.util.Optional;

public interface ContenidoRepositorio {

    ContenidoModulo guardar(ContenidoModulo contenido);

    Optional<ContenidoModulo> buscarPorId(Long id);

    List<ContenidoModulo> listarPorModulo(Long moduloId);

    void eliminar(Long id);
}
