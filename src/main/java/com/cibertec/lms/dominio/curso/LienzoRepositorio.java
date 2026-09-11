package com.cibertec.lms.dominio.curso;

import java.util.List;
import java.util.Optional;

public interface LienzoRepositorio {

    Lienzo guardar(Lienzo lienzo);

    Optional<Lienzo> buscarPorId(Long id);

    List<Lienzo> listarPorModulo(Long moduloId);

    void eliminar(Long id);
}
