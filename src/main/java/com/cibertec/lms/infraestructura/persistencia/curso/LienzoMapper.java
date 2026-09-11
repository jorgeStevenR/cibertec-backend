package com.cibertec.lms.infraestructura.persistencia.curso;

import com.cibertec.lms.dominio.curso.Lienzo;

class LienzoMapper {

    private LienzoMapper() {
    }

    static Lienzo aDominio(LienzoJpaEntity entidad) {
        return Lienzo.builder()
                .id(entidad.getId())
                .moduloId(entidad.getModuloId())
                .nombre(entidad.getNombre())
                .orden(entidad.getOrden())
                .ancho(entidad.getAncho())
                .alto(entidad.getAlto())
                .elementosJson(entidad.getElementos())
                .fechaCreacion(entidad.getFechaCreacion())
                .fechaActualizacion(entidad.getFechaActualizacion())
                .build();
    }

    static LienzoJpaEntity aEntidad(Lienzo lienzo) {
        LienzoJpaEntity entidad = new LienzoJpaEntity();
        entidad.setId(lienzo.getId());
        entidad.setModuloId(lienzo.getModuloId());
        entidad.setNombre(lienzo.getNombre());
        entidad.setOrden(lienzo.getOrden());
        entidad.setAncho(lienzo.getAncho());
        entidad.setAlto(lienzo.getAlto());
        entidad.setElementos(lienzo.getElementosJson());
        entidad.setFechaCreacion(lienzo.getFechaCreacion());
        entidad.setFechaActualizacion(lienzo.getFechaActualizacion());
        return entidad;
    }
}
