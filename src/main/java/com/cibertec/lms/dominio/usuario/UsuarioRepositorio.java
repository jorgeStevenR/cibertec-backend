package com.cibertec.lms.dominio.usuario;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

import com.cibertec.lms.dominio.comun.Pagina;

/** Puerto de salida: contrato que debe cumplir cualquier adaptador de persistencia, sin importar la base de datos usada. */
public interface UsuarioRepositorio {

    Usuario guardar(Usuario usuario);

    Optional<Usuario> buscarPorId(Long id);

    /** Una sola consulta agrupada; los ids que no existen no aparecen en el mapa. Evita N+1 al resolver nombre/email en listados. */
    Map<Long, Usuario> buscarPorIds(Collection<Long> ids);

    Optional<Usuario> buscarPorEmail(String email);

    /** busqueda es opcional (null = sin filtro); compara contra nombre, apellido y email. */
    Pagina<Usuario> listarPaginado(int pagina, int tamano, String busqueda);

    boolean existePorEmail(String email);

    void eliminar(Long id);
}
