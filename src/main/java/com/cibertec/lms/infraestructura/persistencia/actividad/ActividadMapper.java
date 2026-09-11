package com.cibertec.lms.infraestructura.persistencia.actividad;

import com.cibertec.lms.dominio.actividad.Actividad;

class ActividadMapper {

    private ActividadMapper() {
    }

    static Actividad aDominio(ActividadJpaEntity entidad) {
        return Actividad.builder()
                .id(entidad.getId())
                .moduloId(entidad.getModuloId())
                .titulo(entidad.getTitulo())
                .descripcion(entidad.getDescripcion())
                .fechaEntrega(entidad.getFechaEntrega())
                .puntajeMaximo(entidad.getPuntajeMaximo())
                .fechaCreacion(entidad.getFechaCreacion())
                .build();
    }

    static ActividadJpaEntity aEntidad(Actividad actividad) {
        ActividadJpaEntity entidad = new ActividadJpaEntity();
        entidad.setId(actividad.getId());
        entidad.setModuloId(actividad.getModuloId());
        entidad.setTitulo(actividad.getTitulo());
        entidad.setDescripcion(actividad.getDescripcion());
        entidad.setFechaEntrega(actividad.getFechaEntrega());
        entidad.setPuntajeMaximo(actividad.getPuntajeMaximo());
        entidad.setFechaCreacion(actividad.getFechaCreacion());
        return entidad;
    }
}
