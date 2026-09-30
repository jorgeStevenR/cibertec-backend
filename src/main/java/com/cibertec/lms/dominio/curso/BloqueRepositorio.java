package com.cibertec.lms.dominio.curso;

import java.util.List;
import java.util.Optional;

public interface BloqueRepositorio {

    BloqueContenido guardar(BloqueContenido bloque);

    Optional<BloqueContenido> buscarPorId(Long id);

    /** Solo los bloques sueltos del módulo (paginaId null); los de sus páginas no se incluyen. */
    List<BloqueContenido> listarPorModulo(Long moduloId);

    List<BloqueContenido> listarPorPagina(Long paginaId);

    void eliminar(Long id);

    void eliminarPorPagina(Long paginaId);
}
