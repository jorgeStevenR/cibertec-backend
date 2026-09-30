package com.cibertec.lms.infraestructura.persistencia.curso;

import com.cibertec.lms.dominio.curso.PaginaModulo;

class PaginaModuloMapper {

    private PaginaModuloMapper() {
    }

    static PaginaModulo aDominio(PaginaModuloJpaEntity entidad) {
        return PaginaModulo.builder()
                .id(entidad.getId())
                .moduloId(entidad.getModuloId())
                .titulo(entidad.getTitulo())
                .orden(entidad.getOrden())
                .fechaCreacion(entidad.getFechaCreacion())
                .build();
    }

    static PaginaModuloJpaEntity aEntidad(PaginaModulo pagina) {
        PaginaModuloJpaEntity entidad = new PaginaModuloJpaEntity();
        entidad.setId(pagina.getId());
        entidad.setModuloId(pagina.getModuloId());
        entidad.setTitulo(pagina.getTitulo());
        entidad.setOrden(pagina.getOrden());
        entidad.setFechaCreacion(pagina.getFechaCreacion());
        return entidad;
    }
}
