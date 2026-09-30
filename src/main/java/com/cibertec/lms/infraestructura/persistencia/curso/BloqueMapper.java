package com.cibertec.lms.infraestructura.persistencia.curso;

import com.cibertec.lms.dominio.curso.BloqueContenido;

class BloqueMapper {

    private BloqueMapper() {
    }

    static BloqueContenido aDominio(BloqueJpaEntity entidad) {
        return BloqueContenido.builder()
                .id(entidad.getId())
                .moduloId(entidad.getModuloId())
                .paginaId(entidad.getPaginaId())
                .tipo(entidad.getTipo())
                .texto(entidad.getTexto())
                .rutaArchivo(entidad.getRutaArchivo())
                .nombreArchivo(entidad.getNombreArchivo())
                .urlVideo(entidad.getUrlVideo())
                .orden(entidad.getOrden())
                .fechaCreacion(entidad.getFechaCreacion())
                .build();
    }

    static BloqueJpaEntity aEntidad(BloqueContenido bloque) {
        BloqueJpaEntity entidad = new BloqueJpaEntity();
        entidad.setId(bloque.getId());
        entidad.setModuloId(bloque.getModuloId());
        entidad.setPaginaId(bloque.getPaginaId());
        entidad.setTipo(bloque.getTipo());
        entidad.setTexto(bloque.getTexto());
        entidad.setRutaArchivo(bloque.getRutaArchivo());
        entidad.setNombreArchivo(bloque.getNombreArchivo());
        entidad.setUrlVideo(bloque.getUrlVideo());
        entidad.setOrden(bloque.getOrden());
        entidad.setFechaCreacion(bloque.getFechaCreacion());
        return entidad;
    }
}
