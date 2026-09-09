package com.cibertec.lms.dominio.usuario;

import java.util.Optional;

import com.cibertec.lms.dominio.comun.Pagina;

/** Puerto de salida: contrato que debe cumplir cualquier adaptador de persistencia, sin importar la base de datos usada. */
public interface UsuarioRepositorio {

    Usuario guardar(Usuario usuario);

    Optional<Usuario> buscarPorId(Long id);

    Optional<Usuario> buscarPorEmail(String email);

    /** busqueda es opcional (null = sin filtro); compara contra nombre, apellido y email. */
    Pagina<Usuario> listarPaginado(int pagina, int tamano, String busqueda);

    boolean existePorEmail(String email);

    void eliminar(Long id);
}
