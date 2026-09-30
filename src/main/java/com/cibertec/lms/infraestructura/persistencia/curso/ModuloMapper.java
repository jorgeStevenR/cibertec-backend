package com.cibertec.lms.infraestructura.persistencia.curso;

import com.cibertec.lms.dominio.curso.Modulo;

class ModuloMapper {

    private ModuloMapper() {
    }

    static Modulo aDominio(ModuloJpaEntity entidad) {
        return Modulo.builder()
                .id(entidad.getId())
                .cursoId(entidad.getCursoId())
                .titulo(entidad.getTitulo())
                .orden(entidad.getOrden())
                .moduloPadreId(entidad.getModuloPadreId())
                .build();
    }

    static ModuloJpaEntity aEntidad(Modulo modulo) {
        ModuloJpaEntity entidad = new ModuloJpaEntity();
        entidad.setId(modulo.getId());
        entidad.setCursoId(modulo.getCursoId());
        entidad.setTitulo(modulo.getTitulo());
        entidad.setOrden(modulo.getOrden());
        entidad.setModuloPadreId(modulo.getModuloPadreId());
        return entidad;
    }
}
