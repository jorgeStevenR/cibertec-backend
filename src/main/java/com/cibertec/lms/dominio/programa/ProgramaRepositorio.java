package com.cibertec.lms.dominio.programa;

import java.util.Optional;

import com.cibertec.lms.dominio.comun.Pagina;

public interface ProgramaRepositorio {

    Programa guardar(Programa programa);

    Optional<Programa> buscarPorId(Long id);

    /** busqueda es opcional (null/blank = sin filtro), busca en nombre y descripcion. */
    Pagina<Programa> listarPaginado(int pagina, int tamano, String busqueda);

    void eliminar(Long id);
}
