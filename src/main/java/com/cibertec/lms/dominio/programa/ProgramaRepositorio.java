package com.cibertec.lms.dominio.programa;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

import com.cibertec.lms.dominio.comun.Pagina;

public interface ProgramaRepositorio {

    Programa guardar(Programa programa);

    Optional<Programa> buscarPorId(Long id);

    /** Una sola consulta agrupada; los ids que no existen no aparecen en el mapa. Evita N+1 al resolver el nombre en listados. */
    Map<Long, Programa> buscarPorIds(Collection<Long> ids);

    /** busqueda es opcional (null/blank = sin filtro), busca en nombre y descripcion. */
    Pagina<Programa> listarPaginado(int pagina, int tamano, String busqueda);

    void eliminar(Long id);
}
