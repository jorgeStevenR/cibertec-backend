package com.cibertec.lms.infraestructura.persistencia.programa;

import com.cibertec.lms.dominio.programa.Programa;

class ProgramaMapper {

    private ProgramaMapper() {
    }

    static Programa aDominio(ProgramaJpaEntity entidad) {
        return Programa.builder()
                .id(entidad.getId())
                .nombre(entidad.getNombre())
                .descripcion(entidad.getDescripcion())
                .duracionSemestres(entidad.getDuracionSemestres())
                .fechaCreacion(entidad.getFechaCreacion())
                .build();
    }

    static ProgramaJpaEntity aEntidad(Programa programa) {
        ProgramaJpaEntity entidad = new ProgramaJpaEntity();
        entidad.setId(programa.getId());
        entidad.setNombre(programa.getNombre());
        entidad.setDescripcion(programa.getDescripcion());
        entidad.setDuracionSemestres(programa.getDuracionSemestres());
        entidad.setFechaCreacion(programa.getFechaCreacion());
        return entidad;
    }
}
