package com.cibertec.lms.infraestructura.persistencia.actividad;

import com.cibertec.lms.dominio.actividad.Entrega;

class EntregaMapper {

    private EntregaMapper() {
    }

    static Entrega aDominio(EntregaJpaEntity entidad) {
        return Entrega.builder()
                .id(entidad.getId())
                .actividadId(entidad.getActividadId())
                .estudianteId(entidad.getEstudianteId())
                .nombreArchivo(entidad.getNombreArchivo())
                .rutaArchivo(entidad.getRutaArchivo())
                .fechaEntrega(entidad.getFechaEntrega())
                .nota(entidad.getNota())
                .comentarioDocente(entidad.getComentarioDocente())
                .fechaCalificacion(entidad.getFechaCalificacion())
                .build();
    }

    static EntregaJpaEntity aEntidad(Entrega entrega) {
        EntregaJpaEntity entidad = new EntregaJpaEntity();
        entidad.setId(entrega.getId());
        entidad.setActividadId(entrega.getActividadId());
        entidad.setEstudianteId(entrega.getEstudianteId());
        entidad.setNombreArchivo(entrega.getNombreArchivo());
        entidad.setRutaArchivo(entrega.getRutaArchivo());
        entidad.setFechaEntrega(entrega.getFechaEntrega());
        entidad.setNota(entrega.getNota());
        entidad.setComentarioDocente(entrega.getComentarioDocente());
        entidad.setFechaCalificacion(entrega.getFechaCalificacion());
        return entidad;
    }
}
