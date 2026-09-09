package com.cibertec.lms.infraestructura.persistencia.curso;

import com.cibertec.lms.dominio.curso.ContenidoModulo;

class ContenidoMapper {

    private ContenidoMapper() {
    }

    static ContenidoModulo aDominio(ContenidoJpaEntity entidad) {
        return ContenidoModulo.builder()
                .id(entidad.getId())
                .moduloId(entidad.getModuloId())
                .tipo(entidad.getTipo())
                .nombre(entidad.getNombre())
                .rutaArchivo(entidad.getRutaArchivo())
                .url(entidad.getUrl())
                .fechaSubida(entidad.getFechaSubida())
                .build();
    }

    static ContenidoJpaEntity aEntidad(ContenidoModulo contenido) {
        ContenidoJpaEntity entidad = new ContenidoJpaEntity();
        entidad.setId(contenido.getId());
        entidad.setModuloId(contenido.getModuloId());
        entidad.setTipo(contenido.getTipo());
        entidad.setNombre(contenido.getNombre());
        entidad.setRutaArchivo(contenido.getRutaArchivo());
        entidad.setUrl(contenido.getUrl());
        entidad.setFechaSubida(contenido.getFechaSubida());
        return entidad;
    }
}
