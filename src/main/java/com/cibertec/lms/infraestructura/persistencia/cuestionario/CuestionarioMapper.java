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
                // Los cuestionarios anteriores a este campo mostraban la nota: que sigan igual.
                .mostrarNota(entidad.getMostrarNota() == null || entidad.getMostrarNota())
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
        entidad.setMostrarNota(cuestionario.isMostrarNota());
        entidad.setFechaCreacion(cuestionario.getFechaCreacion());
        return entidad;
    }
}
