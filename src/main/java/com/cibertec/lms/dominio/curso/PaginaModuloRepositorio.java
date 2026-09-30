package com.cibertec.lms.dominio.curso;

import java.util.List;
import java.util.Optional;

public interface PaginaModuloRepositorio {

    PaginaModulo guardar(PaginaModulo pagina);

    Optional<PaginaModulo> buscarPorId(Long id);

    List<PaginaModulo> listarPorModulo(Long moduloId);

    void eliminar(Long id);
}
