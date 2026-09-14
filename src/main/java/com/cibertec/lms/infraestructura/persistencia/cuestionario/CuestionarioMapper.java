package com.cibertec.lms.infraestructura.persistencia.cuestionario;

import com.cibertec.lms.dominio.cuestionario.Cuestionario;

class CuestionarioMapper {

    private CuestionarioMapper() {
    }

    static Cuestionario aDominio(CuestionarioJpaEntity entidad) {
        return Cuestionario.builder()
                .id(entidad.getId())
                .moduloId(entidad.getModuloId())
                .titulo(entidad.getTitulo())
                .descripcion(entidad.getDescripcion())
                .fechaApertura(entidad.getFechaApertura())
                .fechaEntrega(entidad.getFechaEntrega())
                .tiempoLimiteMinutos(entidad.getTiempoLimiteMinutos())
                .fechaCreacion(entidad.getFechaCreacion())
                .build();
    }

    static CuestionarioJpaEntity aEntidad(Cuestionario cuestionario) {
        CuestionarioJpaEntity entidad = new CuestionarioJpaEntity();
        entidad.setId(cuestionario.getId());
        entidad.setModuloId(cuestionario.getModuloId());
        entidad.setTitulo(cuestionario.getTitulo());
        entidad.setDescripcion(cuestionario.getDescripcion());
        entidad.setFechaApertura(cuestionario.getFechaApertura());
        entidad.setFechaEntrega(cuestionario.getFechaEntrega());
        entidad.setTiempoLimiteMinutos(cuestionario.getTiempoLimiteMinutos());
        entidad.setFechaCreacion(cuestionario.getFechaCreacion());
        return entidad;
    }
}
