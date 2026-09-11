package com.cibertec.lms.dominio.curso;

import java.util.List;
import java.util.Optional;

public interface BloqueRepositorio {

    BloqueContenido guardar(BloqueContenido bloque);

    Optional<BloqueContenido> buscarPorId(Long id);

    List<BloqueContenido> listarPorModulo(Long moduloId);

    void eliminar(Long id);
}
