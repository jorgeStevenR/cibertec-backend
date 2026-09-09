package com.cibertec.lms.dominio.usuario;

import java.util.List;
import java.util.Optional;

/** Puerto de salida: contrato que debe cumplir cualquier adaptador de persistencia, sin importar la base de datos usada. */
public interface UsuarioRepositorio {

    Usuario guardar(Usuario usuario);

    Optional<Usuario> buscarPorId(Long id);

    Optional<Usuario> buscarPorEmail(String email);

    List<Usuario> listarTodos();

    boolean existePorEmail(String email);

    void eliminar(Long id);
}
